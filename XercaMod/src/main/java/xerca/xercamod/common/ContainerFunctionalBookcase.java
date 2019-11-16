package xerca.xercamod.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xerca.xercamod.common.tile_entity.TileEntityFunctionalBookcase;
import xerca.xercamod.common.tile_entity.XercaTileEntities;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContainerFunctionalBookcase extends Container {
    public static List<ResourceLocation> acceptedItems = new ArrayList<>(); // This is for other mods (xercamusic) to add items
    private TileEntityFunctionalBookcase tileEntity;

    public ContainerFunctionalBookcase(int windowId, PlayerInventory inv, PacketBuffer extraData){
        this(windowId, inv, inv.player.world.getTileEntity(extraData.readBlockPos()));
    }

    public ContainerFunctionalBookcase(int windowId, PlayerInventory invPlayer, TileEntity tileEntityInventoryBookcase) {
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
    public boolean canInteractWith(@Nonnull PlayerEntity player) {
        return tileEntity.isUsableByPlayer(player);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int sourceSlotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(sourceSlotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();

            if (sourceSlotIndex < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.tileEntity.closeInventory(playerIn);
    }

    class SlotBook extends SlotItemHandler {
        SlotBook(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack stack) {
            Item it = stack.getItem();
            return it == Items.BOOK || it == Items.WRITABLE_BOOK || it == Items.WRITTEN_BOOK || it == Items.ENCHANTED_BOOK ||
                    acceptedItems.contains(it.getRegistryName());
        }

        @Override
        public void onSlotChanged() {
            tileEntity.markDirty();
        }
    }
}
