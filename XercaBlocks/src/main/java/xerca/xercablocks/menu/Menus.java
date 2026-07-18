package xerca.xercablocks.menu;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import xerca.xercablocks.Mod;

public final class Menus {
    public static final MenuType<BookcaseMenu> BOOKCASE =
            new ExtendedMenuType<>(BookcaseMenu::new, BlockPos.STREAM_CODEC);
    public static final MenuType<CarvingStationMenu> CARVING_STATION =
            new MenuType<>(CarvingStationMenu::new, FeatureFlags.DEFAULT_FLAGS);

    private Menus() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, Mod.id("container_functional_bookcase"), BOOKCASE);
        Registry.register(BuiltInRegistries.MENU, Mod.id("carving_station"), CARVING_STATION);
    }
}
