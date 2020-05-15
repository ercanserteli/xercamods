package xerca.xercamod.common;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
        if (result == Items.ITEM_GRAB_HOOK) {
            for (int i = 0; i < ev.getInventory().getSizeInventory(); ++i) {
                ItemStack item = ev.getInventory().getStackInSlot(i);
                if (item.getItem() == Items.ITEM_KNIFE) {
                    ev.getInventory().setInventorySlotContents(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }

    private static final ResourceLocation grass = new ResourceLocation("minecraft", "blocks/grass");

    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event)
    {
        // Adding XercaMod seeds to the loot table of grass
        if (event.getName().equals(grass))
        {
            if(Config.isTeaEnabled() && Config.isFoodEnabled()){
                event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(XercaMod.MODID, "blocks/grass"))).build());
            }
            else if(!Config.isTeaEnabled() && Config.isFoodEnabled()){
                event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(XercaMod.MODID, "blocks/grass_tomato_only"))).build());
            }
            else if(Config.isTeaEnabled()){
                event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(XercaMod.MODID, "blocks/grass_tea_only"))).build());
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
}
