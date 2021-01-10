package xerca.xercamod.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamod.common.XercaMod;


@ObjectHolder(XercaMod.MODID)
public class Blocks {
    public static final BlockCarvingStation CARVING_STATION = null;

    public static final BlockDoner BLOCK_DONER = null;
    public static final BlockTeapot BLOCK_TEAPOT = null;
    public static final BlockTeaPlant BLOCK_TEA_PLANT = null;
    public static final BlockTomatoPlant BLOCK_TOMATO_PLANT = null;
    public static final BlockRicePlant BLOCK_RICE_PLANT = null;
    public static final BlockApplePie BLOCK_APPLE_PIE = null;
    public static final BlockSweetBerryPie BLOCK_SWEET_BERRY_PIE = null;
    public static final Block BLOCK_LEATHER = null;
    public static final Block BLOCK_STRAW = null;
    public static final Block BLOCK_BOOKCASE = null;
    public static final BlockCushion BLACK_CUSHION = null;
    public static final BlockCushion BLUE_CUSHION = null;
    public static final BlockCushion BROWN_CUSHION = null;
    public static final BlockCushion CYAN_CUSHION = null;
    public static final BlockCushion GRAY_CUSHION = null;
    public static final BlockCushion GREEN_CUSHION = null;
    public static final BlockCushion LIGHT_BLUE_CUSHION = null;
    public static final BlockCushion LIGHT_GRAY_CUSHION = null;
    public static final BlockCushion LIME_CUSHION = null;
    public static final BlockCushion MAGENTA_CUSHION = null;
    public static final BlockCushion ORANGE_CUSHION = null;
    public static final BlockCushion PINK_CUSHION = null;
    public static final BlockCushion PURPLE_CUSHION = null;
    public static final BlockCushion RED_CUSHION = null;
    public static final BlockCushion WHITE_CUSHION = null;
    public static final BlockCushion YELLOW_CUSHION = null;
    public static final Block CARVED_OAK_1 = null;
    public static final Block CARVED_OAK_2 = null;
    public static final Block CARVED_OAK_3 = null;
    public static final Block CARVED_OAK_4 = null;
    public static final Block CARVED_OAK_5 = null;
    public static final Block CARVED_OAK_6 = null;
    public static final Block CARVED_OAK_7 = null;
    public static final Block CARVED_OAK_8 = null;
    public static final Block CARVED_BIRCH_1 = null;
    public static final Block CARVED_BIRCH_2 = null;
    public static final Block CARVED_BIRCH_3 = null;
    public static final Block CARVED_BIRCH_4 = null;
    public static final Block CARVED_BIRCH_5 = null;
    public static final Block CARVED_BIRCH_6 = null;
    public static final Block CARVED_BIRCH_7 = null;
    public static final Block CARVED_BIRCH_8 = null;
    public static final Block CARVED_DARK_OAK_1 = null;
    public static final Block CARVED_DARK_OAK_2 = null;
    public static final Block CARVED_DARK_OAK_3 = null;
    public static final Block CARVED_DARK_OAK_4 = null;
    public static final Block CARVED_DARK_OAK_5 = null;
    public static final Block CARVED_DARK_OAK_6 = null;
    public static final Block CARVED_DARK_OAK_7 = null;
    public static final Block CARVED_DARK_OAK_8 = null;
    public static final Block CARVED_ACACIA_1 = null;
    public static final Block CARVED_ACACIA_2 = null;
    public static final Block CARVED_ACACIA_3 = null;
    public static final Block CARVED_ACACIA_4 = null;
    public static final Block CARVED_ACACIA_5 = null;
    public static final Block CARVED_ACACIA_6 = null;
    public static final Block CARVED_ACACIA_7 = null;
    public static final Block CARVED_ACACIA_8 = null;
    public static final Block CARVED_JUNGLE_1 = null;
    public static final Block CARVED_JUNGLE_2 = null;
    public static final Block CARVED_JUNGLE_3 = null;
    public static final Block CARVED_JUNGLE_4 = null;
    public static final Block CARVED_JUNGLE_5 = null;
    public static final Block CARVED_JUNGLE_6 = null;
    public static final Block CARVED_JUNGLE_7 = null;
    public static final Block CARVED_JUNGLE_8 = null;
    public static final Block CARVED_SPRUCE_1 = null;
    public static final Block CARVED_SPRUCE_2 = null;
    public static final Block CARVED_SPRUCE_3 = null;
    public static final Block CARVED_SPRUCE_4 = null;
    public static final Block CARVED_SPRUCE_5 = null;
    public static final Block CARVED_SPRUCE_6 = null;
    public static final Block CARVED_SPRUCE_7 = null;
    public static final Block CARVED_SPRUCE_8 = null;

    public static final BlockTerracottaTile BLACK_TERRATILE = null;
    public static final BlockTerracottaTile BLUE_TERRATILE = null;
    public static final BlockTerracottaTile BROWN_TERRATILE = null;
    public static final BlockTerracottaTile CYAN_TERRATILE = null;
    public static final BlockTerracottaTile GRAY_TERRATILE = null;
    public static final BlockTerracottaTile GREEN_TERRATILE = null;
    public static final BlockTerracottaTile LIGHT_BLUE_TERRATILE = null;
    public static final BlockTerracottaTile LIGHT_GRAY_TERRATILE = null;
    public static final BlockTerracottaTile LIME_TERRATILE = null;
    public static final BlockTerracottaTile MAGENTA_TERRATILE = null;
    public static final BlockTerracottaTile ORANGE_TERRATILE = null;
    public static final BlockTerracottaTile PINK_TERRATILE = null;
    public static final BlockTerracottaTile PURPLE_TERRATILE = null;
    public static final BlockTerracottaTile RED_TERRATILE = null;
    public static final BlockTerracottaTile WHITE_TERRATILE = null;
    public static final BlockTerracottaTile YELLOW_TERRATILE = null;
    public static final BlockTerracottaTile TERRATILE = null;

    public static final BlockTerracottaTileSlab BLACK_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab BLUE_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab BROWN_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab CYAN_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab GRAY_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab GREEN_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab LIGHT_BLUE_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab LIGHT_GRAY_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab LIME_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab MAGENTA_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab ORANGE_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab PINK_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab PURPLE_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab RED_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab WHITE_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab YELLOW_TERRATILE_SLAB = null;
    public static final BlockTerracottaTileSlab TERRATILE_SLAB = null;

    public static final BlockTerracottaTileStairs BLACK_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs BLUE_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs BROWN_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs CYAN_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs GRAY_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs GREEN_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs LIGHT_BLUE_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs LIGHT_GRAY_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs LIME_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs MAGENTA_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs ORANGE_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs PINK_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs PURPLE_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs RED_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs WHITE_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs YELLOW_TERRATILE_STAIRS = null;
    public static final BlockTerracottaTileStairs TERRATILE_STAIRS = null;

    public static final BlockRope ROPE = null;


    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
                    new BlockCarvingStation().setRegistryName("carving_station"),

                    new BlockDoner(),
                    new BlockTeapot(),
                    new BlockTeaPlant(),
                    new BlockTomatoPlant(),
                    new BlockRicePlant(),
                    new BlockApplePie(),
                    new BlockSweetBerryPie(),
                    new Block(Block.Properties.create(Material.WOOL).sound(SoundType.CLOTH).hardnessAndResistance(1.0f)).setRegistryName("block_leather"),
                    new Block(Block.Properties.create(Material.LEAVES).sound(SoundType.PLANT).hardnessAndResistance(0.8f)).setRegistryName("block_straw"),
                    new BlockFunctionalBookcase(),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 0, "black_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 1, "blue_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 2, "brown_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 3, "cyan_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 4, "gray_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 5, "green_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 6, "light_blue_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 7, "light_gray_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 8, "lime_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 9, "magenta_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 10, "orange_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 11, "pink_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 12, "purple_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 13, "red_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 14, "white_cushion"),
                    new BlockCushion(Block.Properties.create(Material.WOOL), 15, "yellow_cushion"),
                    new BlockCarvedLog("carved_oak_1"),
                    new BlockCarvedLog("carved_oak_2"),
                    new BlockCarvedLog("carved_oak_3"),
                    new BlockCarvedLog("carved_oak_4"),
                    new BlockCarvedLog("carved_oak_5"),
                    new BlockCarvedLog("carved_oak_6"),
                    new BlockCarvedLog("carved_oak_7"),
                    new BlockCarvedLog("carved_oak_8"),
                    new BlockCarvedLog("carved_birch_1"),
                    new BlockCarvedLog("carved_birch_2"),
                    new BlockCarvedLog("carved_birch_3"),
                    new BlockCarvedLog("carved_birch_4"),
                    new BlockCarvedLog("carved_birch_5"),
                    new BlockCarvedLog("carved_birch_6"),
                    new BlockCarvedLog("carved_birch_7"),
                    new BlockCarvedLog("carved_birch_8"),
                    new BlockCarvedLog("carved_dark_oak_1"),
                    new BlockCarvedLog("carved_dark_oak_2"),
                    new BlockCarvedLog("carved_dark_oak_3"),
                    new BlockCarvedLog("carved_dark_oak_4"),
                    new BlockCarvedLog("carved_dark_oak_5"),
                    new BlockCarvedLog("carved_dark_oak_6"),
                    new BlockCarvedLog("carved_dark_oak_7"),
                    new BlockCarvedLog("carved_dark_oak_8"),
                    new BlockCarvedAcacia("carved_acacia_1"),
                    new BlockCarvedAcacia("carved_acacia_2"),
                    new BlockCarvedAcacia("carved_acacia_3"),
                    new BlockCarvedAcacia("carved_acacia_4"),
                    new BlockCarvedAcacia("carved_acacia_5"),
                    new BlockCarvedAcacia("carved_acacia_6"),
                    new BlockCarvedAcacia("carved_acacia_7"),
                    new BlockCarvedAcacia("carved_acacia_8"),
                    new BlockCarvedLog("carved_jungle_1"),
                    new BlockCarvedLog("carved_jungle_2"),
                    new BlockCarvedLog("carved_jungle_3"),
                    new BlockCarvedLog("carved_jungle_4"),
                    new BlockCarvedLog("carved_jungle_5"),
                    new BlockCarvedLog("carved_jungle_6"),
                    new BlockCarvedLog("carved_jungle_7"),
                    new BlockCarvedLog("carved_jungle_8"),
                    new BlockCarvedLog("carved_spruce_1"),
                    new BlockCarvedLog("carved_spruce_2"),
                    new BlockCarvedLog("carved_spruce_3"),
                    new BlockCarvedLog("carved_spruce_4"),
                    new BlockCarvedLog("carved_spruce_5"),
                    new BlockCarvedLog("carved_spruce_6"),
                    new BlockCarvedLog("carved_spruce_7"),
                    new BlockCarvedLog("carved_spruce_8"),

                    new BlockTerracottaTile(MaterialColor.BLACK_TERRACOTTA).setRegistryName("black_terratile"),
                    new BlockTerracottaTile(MaterialColor.BLUE_TERRACOTTA).setRegistryName("blue_terratile"),
                    new BlockTerracottaTile(MaterialColor.BROWN_TERRACOTTA).setRegistryName("brown_terratile"),
                    new BlockTerracottaTile(MaterialColor.CYAN_TERRACOTTA).setRegistryName("cyan_terratile"),
                    new BlockTerracottaTile(MaterialColor.GRAY_TERRACOTTA).setRegistryName("gray_terratile"),
                    new BlockTerracottaTile(MaterialColor.GREEN_TERRACOTTA).setRegistryName("green_terratile"),
                    new BlockTerracottaTile(MaterialColor.LIGHT_BLUE_TERRACOTTA).setRegistryName("light_blue_terratile"),
                    new BlockTerracottaTile(MaterialColor.LIGHT_GRAY_TERRACOTTA).setRegistryName("light_gray_terratile"),
                    new BlockTerracottaTile(MaterialColor.LIME_TERRACOTTA).setRegistryName("lime_terratile"),
                    new BlockTerracottaTile(MaterialColor.MAGENTA_TERRACOTTA).setRegistryName("magenta_terratile"),
                    new BlockTerracottaTile(MaterialColor.ORANGE_TERRACOTTA).setRegistryName("orange_terratile"),
                    new BlockTerracottaTile(MaterialColor.PINK_TERRACOTTA).setRegistryName("pink_terratile"),
                    new BlockTerracottaTile(MaterialColor.PURPLE_TERRACOTTA).setRegistryName("purple_terratile"),
                    new BlockTerracottaTile(MaterialColor.RED_TERRACOTTA).setRegistryName("red_terratile"),
                    new BlockTerracottaTile(MaterialColor.WHITE_TERRACOTTA).setRegistryName("white_terratile"),
                    new BlockTerracottaTile(MaterialColor.YELLOW_TERRACOTTA).setRegistryName("yellow_terratile"),
                    new BlockTerracottaTile(MaterialColor.LIGHT_GRAY_TERRACOTTA).setRegistryName("terratile"),

                    new BlockTerracottaTileSlab(MaterialColor.BLACK_TERRACOTTA).setRegistryName("black_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.BLUE_TERRACOTTA).setRegistryName("blue_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.BROWN_TERRACOTTA).setRegistryName("brown_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.CYAN_TERRACOTTA).setRegistryName("cyan_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.GRAY_TERRACOTTA).setRegistryName("gray_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.GREEN_TERRACOTTA).setRegistryName("green_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.LIGHT_BLUE_TERRACOTTA).setRegistryName("light_blue_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.LIGHT_GRAY_TERRACOTTA).setRegistryName("light_gray_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.LIME_TERRACOTTA).setRegistryName("lime_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.MAGENTA_TERRACOTTA).setRegistryName("magenta_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.ORANGE_TERRACOTTA).setRegistryName("orange_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.PINK_TERRACOTTA).setRegistryName("pink_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.PURPLE_TERRACOTTA).setRegistryName("purple_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.RED_TERRACOTTA).setRegistryName("red_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.WHITE_TERRACOTTA).setRegistryName("white_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.YELLOW_TERRACOTTA).setRegistryName("yellow_terratile_slab"),
                    new BlockTerracottaTileSlab(MaterialColor.LIGHT_GRAY_TERRACOTTA).setRegistryName("terratile_slab"),

                    new BlockTerracottaTileStairs(() -> BLACK_TERRATILE.getDefaultState(), MaterialColor.BLACK_TERRACOTTA).setRegistryName("black_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> BLUE_TERRATILE.getDefaultState(), MaterialColor.BLUE_TERRACOTTA).setRegistryName("blue_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> BROWN_TERRATILE.getDefaultState(), MaterialColor.BROWN_TERRACOTTA).setRegistryName("brown_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> CYAN_TERRATILE.getDefaultState(), MaterialColor.CYAN_TERRACOTTA).setRegistryName("cyan_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> GRAY_TERRATILE.getDefaultState(), MaterialColor.GRAY_TERRACOTTA).setRegistryName("gray_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> GREEN_TERRATILE.getDefaultState(), MaterialColor.GREEN_TERRACOTTA).setRegistryName("green_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> LIGHT_BLUE_TERRATILE.getDefaultState(), MaterialColor.LIGHT_BLUE_TERRACOTTA).setRegistryName("light_blue_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> LIGHT_GRAY_TERRATILE.getDefaultState(), MaterialColor.LIGHT_GRAY_TERRACOTTA).setRegistryName("light_gray_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> LIME_TERRATILE.getDefaultState(), MaterialColor.LIME_TERRACOTTA).setRegistryName("lime_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> MAGENTA_TERRATILE.getDefaultState(), MaterialColor.MAGENTA_TERRACOTTA).setRegistryName("magenta_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> ORANGE_TERRATILE.getDefaultState(), MaterialColor.ORANGE_TERRACOTTA).setRegistryName("orange_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> PINK_TERRATILE.getDefaultState(), MaterialColor.PINK_TERRACOTTA).setRegistryName("pink_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> PURPLE_TERRATILE.getDefaultState(), MaterialColor.PURPLE_TERRACOTTA).setRegistryName("purple_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> RED_TERRATILE.getDefaultState(), MaterialColor.RED_TERRACOTTA).setRegistryName("red_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> WHITE_TERRATILE.getDefaultState(), MaterialColor.WHITE_TERRACOTTA).setRegistryName("white_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> YELLOW_TERRATILE.getDefaultState(), MaterialColor.YELLOW_TERRACOTTA).setRegistryName("yellow_terratile_stairs"),
                    new BlockTerracottaTileStairs(() -> TERRATILE.getDefaultState(), MaterialColor.LIGHT_GRAY_TERRACOTTA).setRegistryName("terratile_stairs"),

                    new BlockRope().setRegistryName("rope")
            );
        }
    }
}
