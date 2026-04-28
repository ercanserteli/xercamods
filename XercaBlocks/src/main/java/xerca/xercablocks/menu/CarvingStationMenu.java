package xerca.xercablocks.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.StonecutterMenu;
import xerca.xercablocks.block.Blocks;

public class CarvingStationMenu extends StonecutterMenu {
    private final ContainerLevelAccess access;

    public CarvingStationMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public CarvingStationMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(containerId, inventory, access);
        this.access = access;
    }

    @Override
    public MenuType<?> getType() {
        return Menus.CARVING_STATION;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, Blocks.CARVING_STATION);
    }
}
