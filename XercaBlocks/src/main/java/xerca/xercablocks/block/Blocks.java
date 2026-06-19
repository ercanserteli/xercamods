package xerca.xercablocks.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import xerca.xercablocks.Mod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Blocks {
    public record TerracottaVariant(String id, DyeColor color) {
    }

    public record CarvedWoodVariant(String name, Block log, Block strippedLog) {
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
    public static final List<CarvedWoodVariant> CARVED_WOOD_VARIANTS = List.of(
            new CarvedWoodVariant("oak", net.minecraft.world.level.block.Blocks.OAK_LOG, net.minecraft.world.level.block.Blocks.STRIPPED_OAK_LOG),
            new CarvedWoodVariant("birch", net.minecraft.world.level.block.Blocks.BIRCH_LOG, net.minecraft.world.level.block.Blocks.STRIPPED_BIRCH_LOG),
            new CarvedWoodVariant("dark_oak", net.minecraft.world.level.block.Blocks.DARK_OAK_LOG, net.minecraft.world.level.block.Blocks.STRIPPED_DARK_OAK_LOG),
            new CarvedWoodVariant("acacia", net.minecraft.world.level.block.Blocks.ACACIA_LOG, net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_LOG),
            new CarvedWoodVariant("jungle", net.minecraft.world.level.block.Blocks.JUNGLE_LOG, net.minecraft.world.level.block.Blocks.STRIPPED_JUNGLE_LOG),
            new CarvedWoodVariant("spruce", net.minecraft.world.level.block.Blocks.SPRUCE_LOG, net.minecraft.world.level.block.Blocks.STRIPPED_SPRUCE_LOG),
            new CarvedWoodVariant("crimson", net.minecraft.world.level.block.Blocks.CRIMSON_STEM, net.minecraft.world.level.block.Blocks.STRIPPED_CRIMSON_STEM),
            new CarvedWoodVariant("warped", net.minecraft.world.level.block.Blocks.WARPED_STEM, net.minecraft.world.level.block.Blocks.STRIPPED_WARPED_STEM)
    );

    private static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
    private static final Map<String, Block> TERRATILES = new LinkedHashMap<>();
    private static final Map<String, Block> TERRATILE_SLABS = new LinkedHashMap<>();
    private static final Map<String, Block> TERRATILE_STAIRS = new LinkedHashMap<>();
    private static final Map<String, Block> CARVED_WOODS = new LinkedHashMap<>();

    public static final Block BLOCK_LEATHER = register("block_leather",
            new Block(properties("block_leather").ignitedByLava().mapColor(DyeColor.YELLOW).sound(SoundType.WOOL).strength(1.0f)));
    public static final Block BLOCK_STRAW = register("block_straw",
            new Block(properties("block_straw").ignitedByLava().mapColor(DyeColor.YELLOW).sound(SoundType.GRASS).strength(0.8f)));
    public static final Block BLOCK_BOOKCASE = register("block_bookcase", new BlockFunctionalBookcase(properties("block_bookcase")));
    public static final Block ROPE = register("rope", new BlockRope(properties("rope")));
    public static final Block CARVING_STATION = register("carving_station", new BlockCarvingStation(properties("carving_station")));

    static {
        for (TerracottaVariant variant : TERRACOTTA_VARIANTS) {
            Block base = register(variant.id(), new BlockTerracottaTile(variant.color(), properties(variant.id())));
            TERRATILES.put(variant.id(), base);

            Block slab = register(variant.id() + "_slab", new BlockTerracottaTileSlab(variant.color(), properties(variant.id() + "_slab")));
            TERRATILE_SLABS.put(variant.id() + "_slab", slab);

            Block stairs = register(variant.id() + "_stairs", new BlockTerracottaTileStairs(base.defaultBlockState(), variant.color(), properties(variant.id() + "_stairs")));
            TERRATILE_STAIRS.put(variant.id() + "_stairs", stairs);
        }

        for (CarvedWoodVariant variant : CARVED_WOOD_VARIANTS) {
            for (int i = 1; i <= 8; ++i) {
                String id = "carved_" + variant.name() + "_" + i;
                Block block = switch (variant.name()) {
                    case "acacia" -> new BlockCarvedAcacia(properties(id));
                    case "crimson", "warped" -> new BlockCarvedNetherLog(properties(id));
                    default -> new BlockCarvedLog(properties(id));
                };
                CARVED_WOODS.put(id, register(id, block));
            }
        }
    }

    private Blocks() {
    }

    private static BlockBehaviour.Properties properties(String id) {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Mod.id(id)));
    }

    private static Block register(String id, Block block) {
        BLOCKS.put(id, block);
        return block;
    }

    public static void registerBlocks() {
        BLOCKS.forEach((id, block) -> Registry.register(BuiltInRegistries.BLOCK, Mod.id(id), block));
    }

    public static Map<String, Block> terratiles() {
        return TERRATILES;
    }

    public static Map<String, Block> terratileSlabs() {
        return TERRATILE_SLABS;
    }

    public static Map<String, Block> terratileStairs() {
        return TERRATILE_STAIRS;
    }

    public static Map<String, Block> carvedWoods() {
        return CARVED_WOODS;
    }
}
