package xerca.xercaomnichest.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.Blocks;

public final class Items {
    public static final Item OMNI_CHEST = Registry.register(BuiltInRegistries.ITEM, Mod.id("omni_chest"), new BlockItem(Blocks.OMNI_CHEST, new Item.Properties().setId(Mod.itemKey("omni_chest")).useBlockDescriptionPrefix()));

    private Items() {
    }

    public static void registerItems() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> entries.accept(OMNI_CHEST));
    }
}
