package xerca.xercaomnichest.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.Blocks;

public final class Items {
    public static final Item OMNI_CHEST = Registry.register(BuiltInRegistries.ITEM, Mod.id("omni_chest"), new BlockItem(Blocks.OMNI_CHEST, new Item.Properties()));

    private Items() {
    }

    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> entries.accept(OMNI_CHEST));
    }
}
