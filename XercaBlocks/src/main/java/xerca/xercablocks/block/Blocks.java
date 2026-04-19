package xerca.xercablocks.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import xerca.xercablocks.Mod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Blocks {
    public record TerracottaVariant(String id, DyeColor color) {
    }

    public static final List<TerracottaVariant> TERRACOTTA_VARIANTS = List.of(
            new TerracottaVariant("black_terratile", DyeColor.BLACK),
            new TerracottaVariant("blue_terratile", DyeColor.BLUE),
            new TerracottaVariant("brown_terratile", DyeColor.BROWN),
            new TerracottaVariant("cyan_terratile", DyeColor.CYAN),
            new TerracottaVariant("gray_terratile", DyeColor.GRAY),
            new TerracottaVariant("green_terratile", DyeColor.GREEN),
            new TerracottaVariant("light_blue_terratile", DyeColor.LIGHT_BLUE),
            new TerracottaVariant("light_gray_terratile", DyeColor.LIGHT_GRAY),
            new TerracottaVariant("lime_terratile", DyeColor.LIME),
            new TerracottaVariant("magenta_terratile", DyeColor.MAGENTA),
            new TerracottaVariant("orange_terratile", DyeColor.ORANGE),
            new TerracottaVariant("pink_terratile", DyeColor.PINK),
            new TerracottaVariant("purple_terratile", DyeColor.PURPLE),
            new TerracottaVariant("red_terratile", DyeColor.RED),
            new TerracottaVariant("white_terratile", DyeColor.WHITE),
            new TerracottaVariant("yellow_terratile", DyeColor.YELLOW),
            new TerracottaVariant("terratile", DyeColor.LIGHT_GRAY)
    );

    private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    public static final Map<String, Block> TERRATILES = new LinkedHashMap<>();
    public static final Map<String, Block> TERRATILE_SLABS = new LinkedHashMap<>();
    public static final Map<String, Block> TERRATILE_STAIRS = new LinkedHashMap<>();

    public static final Block BLOCK_LEATHER = register("block_leather",
            new Block(BlockBehaviour.Properties.of().ignitedByLava().mapColor(DyeColor.YELLOW).sound(SoundType.WOOL).strength(1.0f)));
    public static final Block BLOCK_STRAW = register("block_straw",
            new Block(BlockBehaviour.Properties.of().ignitedByLava().mapColor(DyeColor.YELLOW).sound(SoundType.GRASS).strength(0.8f)));
    public static final Block BLOCK_BOOKCASE = register("block_bookcase", new BlockFunctionalBookcase());
    public static final Block ROPE = register("rope", new BlockRope());

    static {
        for (TerracottaVariant variant : TERRACOTTA_VARIANTS) {
            Block base = register(variant.id(), new BlockTerracottaTile(variant.color()));
            TERRATILES.put(variant.id(), base);

            Block slab = register(variant.id() + "_slab", new BlockTerracottaTileSlab(variant.color()));
            TERRATILE_SLABS.put(variant.id() + "_slab", slab);

            Block stairs = register(variant.id() + "_stairs", new BlockTerracottaTileStairs(base.defaultBlockState(), variant.color()));
            TERRATILE_STAIRS.put(variant.id() + "_stairs", stairs);
        }
    }

    private Blocks() {
    }

    private static Block register(String id, Block block) {
        BLOCKS.put(id, block);
        return block;
    }

    public static void registerBlocks() {
        BLOCKS.forEach((id, block) -> Registry.register(BuiltInRegistries.BLOCK, Mod.id(id), block));
    }

    public static Map<String, Block> allBlocks() {
        return BLOCKS;
    }
}
