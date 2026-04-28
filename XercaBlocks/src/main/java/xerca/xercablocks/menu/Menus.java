package xerca.xercablocks.menu;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import xerca.xercablocks.Mod;

public final class Menus {
    public static MenuType<BookcaseMenu> bookcase;
    public static MenuType<CarvingStationMenu> carvingStation;

    private Menus() {
    }

    public static void register() {
        carvingStation = Registry.register(
                BuiltInRegistries.MENU,
                Mod.id("carving_station"),
                new MenuType<>(CarvingStationMenu::new, FeatureFlags.DEFAULT_FLAGS)
        );

        bookcase = Registry.register(
                BuiltInRegistries.MENU,
                Mod.id("container_functional_bookcase"),
                new ExtendedScreenHandlerType<>(BookcaseMenu::new, BlockPos.STREAM_CODEC)
        );
    }
}
