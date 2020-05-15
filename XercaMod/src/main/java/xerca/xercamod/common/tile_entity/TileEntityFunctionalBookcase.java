package xerca.xercamod.common.tile_entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

public class TileEntityFunctionalBookcase extends TileEntity implements INamedContainerProvider {
    private final static int NUMBER_OF_SLOTS = 6;
    private final ItemStackHandler inventory = new ItemStackHandler(NUMBER_OF_SLOTS) {
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityFunctionalBookcase() {
        super(FUNCTIONAL_BOOKCASE);
    }

    public int getSizeInventory() {
        return NUMBER_OF_SLOTS;
    }

    // Return true if the given player is able to use this block. In this case it checks that
    // 1) the world tileentity hasn't been replaced in the meantime, and
    // 2) the player isn't too far away from the centre of the block
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryCap.cast();// CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        }
        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT parentNBTTagCompound) {
        super.write(parentNBTTagCompound); // The super call is required to save and load the tileEntity's location
        CompoundNBT inventoryTagCompound = this.inventory.serializeNBT();
        parentNBTTagCompound.put("inventory", inventoryTagCompound);
        return parentNBTTagCompound;
    }

    @Override
    public void read(CompoundNBT parentNBTTagCompound) {
        super.read(parentNBTTagCompound); // The super call is required to save and load the tiles location
        CompoundNBT inventoryTagCompound = parentNBTTagCompound.getCompound("inventory");
        this.inventory.deserializeNBT(inventoryTagCompound);
    }

    public void closeInventory(PlayerEntity player) {
        int i = getBookAmount();
        BlockState st = Blocks.BLOCK_BOOKCASE.getDefaultState().with(BlockFunctionalBookcase.BOOK_AMOUNT, i);
        this.world.setBlockState(this.pos, st);
        this.world.updateComparatorOutputLevel(this.pos, st.getBlock());
        this.markDirty();
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
    public void markDirty() {
        super.markDirty();
    }


    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.read(packet.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(CompoundNBT nbt) {
        this.read(nbt);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Blocks.BLOCK_BOOKCASE.getTranslationKey() + ".name");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerFunctionalBookcase(windowId, playerInventory, this);
    }
}
