package xerca.xercaomnichest.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import xerca.xercaomnichest.block_entity.BlockEntityOmniChest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OmniChestInventory extends SimpleContainer {
    private final Runnable dirtyCallback;
    private final Map<UUID, BlockPos> playerChests = new HashMap<>();

    public OmniChestInventory(Runnable dirtyCallback) {
        super(27);
        this.dirtyCallback = dirtyCallback;
    }

    public void setActiveChest(BlockEntityOmniChest chest, Player player) {
        playerChests.put(player.getUUID(), chest.getBlockPos());
    }

    @Override
    public boolean stillValid(Player player) {
        BlockEntityOmniChest chest = getActiveChest(player);
        return chest != null && chest.stillValid(player);
    }

    @Override
    public void startOpen(ContainerUser user) {
        if (user.getLivingEntity() instanceof Player player) {
            BlockEntityOmniChest chest = getActiveChest(player);
            if (chest != null) {
                chest.startOpen(user);
                super.startOpen(user);
            }
        }
    }

    @Override
    public void stopOpen(ContainerUser user) {
        if (user.getLivingEntity() instanceof Player player) {
            BlockEntityOmniChest chest = getActiveChest(player);
            if (chest != null) {
                chest.stopOpen(user);
                super.stopOpen(user);
                playerChests.remove(player.getUUID());
            }
        }
    }

    public boolean testPlayerChest(Player player, BlockEntityOmniChest chest) {
        BlockPos activePos = playerChests.get(player.getUUID());
        return chest.getBlockPos().equals(activePos);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        dirtyCallback.run();
    }

    private @Nullable BlockEntityOmniChest getActiveChest(Player player) {
        BlockPos activePos = playerChests.get(player.getUUID());
        if (activePos == null) {
            return null;
        }
        if (!(player.level().getBlockEntity(activePos) instanceof BlockEntityOmniChest omniChest)) {
            playerChests.remove(player.getUUID());
            return null;
        }
        return omniChest;
    }
}
