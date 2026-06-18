package xerca.xercacushion.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import xerca.xercacushion.Mod;
import xerca.xercacushion.block.Blocks;

public final class Items {
    public static final ItemCushion BLACK_CUSHION = create(Blocks.BLACK_CUSHION.getVariant());
    public static final ItemCushion BLUE_CUSHION = create(Blocks.BLUE_CUSHION.getVariant());
    public static final ItemCushion BROWN_CUSHION = create(Blocks.BROWN_CUSHION.getVariant());
    public static final ItemCushion CYAN_CUSHION = create(Blocks.CYAN_CUSHION.getVariant());
    public static final ItemCushion GRAY_CUSHION = create(Blocks.GRAY_CUSHION.getVariant());
    public static final ItemCushion GREEN_CUSHION = create(Blocks.GREEN_CUSHION.getVariant());
    public static final ItemCushion LIGHT_BLUE_CUSHION = create(Blocks.LIGHT_BLUE_CUSHION.getVariant());
    public static final ItemCushion LIGHT_GRAY_CUSHION = create(Blocks.LIGHT_GRAY_CUSHION.getVariant());
    public static final ItemCushion LIME_CUSHION = create(Blocks.LIME_CUSHION.getVariant());
    public static final ItemCushion MAGENTA_CUSHION = create(Blocks.MAGENTA_CUSHION.getVariant());
    public static final ItemCushion ORANGE_CUSHION = create(Blocks.ORANGE_CUSHION.getVariant());
    public static final ItemCushion PINK_CUSHION = create(Blocks.PINK_CUSHION.getVariant());
    public static final ItemCushion PURPLE_CUSHION = create(Blocks.PURPLE_CUSHION.getVariant());
    public static final ItemCushion RED_CUSHION = create(Blocks.RED_CUSHION.getVariant());
    public static final ItemCushion WHITE_CUSHION = create(Blocks.WHITE_CUSHION.getVariant());
    public static final ItemCushion YELLOW_CUSHION = create(Blocks.YELLOW_CUSHION.getVariant());

    private static final ItemCushion[] ALL = {
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

    public static final String[] PATHS = {
            "black_cushion",
            "blue_cushion",
            "brown_cushion",
            "cyan_cushion",
            "gray_cushion",
            "green_cushion",
            "light_blue_cushion",
            "light_gray_cushion",
            "lime_cushion",
            "magenta_cushion",
            "orange_cushion",
            "pink_cushion",
            "purple_cushion",
            "red_cushion",
            "white_cushion",
            "yellow_cushion"
    };

    private Items() {
    }

    private static ItemCushion create(int variant) {
        return new ItemCushion(new Item.Properties(), variant);
    }

    public static Item byVariant(int variant) {
        return ALL[variant];
    }

    public static String pathByVariant(int variant) {
        return PATHS[variant];
    }

    public static void register() {
        for (int i = 0; i < ALL.length; i++) {
            Registry.register(BuiltInRegistries.ITEM, Mod.id(PATHS[i]), ALL[i]);
        }
    }

    public static ItemCushion[] all() {
        return ALL;
    }
}
