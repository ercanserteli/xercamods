package xerca.xercablocks.menu;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import xerca.xercablocks.Mod;

public final class Menus {
    public static MenuType<BookcaseMenu> BOOKCASE;

    private Menus() {
    }

    public static void register() {
        BOOKCASE = Registry.register(
                BuiltInRegistries.MENU,
                Mod.id("container_functional_bookcase"),
                new ExtendedScreenHandlerType<>(BookcaseMenu::new, BlockPos.STREAM_CODEC)
        );
    }
}
