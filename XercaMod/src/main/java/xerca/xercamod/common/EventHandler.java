package xerca.xercamod.common;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.packets.ConfigSyncPacket;

@Mod.EventBusSubscriber(modid = XercaMod.MODID)
class EventHandler {
//    @SubscribeEvent
//    public static void playerTickEvent(TickEvent.PlayerTickEvent ev) {
//        if(ev.phase == TickEvent.Phase.START){
//        }
//    }

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
        XercaMod.LOGGER.debug("PlayerLoggedIn Event");
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getPlayer();
        ConfigSyncPacket pack = Config.makePacket();
        XercaMod.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), pack);
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
