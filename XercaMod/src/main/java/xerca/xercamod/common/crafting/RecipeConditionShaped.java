package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class RecipeConditionShaped extends ShapedRecipe {
    private final Supplier<Boolean> condition;
    private final RecipeSerializer<?> serializer;

    public RecipeConditionShaped(ShapedRecipe shapedRecipe, Supplier<Boolean> condition, RecipeSerializer<?> serializer){
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), CraftingBookCategory.MISC, shapedRecipe.getRecipeWidth(), shapedRecipe.getRecipeHeight(), shapedRecipe.getIngredients(), shapedRecipe.getResultItem(RegistryAccess.EMPTY));
        this.condition = condition;
        this.serializer = serializer;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
        if(!condition.get()){
            return false;
        }
        return super.matches(inv, worldIn);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess access) {
        if(!condition.get()){
            return ItemStack.EMPTY;
        }
        return super.assemble(inv, access);
    }

    public @NotNull RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public static class Serializer implements RecipeSerializer<RecipeConditionShaped> {
        private static final ShapedRecipe.Serializer shapedSerializer = new ShapedRecipe.Serializer();
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        @Override
        public @NotNull RecipeConditionShaped fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            ShapedRecipe shapedRecipe = shapedSerializer.fromJson(recipeId, json);
            return new RecipeConditionShaped(shapedRecipe, condition, this);
        }

        @Override
        public RecipeConditionShaped fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            ShapedRecipe shapedRecipe = shapedSerializer.fromNetwork(recipeId, buffer);
            return new RecipeConditionShaped(Objects.requireNonNull(shapedRecipe), condition, this);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RecipeConditionShaped recipe) {
            shapedSerializer.toNetwork(buffer, recipe);
        }


    }
}