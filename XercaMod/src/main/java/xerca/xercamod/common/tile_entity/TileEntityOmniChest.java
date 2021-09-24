package xerca.xercamod.common.tile_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercamod.common.ContainerOmniChest;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public class TileEntityOmniChest extends BlockEntity implements LidBlockEntity {
    private final ChestLidController chestLidController = new ChestLidController();
    private final Set<Player> openers = new HashSet<>();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level level, BlockPos blockPos, BlockState blockState) {
            level.playSound(null, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.5F);
        }

        protected void onClose(Level level, BlockPos blockPos, BlockState blockState) {
            level.playSound(null, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.5F);
        }

        protected void openerCountChanged(Level level, BlockPos blockPos, BlockState blockState, int i, int j) {
            level.blockEvent(TileEntityOmniChest.this.worldPosition, Blocks.OMNI_CHEST, 1, j);
        }

        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu) {
                Container plContainer = ((ChestMenu) player.containerMenu).getContainer();
                if (plContainer instanceof ContainerOmniChest uniContainer) {
                    return uniContainer.testPlayerChest(player, TileEntityOmniChest.this);
                }
            }
            return false;
        }
    };

    public TileEntityOmniChest(BlockPos blockPos, BlockState blockState) {
        super(TileEntities.OMNI_CHEST, blockPos, blockState);
    }

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState blockState, TileEntityOmniChest tileEntityOmniChest) {
        tileEntityOmniChest.chestLidController.tickLid();
    }

    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            this.chestLidController.shouldBeOpen(j > 0);
            return true;
        } else {
            return super.triggerEvent(i, j);
        }
    }

    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            XercaMod.LOGGER.info(player.getName().getString() + " chest("+getBlockPos()+") startOpen");
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
            this.openers.add(player);
        }
    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            XercaMod.LOGGER.info(player.getName().getString() + " chest("+getBlockPos()+") stopOpen");
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
            this.openers.remove(player);
        }
    }

//    public void addOpener(Player player){
//        if (!this.remove && !player.isSpectator()) {
//            XercaMod.LOGGER.info(player.getName().getString() + " chest("+getBlockPos()+") addOpener");
//        }
//    }

    public boolean isPlayerOpener(Player player){
        return openers.contains(player);
    }

    public boolean hasOpeners(){
        return !openers.isEmpty();
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr(this.worldPosition.getX() + 0.5D,
                    this.worldPosition.getY() + 0.5D,
                    this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public float getOpenNess(float p_59281_) {
        return this.chestLidController.getOpenness(p_59281_);
    }
}