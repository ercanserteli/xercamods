package xerca.xercamod.common.tile_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import xerca.xercamod.common.ContainerFunctionalBookcase;
import xerca.xercamod.common.block.BlockFunctionalBookcase;
import xerca.xercamod.common.block.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static xerca.xercamod.common.tile_entity.XercaTileEntities.FUNCTIONAL_BOOKCASE;

public class TileEntityFunctionalBookcase extends BlockEntity implements MenuProvider {
    private final static int NUMBER_OF_SLOTS = 6;
    private final ItemStackHandler inventory = new ItemStackHandler(NUMBER_OF_SLOTS) {
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityFunctionalBookcase(BlockPos blockPos, BlockState blockState) {
        super(FUNCTIONAL_BOOKCASE, blockPos, blockState);
    }

    public int getSizeInventory() {
        return NUMBER_OF_SLOTS;
    }

    // Return true if the given player is able to use this block. In this case it checks that
    // 1) the world tileentity hasn't been replaced in the meantime, and
    // 2) the player isn't too far away from the centre of the block
    public boolean isUsableByPlayer(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.distanceToSqr(worldPosition.getX() + X_CENTRE_OFFSET, worldPosition.getY() + Y_CENTRE_OFFSET, worldPosition.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryCap.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag parentNBTTagCompound) {
        super.save(parentNBTTagCompound); // The super call is required to save and load the tileEntity's location
        CompoundTag inventoryTagCompound = this.inventory.serializeNBT();
        parentNBTTagCompound.put("inventory", inventoryTagCompound);
        return parentNBTTagCompound;
    }

    @Override
    public void load(CompoundTag parentNBTTagCompound) {
        super.load(parentNBTTagCompound); // The super call is required to save and load the tiles location
        CompoundTag inventoryTagCompound = parentNBTTagCompound.getCompound("inventory");
        this.inventory.deserializeNBT(inventoryTagCompound);
    }

    public void closeInventory(Player player) {
        int i = getBookAmount();
        BlockState st = Blocks.BLOCK_BOOKCASE.defaultBlockState().setValue(BlockFunctionalBookcase.BOOK_AMOUNT, i);
        this.level.setBlockAndUpdate(this.worldPosition, st);
        this.level.updateNeighbourForOutputSignal(this.worldPosition, st.getBlock());//todo check if needed
        this.setChanged();
    }

    private int getBookAmount() {
        int total = 0;
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                total++;
            }
        }
        return total;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }


    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.getBlockPos(), 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        this.load(packet.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        this.load(nbt);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(Blocks.BLOCK_BOOKCASE.getDescriptionId() + ".name");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new ContainerFunctionalBookcase(windowId, playerInventory, this);
    }
}
