package xerca.xercafood.common;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xerca.xercafood.common.item.Items;

class EventHandler {
    public static void villagerTradesEvent(VillagerTradesEvent ev) {
        if(ev.getType().equals(VillagerProfession.FARMER)){
            ev.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.ITEM_RICE_SEEDS, 24), new ItemStack(net.minecraft.world.item.Items.EMERALD), 16, 2, 0.05f));
            ev.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.ITEM_TOMATO, 22), new ItemStack(net.minecraft.world.item.Items.EMERALD), 16, 2, 0.05f));
            ev.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.ITEM_TEA_LEAF, 18), new ItemStack(net.minecraft.world.item.Items.EMERALD), 16, 2, 0.05f));
        }
    }

    @SubscribeEvent
    public static void craftEvent(PlayerEvent.ItemCraftedEvent ev) {
        Item result = ev.getCrafting().getItem();
        // Handling knife usage in repairing
        if (result == Items.ITEM_KNIFE) {
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
    public static void onPlayerRightClickedBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getWorld();
        ItemStack heldItem = event.getPlayer().getItemInHand(event.getHand());
        if(world.getBlockState(event.getPos()).getBlock() == Blocks.IRON_BARS && heldItem.getItem() == net.minecraft.world.item.Items.MUTTON){
            world.setBlockAndUpdate(event.getPos(), xerca.xercafood.common.block.Blocks.BLOCK_DONER.defaultBlockState());
            heldItem.shrink(1);
            world.playSound(null, event.getPos(), SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.8f, 0.9f + world.random.nextFloat()*0.1f);
        }
    }
}
