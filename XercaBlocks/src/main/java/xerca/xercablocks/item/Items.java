package xerca.xercablocks.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import xerca.xercablocks.Mod;
import xerca.xercablocks.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public final class Items {
    public static final Item BLOCK_LEATHER = new BlockItem(Blocks.BLOCK_LEATHER, new Item.Properties());
    public static final Item BLOCK_STRAW = new BlockItem(Blocks.BLOCK_STRAW, new Item.Properties());
    public static final Item BOOKCASE = new BlockItem(Blocks.BLOCK_BOOKCASE, new Item.Properties());
    public static final Item ROPE = new RopeItem(Blocks.ROPE, new Item.Properties());
    public static final Item CARVING_STATION = new BlockItem(Blocks.CARVING_STATION, new Item.Properties());
    private static final List<Item> BUILDING_TAB_ITEMS = new ArrayList<>();
    private static final List<Item> COLORED_TAB_ITEMS = new ArrayList<>();

    private Items() {
    }

    public static void registerItems() {
        register("block_leather", BLOCK_LEATHER);
        register("block_straw", BLOCK_STRAW);
        register("bookcase", BOOKCASE);
        register("rope", ROPE);
        register("carving_station", CARVING_STATION);
        Blocks.carvedWoods().forEach(Items::registerBuildingBlockItem);
        Blocks.terratiles().forEach(Items::registerColoredBlockItem);
        Blocks.terratileSlabs().forEach(Items::registerColoredBlockItem);
        Blocks.terratileStairs().forEach(Items::registerColoredBlockItem);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(BOOKCASE);
            entries.accept(ROPE);
            entries.accept(CARVING_STATION);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            entries.accept(BLOCK_LEATHER);
            entries.accept(BLOCK_STRAW);
            BUILDING_TAB_ITEMS.forEach(entries::accept);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries ->
                COLORED_TAB_ITEMS.forEach(entries::accept));
    }

    private static void registerBuildingBlockItem(String id, Block block) {
        Item item = new BlockItem(block, new Item.Properties());
        register(id, item);
        BUILDING_TAB_ITEMS.add(item);
    }

    private static void registerColoredBlockItem(String id, Block block) {
        Item item = new BlockItem(block, new Item.Properties());
        register(id, item);
        COLORED_TAB_ITEMS.add(item);
    }

    private static void register(String id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, Mod.id(id), item);
    }
}
