package xerca.xercablocks.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercablocks.block_entity.FunctionalBookcaseBlockEntity;

public class BookcaseMenu extends AbstractContainerMenu {
    private static final ResourceLocation MUSIC_SHEET_ID = ResourceLocation.fromNamespaceAndPath("xercamusic", "music_sheet");
    private final Container container;

    public BookcaseMenu(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, getClientContainer(playerInventory, pos));
    }

    public BookcaseMenu(int syncId, Inventory playerInventory, Container container) {
        super(Menus.BOOKCASE, syncId);
        checkContainerSize(container, 6);
        this.container = container;
        container.startOpen(playerInventory.player);

        addBookcaseSlots(container);
        addPlayerSlots(playerInventory);
    }

    private static Container getClientContainer(Inventory playerInventory, BlockPos pos) {
        if (playerInventory.player.level().getBlockEntity(pos) instanceof FunctionalBookcaseBlockEntity blockEntity) {
            return blockEntity;
        }
        return new SimpleContainer(6);
    }

    private void addBookcaseSlots(Container container) {
        final int startX = 61;
        final int startY = 17;
        final int xSpacing = 18;
        final int rowSpacing = 32;

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 3; column++) {
                int slot = row * 3 + column;
                addSlot(new BookSlot(container, slot, startX + column * xSpacing, startY + row * rowSpacing));
            }
        }
    }

    private void addPlayerSlots(Inventory playerInventory) {
        final int xSpacing = 18;

        for (int slot = 0; slot < 9; slot++) {
            addSlot(new Slot(playerInventory, slot, 8 + slot * xSpacing, 142));
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int slot = 9 + row * 9 + column;
                addSlot(new Slot(playerInventory, slot, 8 + column * xSpacing, 84 + row * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int containerSlotCount = 6;

        if (index < containerSlotCount) {
            if (!moveItemStackTo(stack, containerSlotCount, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, containerSlotCount, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    private static final class BookSlot extends Slot {
        private BookSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        private static boolean isAllowedBook(ItemStack stack) {
            Item item = stack.getItem();
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            return stack.is(net.minecraft.world.item.Items.BOOK)
                    || stack.is(net.minecraft.world.item.Items.WRITABLE_BOOK)
                    || stack.is(net.minecraft.world.item.Items.WRITTEN_BOOK)
                    || stack.is(net.minecraft.world.item.Items.ENCHANTED_BOOK)
                    || MUSIC_SHEET_ID.equals(itemId);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isAllowedBook(stack);
        }
    }
}
