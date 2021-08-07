package xerca.xercamod.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xerca.xercamod.common.tile_entity.TileEntityFunctionalBookcase;
import xerca.xercamod.common.tile_entity.XercaTileEntities;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContainerFunctionalBookcase extends AbstractContainerMenu {
    public static List<ResourceLocation> acceptedItems = new ArrayList<>(); // This is for other mods (xercamusic) to add items
    private TileEntityFunctionalBookcase tileEntity;

    public ContainerFunctionalBookcase(int windowId, Inventory inv, FriendlyByteBuf extraData){
        this(windowId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerFunctionalBookcase(int windowId, Inventory invPlayer, BlockEntity tileEntityInventoryBookcase) {
        super(XercaTileEntities.CONTAINER_FUNCTIONAL_BOOKCASE, windowId);
        if(!(tileEntityInventoryBookcase instanceof TileEntityFunctionalBookcase)){
            XercaMod.LOGGER.error("TileEntity not an instance of TileEntityFunctionalBookcase!");
            return;
        }

        this.tileEntity = (TileEntityFunctionalBookcase)tileEntityInventoryBookcase;
        IItemHandler bookcaseInventory = tileEntityInventoryBookcase.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(NullPointerException::new);

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int TE_INVENTORY_SLOT_COUNT = 6;
        if (TE_INVENTORY_SLOT_COUNT != bookcaseInventory.getSlots()) {
            XercaMod.LOGGER.error("Mismatched slot count in ContainerFunctionalBookcase(" + TE_INVENTORY_SLOT_COUNT
                    + ") and TileEntityFunctionalBookcase (" + bookcaseInventory.getSlots() + ")");
        }
        final int TILE_INVENTORY_XPOS = 61;
        final int TILE_INVENTORY_YPOS = 17;
        final int TILE_SLOT_Y_SPACING = 32;
        final int TILE_ROW_COUNT = 2;
        final int TILE_COLUMN_COUNT = 3;

        // Add the tile inventory container to the gui
        for (int y = 0; y < TILE_ROW_COUNT; y++) {
            for (int x = 0; x < TILE_COLUMN_COUNT; x++) {
                int slotNumber = y * TILE_COLUMN_COUNT + x;
                int xpos = TILE_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = TILE_INVENTORY_YPOS + y * TILE_SLOT_Y_SPACING;
                addSlot(new SlotBook(bookcaseInventory, slotNumber, xpos, ypos));
            }
        }

        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 142;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        int HOTBAR_SLOT_COUNT = 9;
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            addSlot(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 84;
        // Add the rest of the players inventory to the gui
        int PLAYER_INVENTORY_ROW_COUNT = 3;
        int PLAYER_INVENTORY_COLUMN_COUNT = 9;
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new Slot(invPlayer, slotNumber, xpos, ypos));
            }
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return tileEntity.isUsableByPlayer(player);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player player, int sourceSlotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(sourceSlotIndex);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int containerSlots = slots.size() - player.getInventory().items.size();

            if (sourceSlotIndex < containerSlots) {
                if (!this.moveItemStackTo(itemstack1, containerSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.tileEntity.closeInventory(playerIn);
    }

    class SlotBook extends SlotItemHandler {
        SlotBook(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            Item it = stack.getItem();
            return it == Items.BOOK || it == Items.WRITABLE_BOOK || it == Items.WRITTEN_BOOK || it == Items.ENCHANTED_BOOK ||
                    acceptedItems.contains(it.getRegistryName());
        }

        @Override
        public void setChanged() {
            tileEntity.setChanged();
        }
    }
}
