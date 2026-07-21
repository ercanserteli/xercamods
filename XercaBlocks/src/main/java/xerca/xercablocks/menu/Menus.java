package xerca.xercablocks.menu;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercablocks.Mod;

public final class Menus {
    public static final MenuType<BookcaseMenu> BOOKCASE =
            IMenuTypeExtension.create((windowId, inv, buf) -> new BookcaseMenu(windowId, inv, buf.readBlockPos()));
    public static final MenuType<CarvingStationMenu> CARVING_STATION =
            new MenuType<>(CarvingStationMenu::new, FeatureFlags.DEFAULT_FLAGS);

    private Menus() {
    }

    public static void register(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
        helper.register(Mod.id("container_functional_bookcase"), BOOKCASE);
        helper.register(Mod.id("carving_station"), CARVING_STATION);
    }
}
