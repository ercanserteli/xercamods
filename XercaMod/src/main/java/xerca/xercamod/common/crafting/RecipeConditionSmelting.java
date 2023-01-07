package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class RecipeConditionSmelting extends SmeltingRecipe {
    private final Supplier<Boolean> condition;
    private final RecipeSerializer<?> serializer;

    public RecipeConditionSmelting(SmeltingRecipe furnaceRecipe, Supplier<Boolean> condition, RecipeSerializer<?> serializer){
        super(furnaceRecipe.getId(), furnaceRecipe.getGroup(), CookingBookCategory.FOOD, furnaceRecipe.getIngredients().get(0), furnaceRecipe.getResultItem(), furnaceRecipe.getExperience(), furnaceRecipe.getCookingTime());
        this.condition = condition;
        this.serializer = serializer;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(@NotNull Container inv, @NotNull Level worldIn) {
        if(!condition.get()){
            return false;
        }
        return super.matches(inv, worldIn);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public @NotNull ItemStack assemble(@NotNull Container inv) {
        if(!condition.get()){
            return ItemStack.EMPTY;
        }
        return super.assemble(inv);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public static class Serializer implements RecipeSerializer<RecipeConditionSmelting> {
        private static final RecipeSerializer<SmeltingRecipe> furnaceSerializer = RecipeSerializer.SMELTING_RECIPE;
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        @Override
        public @NotNull RecipeConditionSmelting fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            SmeltingRecipe furnaceRecipe = furnaceSerializer.fromJson(recipeId, json);
            return new RecipeConditionSmelting(furnaceRecipe, condition, this);
        }

        @Override
        public RecipeConditionSmelting fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            SmeltingRecipe furnaceRecipe = furnaceSerializer.fromNetwork(recipeId, buffer);
            return new RecipeConditionSmelting(Objects.requireNonNull(furnaceRecipe), condition, this);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RecipeConditionSmelting recipe) {
            furnaceSerializer.toNetwork(buffer, recipe);
        }
    }
}