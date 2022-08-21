package xerca.xercamod.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.Items;

public class RecipeTeaFilling extends CustomRecipe {
    public RecipeTeaFilling(ResourceLocation p_i48170_1_) {
        super(p_i48170_1_);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
        if(!Config.isTeaEnabled()){
            return false;
        }

        int i = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemStack bucketStack = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ITEM_TEAPOT.get()) {
                    if (!teapotStack.isEmpty()) {
                        return false;
                    }
                    teapotStack = itemstack;
                }else if (itemstack.getItem() == net.minecraft.world.item.Items.WATER_BUCKET) {
                    if (!bucketStack.isEmpty()) {
                        return false;
                    }
                    bucketStack = itemstack;
                } else {
                    if (itemstack.getItem() != Items.ITEM_TEA_DRIED.get()) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !teapotStack.isEmpty() && !bucketStack.isEmpty() && i > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv) {
        if(!Config.isTeaEnabled()){
            return ItemStack.EMPTY;
        }

        int i = 0;
        ItemStack teapotStack = ItemStack.EMPTY;
        ItemStack bucketStack = ItemStack.EMPTY;

        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.ITEM_TEAPOT.get()) {
                    if (!teapotStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    teapotStack = itemstack;
                }else if (itemstack.getItem() == net.minecraft.world.item.Items.WATER_BUCKET) {
                    if (!bucketStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    bucketStack = itemstack;
                } else {
                    if (itemstack.getItem() != Items.ITEM_TEA_DRIED.get()) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        if (!teapotStack.isEmpty() && !bucketStack.isEmpty() && i > 0) {
            String str = Items.ITEM_FULL_TEAPOT_1.getId().toString();
            str = str.substring(0, str.length() - 1) + i;
            return new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(str)));
        } else {
            return ItemStack.EMPTY;
        }
    }

    public @NotNull RecipeSerializer<?> getSerializer() {
        return Items.CRAFTING_SPECIAL_TEA_FILLING.get();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }
}