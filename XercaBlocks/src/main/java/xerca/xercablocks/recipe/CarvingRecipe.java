package xerca.xercablocks.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import xerca.xercablocks.block.Blocks;

public class CarvingRecipe extends StonecutterRecipe {
    public CarvingRecipe(String group, Ingredient ingredient, ItemStack result) {
        super(group, ingredient, result);
    }

    @Override
    public RecipeType<?> getType() {
        return Recipes.CARVING_TYPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Recipes.CARVING_SERIALIZER;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.CARVING_STATION);
    }
}
