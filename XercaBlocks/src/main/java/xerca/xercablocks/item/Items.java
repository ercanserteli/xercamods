package xerca.xercablocks.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import xerca.xercablocks.Mod;
import xerca.xercablocks.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public final class Items {
    private static final List<Item> TAB_ITEMS = new ArrayList<>();
    public static final boolean USE_FALLBACK_KNIFE = !FabricLoader.getInstance().isModLoaded("xercafood");

    public static final @Nullable Item ITEM_KNIFE = USE_FALLBACK_KNIFE ? register("item_knife", new ItemKnife()) : null;
    public static final Item ITEM_BLOCK_LEATHER = register("item_block_leather", new BlockItem(Blocks.BLOCK_LEATHER, new Item.Properties()));
    public static final Item ITEM_BLOCK_STRAW = register("item_block_straw", new BlockItem(Blocks.BLOCK_STRAW, new Item.Properties()));
    public static final Item ITEM_BOOKCASE = register("item_bookcase", new BlockItem(Blocks.BLOCK_BOOKCASE, new Item.Properties()));
    public static final Item ROPE = register("rope", new RopeItem(Blocks.ROPE, new Item.Properties()));

    public static final CreativeModeTab BLOCKS_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ITEM_BOOKCASE))
            .title(Component.translatable("itemGroup.xercablocks.blocks_tab"))
            .displayItems((parameters, output) -> TAB_ITEMS.forEach(output::accept))
            .build();

    static {
        Blocks.TERRATILES.forEach(Items::registerBlockItem);
        Blocks.TERRATILE_SLABS.forEach(Items::registerBlockItem);
        Blocks.TERRATILE_STAIRS.forEach(Items::registerBlockItem);
        register("carving_station", new BlockItem(Blocks.CARVING_STATION, new Item.Properties()));
        Blocks.CARVED_WOODS.forEach(Items::registerBlockItem);
    }

    private Items() {
    }

    public static void registerItems() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Mod.id("blocks_tab"), BLOCKS_TAB);

        if (ITEM_KNIFE != null) {
            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.accept(ITEM_KNIFE));
        }
    }

    private static void registerBlockItem(String id, Block block) {
        register(id, new BlockItem(block, new Item.Properties()));
    }

    private static Item register(String id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, Mod.id(id), item);
        TAB_ITEMS.add(item);
        return item;
    }

    private static void registerRecipeSerializer(String id, RecipeSerializer<?> serializer) {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Mod.id(id), serializer);
    }
}
