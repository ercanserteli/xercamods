package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class RecipeConditionCampfire extends CampfireCookingRecipe {
    private final Supplier<Boolean> condition;
    private final RecipeSerializer<?> serializer;

    public RecipeConditionCampfire(CampfireCookingRecipe CampfireCookingRecipe, Supplier<Boolean> condition, RecipeSerializer<?> serializer){
        super(CampfireCookingRecipe.getId(), CampfireCookingRecipe.getGroup(), CookingBookCategory.FOOD, CampfireCookingRecipe.getIngredients().get(0), CampfireCookingRecipe.getResultItem(), CampfireCookingRecipe.getExperience(), CampfireCookingRecipe.getCookingTime());
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

    public static class Serializer implements RecipeSerializer<RecipeConditionCampfire> {
        private static final RecipeSerializer<CampfireCookingRecipe> furnaceSerializer = RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        @Override
        public @NotNull RecipeConditionCampfire fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            CampfireCookingRecipe campfireCookingRecipe = furnaceSerializer.fromJson(recipeId, json);
            return new RecipeConditionCampfire(campfireCookingRecipe, condition, this);
        }

        @Override
        public RecipeConditionCampfire fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            CampfireCookingRecipe campfireCookingRecipe = furnaceSerializer.fromNetwork(recipeId, buffer);
            return new RecipeConditionCampfire(Objects.requireNonNull(campfireCookingRecipe), condition, this);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RecipeConditionCampfire recipe) {
            furnaceSerializer.toNetwork(buffer, recipe);
        }
    }
}