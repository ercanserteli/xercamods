package xerca.xercablocks.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;

@MethodsReturnNonnullByDefault
public class CarvingRecipe extends StonecutterRecipe {
    public CarvingRecipe(String group, Ingredient ingredient, ItemStack result) {
        super(group, ingredient, result);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RecipeType<StonecutterRecipe> getType() {
        return (RecipeType) Recipes.CARVING_TYPE;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RecipeSerializer<StonecutterRecipe> getSerializer() {
        return (RecipeSerializer) Recipes.CARVING_SERIALIZER;
    }

}
