package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class RecipeConditionShapeless extends ShapelessRecipe {
    private final Supplier<Boolean> condition;
    private final RecipeSerializer<?> serializer;

    public RecipeConditionShapeless(ShapelessRecipe shapedRecipe, Supplier<Boolean> condition, RecipeSerializer<?> serializer){
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), CraftingBookCategory.MISC, shapedRecipe.getResultItem(RegistryAccess.EMPTY), shapedRecipe.getIngredients());
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

    public static class Serializer implements RecipeSerializer<RecipeConditionShapeless> {
        private static final ShapelessRecipe.Serializer shapelessSerializer = new ShapelessRecipe.Serializer();
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        @Override
        public @NotNull RecipeConditionShapeless fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            ShapelessRecipe shapelessRecipe = shapelessSerializer.fromJson(recipeId, json);
            return new RecipeConditionShapeless(shapelessRecipe, condition, this);
        }

        @Override
        public RecipeConditionShapeless fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            ShapelessRecipe shapelessRecipe = shapelessSerializer.fromNetwork(recipeId, buffer);
            return new RecipeConditionShapeless(Objects.requireNonNull(shapelessRecipe), condition, this);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RecipeConditionShapeless recipe) {
            shapelessSerializer.toNetwork(buffer, recipe);
        }
    }
}