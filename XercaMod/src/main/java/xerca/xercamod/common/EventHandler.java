package xerca.xercamod.common;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamod.common.entity.EntityHealthOrb;
import xerca.xercamod.common.item.ItemScythe;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.packets.ConfigSyncPacket;

import java.util.ArrayList;
import java.util.Collection;

@Mod.EventBusSubscriber(modid = XercaMod.MODID)
class EventHandler {

    @SubscribeEvent
    public static void villagerTradesEvent(VillagerTradesEvent ev) {
        if(Config.isFoodEnabled()){
            if(ev.getType().equals(VillagerProfession.FARMER)){
                ev.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.ITEM_RICE_SEEDS, 24), new ItemStack(net.minecraft.world.item.Items.EMERALD), 16, 2, 0.05f));
                ev.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.ITEM_TOMATO, 22), new ItemStack(net.minecraft.world.item.Items.EMERALD), 16, 2, 0.05f));
                ev.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.ITEM_TEA_LEAF, 18), new ItemStack(net.minecraft.world.item.Items.EMERALD), 16, 2, 0.05f));
            }
        }
        if(Config.isScytheEnabled()){
            if(ev.getType().equals(VillagerProfession.TOOLSMITH)){
                ev.getTrades().get(1).add(new BasicItemListing(1, new ItemStack(Items.STONE_SCYTHE), 12, 1, 0.2f));
                ev.getTrades().get(3).add(new EnchantedItemTrade(Items.IRON_SCYTHE, 2, 3, 10, 0.2F));
                ev.getTrades().get(4).add(new EnchantedItemTrade(Items.DIAMOND_SCYTHE, 5, 3, 15, 0.2F));
            }
        }
        if(Config.isWarhammerEnabled()){
            if(ev.getType().equals(VillagerProfession.WEAPONSMITH)){
                ev.getTrades().get(3).add(new EnchantedItemTrade(Items.ITEM_IRON_WARHAMMER, 3, 3, 20, 0.2F));
                ev.getTrades().get(5).add(new EnchantedItemTrade(Items.ITEM_DIAMOND_WARHAMMER, 10, 3, 30, 0.2F));
            }
        }
    }


    @SubscribeEvent
    public static void craftEvent(PlayerEvent.ItemCraftedEvent ev) {
        Item result = ev.getCrafting().getItem();
        // Handling knife usage in recipes
        if (result == Items.ITEM_GRAB_HOOK || result == Items.ITEM_KNIFE || result == Items.CARVING_STATION) {
            for (int i = 0; i < ev.getInventory().getContainerSize(); ++i) {
                ItemStack item = ev.getInventory().getItem(i);
                if (item.getItem() == Items.ITEM_KNIFE) {
                    ev.getInventory().setItem(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        XercaMod.LOGGER.debug("PlayerLoggedIn Event: Syncing config");
        ServerPlayer serverPlayer = (ServerPlayer) event.getPlayer();
        ConfigSyncPacket pack = Config.makePacket();
        XercaMod.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), pack);

        Triggers.CONFIG_CHECK.test(serverPlayer);

        Collection<Recipe<?>> recipesToRemove = new ArrayList<>();
        for(Recipe<?> r : serverPlayer.server.getRecipeManager().getRecipes()){
            if(r.getId().getNamespace().equals(XercaMod.MODID)){
                String path = r.getId().getPath();
                for(String conditionName : Config.conditionMap.keySet()) {
                    if(!Config.conditionMap.get(conditionName).get()){
                        int slash = path.indexOf("/");
                        if(slash > 0){
                            if(conditionName.equals(path.substring(0 , slash)) && serverPlayer.getRecipeBook().contains(r)){
                                XercaMod.LOGGER.warn("Removing Recipe " + path);
                                recipesToRemove.add(r);
                            }
                        }
                    }
                }
            }
        }
        if(!recipesToRemove.isEmpty()){
            serverPlayer.getRecipeBook().removeRecipes(recipesToRemove, serverPlayer);
            for(Recipe<?> r : recipesToRemove){
                Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation(XercaMod.MODID, "recipes/" + r.getId().getPath()));
                if(advancement != null){
                    serverPlayer.getAdvancements().revoke(advancement, "config");
                    serverPlayer.getAdvancements().revoke(advancement, "has_item");
                    serverPlayer.getAdvancements().revoke(advancement, "has_the_recipe");
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickedBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getWorld();
        ItemStack heldItem = event.getPlayer().getItemInHand(event.getHand());
        if(world.getBlockState(event.getPos()).getBlock() == Blocks.IRON_BARS && heldItem.getItem() == net.minecraft.world.item.Items.MUTTON){
            world.setBlockAndUpdate(event.getPos(), xerca.xercamod.common.block.Blocks.BLOCK_DONER.defaultBlockState());
            heldItem.shrink(1);
            world.playSound(null, event.getPos(), SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.8f, 0.9f + world.random.nextFloat()*0.1f);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        DamageSource s = event.getSource();
        if (s.getEntity() instanceof Player attacker) {
            // Handle scythe devour
            if(attacker.getMainHandItem().getItem() instanceof ItemScythe){
                int devourLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_DEVOUR, attacker.getMainHandItem());
                if(devourLevel > 0 && !s.isExplosion() && !s.isFall() && !s.isFire() && !s.isMagic() && !s.isProjectile()){
                    EntityHealthOrb.award((ServerLevel) attacker.level, event.getEntity(), attacker, devourLevel*2);
                }
            }
        }
    }
}
