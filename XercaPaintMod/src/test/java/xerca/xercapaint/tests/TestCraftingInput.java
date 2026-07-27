package xerca.xercapaint.tests;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * A standalone crafting grid for recipe unit tests. Recipes take a {@link CraftingContainer} which normally only
 * exists while a menu is open, so this wraps a {@link TransientCraftingContainer} around a stub menu.
 */
public class TestCraftingInput implements CraftingContainer {
    private static final AbstractContainerMenu STUB_MENU = new AbstractContainerMenu(null, -1) {
        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    };

    private final TransientCraftingContainer delegate;

    private TestCraftingInput(int width, int height, List<ItemStack> items) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(width * height, ItemStack.EMPTY);
        for (int i = 0; i < stacks.size() && i < items.size(); i++) {
            stacks.set(i, items.get(i));
        }
        this.delegate = new TransientCraftingContainer(STUB_MENU, width, height, stacks);
    }

    public static TestCraftingInput of(int width, int height, List<ItemStack> items) {
        return new TestCraftingInput(width, height, items);
    }

    public int size() {
        return delegate.getContainerSize();
    }

    @Override
    public int getWidth() {
        return delegate.getWidth();
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public List<ItemStack> getItems() {
        return delegate.getItems();
    }

    @Override
    public int getContainerSize() {
        return delegate.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return delegate.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return delegate.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return delegate.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        delegate.setItem(slot, stack);
    }

    @Override
    public void setChanged() {
        delegate.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        delegate.clearContent();
    }

    @Override
    public void fillStackedContents(StackedContents contents) {
        delegate.fillStackedContents(contents);
    }
}
