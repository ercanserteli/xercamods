package xerca.xercablocks.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
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

    public static void register(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register(Mod.id("block_leather"), BLOCK_LEATHER);
        helper.register(Mod.id("block_straw"), BLOCK_STRAW);
        helper.register(Mod.id("bookcase"), BOOKCASE);
        helper.register(Mod.id("rope"), ROPE);
        helper.register(Mod.id("carving_station"), CARVING_STATION);
        Blocks.carvedWoods().forEach((id, block) -> registerBuildingBlockItem(helper, id, block));
        Blocks.terratiles().forEach((id, block) -> registerColoredBlockItem(helper, id, block));
        Blocks.terratileSlabs().forEach((id, block) -> registerColoredBlockItem(helper, id, block));
        Blocks.terratileStairs().forEach((id, block) -> registerColoredBlockItem(helper, id, block));
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(BOOKCASE);
            event.accept(ROPE);
            event.accept(CARVING_STATION);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(BLOCK_LEATHER);
            event.accept(BLOCK_STRAW);
            BUILDING_TAB_ITEMS.forEach(event::accept);
        }
        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            COLORED_TAB_ITEMS.forEach(event::accept);
        }
    }

    private static void registerBuildingBlockItem(RegisterEvent.RegisterHelper<Item> helper, String id, Block block) {
        Item item = new BlockItem(block, new Item.Properties());
        helper.register(Mod.id(id), item);
        BUILDING_TAB_ITEMS.add(item);
    }

    private static void registerColoredBlockItem(RegisterEvent.RegisterHelper<Item> helper, String id, Block block) {
        Item item = new BlockItem(block, new Item.Properties());
        helper.register(Mod.id(id), item);
        COLORED_TAB_ITEMS.add(item);
    }
}
