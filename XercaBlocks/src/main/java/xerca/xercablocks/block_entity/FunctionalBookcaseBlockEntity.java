package xerca.xercablocks.block_entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import xerca.xercablocks.block.BlockFunctionalBookcase;
import xerca.xercablocks.menu.BookcaseMenu;

public class FunctionalBookcaseBlockEntity extends BlockEntity implements Container, ExtendedScreenHandlerFactory<BlockPos> {
    private static final int SLOT_COUNT = 6;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public FunctionalBookcaseBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.FUNCTIONAL_BOOKCASE, pos, state);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return worldPosition;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.xercablocks.bookcase");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new BookcaseMenu(syncId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        syncVisualState();
    }

    private void syncVisualState() {
        Level level = this.level;
        if (level == null || level.isClientSide) {
            return;
        }

        int books = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                books++;
            }
        }

        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BlockFunctionalBookcase.BOOK_AMOUNT) && state.getValue(BlockFunctionalBookcase.BOOK_AMOUNT) != books) {
            level.setBlock(worldPosition, state.setValue(BlockFunctionalBookcase.BOOK_AMOUNT, books), 3);
        }
        level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }
}
