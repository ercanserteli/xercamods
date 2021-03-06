package xerca.xercamod.common;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.data.RecipeProvider;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.TableLootEntry;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
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
                ev.getTrades().get(1).add(new BasicTrade(new ItemStack(Items.ITEM_RICE_SEEDS, 24), new ItemStack(net.minecraft.item.Items.EMERALD), 16, 2, 0.05f));
                ev.getTrades().get(1).add(new BasicTrade(new ItemStack(Items.ITEM_TOMATO, 22), new ItemStack(net.minecraft.item.Items.EMERALD), 16, 2, 0.05f));
                ev.getTrades().get(1).add(new BasicTrade(new ItemStack(Items.ITEM_TEA_LEAF, 18), new ItemStack(net.minecraft.item.Items.EMERALD), 16, 2, 0.05f));
            }
        }
        if(Config.isScytheEnabled()){
            if(ev.getType().equals(VillagerProfession.TOOLSMITH)){
                ev.getTrades().get(1).add(new BasicTrade(1, new ItemStack(Items.STONE_SCYTHE), 12, 1, 0.2f));
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
            for (int i = 0; i < ev.getInventory().getSizeInventory(); ++i) {
                ItemStack item = ev.getInventory().getStackInSlot(i);
                if (item.getItem() == Items.ITEM_KNIFE) {
                    ev.getInventory().setInventorySlotContents(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        XercaMod.LOGGER.debug("PlayerLoggedIn Event: Syncing config");
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();
        ConfigSyncPacket pack = Config.makePacket();
        XercaMod.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), pack);

        Triggers.CONFIG_CHECK.test(serverPlayer);

        Collection<IRecipe<?>> recipesToRemove = new ArrayList<>();
        for(IRecipe<?> r : serverPlayer.server.getRecipeManager().getRecipes()){
            if(r.getId().getNamespace().equals(XercaMod.MODID)){
                String path = r.getId().getPath();
                for(String conditionName : Config.conditionMap.keySet()) {
                    if(!Config.conditionMap.get(conditionName).get()){
                        int slash = path.indexOf("/");
                        if(slash > 0){
                            if(conditionName.equals(path.substring(0 , slash)) && serverPlayer.getRecipeBook().isUnlocked(r)){
                                XercaMod.LOGGER.warn("Removing Recipe " + path);
                                recipesToRemove.add(r);
                            }
                        }
                    }
                }
            }
        }
        if(!recipesToRemove.isEmpty()){
            serverPlayer.getRecipeBook().remove(recipesToRemove, serverPlayer);
            for(IRecipe<?> r : recipesToRemove){
                Advancement advancement = serverPlayer.server.getAdvancementManager().getAdvancement(new ResourceLocation(XercaMod.MODID, "recipes/" + r.getId().getPath()));
                if(advancement != null){
                    serverPlayer.getAdvancements().revokeCriterion(advancement, "config");
                    serverPlayer.getAdvancements().revokeCriterion(advancement, "has_item");
                    serverPlayer.getAdvancements().revokeCriterion(advancement, "has_the_recipe");
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickedBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        ItemStack heldItem = event.getPlayer().getHeldItem(event.getHand());
        if(world.getBlockState(event.getPos()).getBlock() == Blocks.IRON_BARS && heldItem.getItem() == net.minecraft.item.Items.MUTTON){
            world.setBlockState(event.getPos(), xerca.xercamod.common.block.Blocks.BLOCK_DONER.getDefaultState());
            heldItem.shrink(1);
            world.playSound(null, event.getPos(), SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 0.8f, 0.9f + world.rand.nextFloat()*0.1f);
        }
    }
}
