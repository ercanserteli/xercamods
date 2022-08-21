package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class RecipeConditionSmoking extends SmokingRecipe {
    private final Supplier<Boolean> condition;
    private final RecipeSerializer<?> serializer;

    public RecipeConditionSmoking(SmokingRecipe SmokingRecipe, Supplier<Boolean> condition, RecipeSerializer<?> serializer){
        super(SmokingRecipe.getId(), SmokingRecipe.getGroup(), SmokingRecipe.getIngredients().get(0), SmokingRecipe.getResultItem(), SmokingRecipe.getExperience(), SmokingRecipe.getCookingTime());
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

    public static class Serializer implements RecipeSerializer<RecipeConditionSmoking> {
        private static final SimpleCookingSerializer<SmokingRecipe> furnaceSerializer = RecipeSerializer.SMOKING_RECIPE;
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        @Override
        public @NotNull RecipeConditionSmoking fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            SmokingRecipe SmokingRecipe = furnaceSerializer.fromJson(recipeId, json);
            return new RecipeConditionSmoking(SmokingRecipe, condition, this);
        }

        @Override
        public RecipeConditionSmoking fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            SmokingRecipe SmokingRecipe = furnaceSerializer.fromNetwork(recipeId, buffer);
            return new RecipeConditionSmoking(Objects.requireNonNull(SmokingRecipe), condition, this);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RecipeConditionSmoking recipe) {
            furnaceSerializer.toNetwork(buffer, recipe);
        }


    }
}