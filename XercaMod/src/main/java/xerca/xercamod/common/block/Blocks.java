package xerca.xercamod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamod.common.XercaMod;


@ObjectHolder(XercaMod.MODID)
public class Blocks {
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

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
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
                    new BlockCarvedLog("carved_dark_oak_8")
            );
        }
    }
}
