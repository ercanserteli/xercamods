package xerca.xercamod.common;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLLoginWrapper;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.packets.ConfigSyncPacket;

@Mod.EventBusSubscriber(modid = XercaMod.MODID)
class EventHandler {
    @SubscribeEvent
    public static void craftEvent(PlayerEvent.ItemCraftedEvent ev) {
        Item result = ev.getCrafting().getItem();
        // Handling knife usage in recipes
        if (result == Items.ITEM_TOMATO_SLICES || result == Items.ITEM_POTATO_SLICES || result == Items.ITEM_RAW_PATTY
                || result == Items.ITEM_RAW_SAUSAGE || result == Items.ITEM_RAW_CHICKEN_PATTY || result == Items.ITEM_BUN
                || result == Items.CARVED_OAK_1 || result == Items.CARVED_OAK_2 || result == Items.CARVED_OAK_3
                || result == Items.CARVED_OAK_4 || result == Items.CARVED_OAK_5 || result == Items.CARVED_OAK_6
                || result == Items.CARVED_OAK_7 || result == Items.CARVED_OAK_8 || result == Items.CARVED_BIRCH_1
                || result == Items.CARVED_BIRCH_2 || result == Items.CARVED_BIRCH_3 || result == Items.CARVED_BIRCH_4
                || result == Items.CARVED_BIRCH_5 || result == Items.CARVED_BIRCH_6 || result == Items.CARVED_BIRCH_7
                || result == Items.CARVED_BIRCH_8) {
            for (int i = 0; i < ev.getInventory().getSizeInventory(); ++i) {
                ItemStack item = ev.getInventory().getStackInSlot(i);
                if (item.getItem() == Items.ITEM_KNIFE) {
                    ItemStack newItem = new ItemStack(Items.ITEM_KNIFE, 2);
                    newItem.setDamage(item.getDamage() + 1);
                    ev.getInventory().setInventorySlotContents(i, newItem);
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
