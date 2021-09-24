package xerca.xercamod.common;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.crafting.RecipeCarvingStation;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.tile_entity.TileEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ContainerCarvingStation extends AbstractContainerMenu {
    private final ContainerLevelAccess worldPosCallable;
    /** The index of the selected recipe in the GUI. */
    private final DataSlot selectedRecipe = DataSlot.standalone();
    private final Level world;
    private List<RecipeCarvingStation> recipes = Lists.newArrayList();
    /** The ItemStack set in the input slot by the player. */
    private ItemStack itemStackInput = ItemStack.EMPTY;
    /**
     * Stores the game time of the last time the player took items from the the crafting result slot. This is used to
     * prevent the sound from being played multiple times on the same tick.
     */
    private long lastOnTake;
    final Slot inputInventorySlot;
    /** The inventory slot that stores the output of the crafting recipe. */
    final Slot outputInventorySlot;
    private Runnable inventoryUpdateListener = () -> {
    };
    public final Container inputInventory = new SimpleContainer(1) {
        /**
         * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
         * it hasn't changed and skip it.
         */
        public void setChanged() {
            super.setChanged();
            ContainerCarvingStation.this.slotsChanged(this);
            ContainerCarvingStation.this.inventoryUpdateListener.run();
        }
    };
    /** The inventory that stores the output of the crafting recipe. */
    private final ResultContainer inventory = new ResultContainer();

    public ContainerCarvingStation(int windowIdIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
        this(windowIdIn, playerInventoryIn, ContainerLevelAccess.NULL);
    }

    public ContainerCarvingStation(int windowIdIn, Inventory playerInventoryIn, final ContainerLevelAccess worldPosCallableIn) {
        super(TileEntities.CONTAINER_CARVING_STATION, windowIdIn);
        this.worldPosCallable = worldPosCallableIn;
        this.world = playerInventoryIn.player.level;
        this.inputInventorySlot = this.addSlot(new Slot(this.inputInventory, 0, 20, 33));
        this.outputInventorySlot = this.addSlot(new Slot(this.inventory, 1, 143, 33) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            public void onTake(Player thePlayer, ItemStack stack) {
                ItemStack itemstack = ContainerCarvingStation.this.inputInventorySlot.remove(1);
                if (!itemstack.isEmpty()) {
                    ContainerCarvingStation.this.updateRecipeResultSlot();
                }

                stack.getItem().onCraftedBy(stack, thePlayer.level, thePlayer);
                worldPosCallableIn.execute((world, blockPos) -> {
                    long l = world.getGameTime();
                    if (ContainerCarvingStation.this.lastOnTake != l) {
                        world.playSound(null, blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        ContainerCarvingStation.this.lastOnTake = l;
                    }

                });
                super.onTake(thePlayer, stack);
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.selectedRecipe);
    }

    /**
     * Returns the index of the selected recipe.
     */
    @OnlyIn(Dist.CLIENT)
    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    @OnlyIn(Dist.CLIENT)
    public List<RecipeCarvingStation> getRecipeList() {
        return this.recipes;
    }

    @OnlyIn(Dist.CLIENT)
    public int getRecipeListSize() {
        return this.recipes.size();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasItemsinInputSlot() {
        return this.inputInventorySlot.hasItem() && !this.recipes.isEmpty();
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player playerIn) {
        return stillValid(this.worldPosCallable, playerIn, Blocks.CARVING_STATION);
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean clickMenuButton(Player playerIn, int id) {
        if (id >= 0 && id < this.recipes.size()) {
            this.selectedRecipe.set(id);
            this.updateRecipeResultSlot();
        }

        return true;
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void slotsChanged(Container inventoryIn) {
        ItemStack itemstack = this.inputInventorySlot.getItem();
        if (itemstack.getItem() != this.itemStackInput.getItem()) {
            this.itemStackInput = itemstack.copy();
            this.updateAvailableRecipes(inventoryIn, itemstack);
        }

    }

    private void updateAvailableRecipes(Container inventoryIn, ItemStack stack) {
        this.recipes.clear();
        this.selectedRecipe.set(-1);
        this.outputInventorySlot.set(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.recipes = this.world.getRecipeManager().getRecipesFor(Items.CARVING_STATION_TYPE, inventoryIn, this.world);
        }
    }

    private void updateRecipeResultSlot() {
        if (!this.recipes.isEmpty()) {
            RecipeCarvingStation RecipeCarvingStation = this.recipes.get(this.selectedRecipe.get());
            this.outputInventorySlot.set(RecipeCarvingStation.assemble(this.inputInventory));
        } else {
            this.outputInventorySlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    public MenuType<?> getType() {
        return TileEntities.CONTAINER_CARVING_STATION;
    }

    @OnlyIn(Dist.CLIENT)
    public void setInventoryUpdateListener(Runnable listenerIn) {
        this.inventoryUpdateListener = listenerIn;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
     * null for the initial slot that was double-clicked.
     */
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.container != this.inventory && super.canTakeItemForPickAll(stack, slotIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (index == 1) {
                item.onCraftedBy(itemstack1, playerIn.level, playerIn);
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.world.getRecipeManager().getRecipeFor(Items.CARVING_STATION_TYPE, new SimpleContainer(itemstack1), this.world).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 2 && index < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.inventory.removeItemNoUpdate(1);
        this.worldPosCallable.execute((p_217079_2_, p_217079_3_) -> {
            this.clearContainer(playerIn, this.inputInventory);
        });
    }
}
