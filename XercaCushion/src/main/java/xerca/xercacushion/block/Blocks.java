package xerca.xercacushion.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import xerca.xercacushion.Mod;
import xerca.xercacushion.item.Items;

public final class Blocks {
    public static final BlockCushion BLACK_CUSHION = create(0, MapColor.COLOR_BLACK);
    public static final BlockCushion BLUE_CUSHION = create(1, MapColor.COLOR_BLUE);
    public static final BlockCushion BROWN_CUSHION = create(2, MapColor.COLOR_BROWN);
    public static final BlockCushion CYAN_CUSHION = create(3, MapColor.COLOR_CYAN);
    public static final BlockCushion GRAY_CUSHION = create(4, MapColor.COLOR_GRAY);
    public static final BlockCushion GREEN_CUSHION = create(5, MapColor.COLOR_GREEN);
    public static final BlockCushion LIGHT_BLUE_CUSHION = create(6, MapColor.COLOR_LIGHT_BLUE);
    public static final BlockCushion LIGHT_GRAY_CUSHION = create(7, MapColor.COLOR_LIGHT_GRAY);
    public static final BlockCushion LIME_CUSHION = create(8, MapColor.COLOR_LIGHT_GREEN);
    public static final BlockCushion MAGENTA_CUSHION = create(9, MapColor.COLOR_MAGENTA);
    public static final BlockCushion ORANGE_CUSHION = create(10, MapColor.COLOR_ORANGE);
    public static final BlockCushion PINK_CUSHION = create(11, MapColor.COLOR_PINK);
    public static final BlockCushion PURPLE_CUSHION = create(12, MapColor.COLOR_PURPLE);
    public static final BlockCushion RED_CUSHION = create(13, MapColor.COLOR_RED);
    public static final BlockCushion WHITE_CUSHION = create(14, MapColor.WOOL);
    public static final BlockCushion YELLOW_CUSHION = create(15, MapColor.COLOR_YELLOW);

    public static final BlockCushion[] ALL = {
            BLACK_CUSHION,
            BLUE_CUSHION,
            BROWN_CUSHION,
            CYAN_CUSHION,
            GRAY_CUSHION,
            GREEN_CUSHION,
            LIGHT_BLUE_CUSHION,
            LIGHT_GRAY_CUSHION,
            LIME_CUSHION,
            MAGENTA_CUSHION,
            ORANGE_CUSHION,
            PINK_CUSHION,
            PURPLE_CUSHION,
            RED_CUSHION,
            WHITE_CUSHION,
            YELLOW_CUSHION
    };

    private Blocks() {
    }

    private static BlockCushion create(int variant, MapColor mapColor) {
        return new BlockCushion(Block.Properties.of().mapColor(mapColor).strength(0.8F).sound(SoundType.WOOL).noOcclusion(), variant);
    }

    public static void register() {
        for (BlockCushion block : ALL) {
            Registry.register(BuiltInRegistries.BLOCK, Mod.id(Items.pathByVariant(block.getVariant())), block);
        }
    }
}
