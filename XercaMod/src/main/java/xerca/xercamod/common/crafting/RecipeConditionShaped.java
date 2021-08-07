package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Supplier;

public class RecipeConditionShaped extends ShapedRecipe {
    private final Supplier<Boolean> condition;
    private RecipeSerializer serializer;

    public RecipeConditionShaped(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, Supplier<Boolean> condition) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
        this.condition = condition;
    }

    public RecipeConditionShaped(ShapedRecipe shapedRecipe, Supplier<Boolean> condition, RecipeSerializer serializer){
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), shapedRecipe.getRecipeWidth(), shapedRecipe.getRecipeHeight(), shapedRecipe.getIngredients(), shapedRecipe.getResultItem());
        this.condition = condition;
        this.serializer = serializer;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingContainer inv, Level worldIn) {
        if(!condition.get()){
            return false;
        }
        return super.matches(inv, worldIn);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(CraftingContainer inv) {
        if(!condition.get()){
            return ItemStack.EMPTY;
        }
        return super.assemble(inv);
    }

    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public void setSerializer(RecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RecipeConditionShaped> {
        private static final ShapedRecipe.Serializer shapedSerializer = new ShapedRecipe.Serializer();
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        public RecipeConditionShaped fromJson(ResourceLocation recipeId, JsonObject json) {
            ShapedRecipe shapedRecipe = shapedSerializer.fromJson(recipeId, json);
            return new RecipeConditionShaped(shapedRecipe, condition, this);
        }

        public RecipeConditionShaped fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ShapedRecipe shapedRecipe = shapedSerializer.fromNetwork(recipeId, buffer);
            return new RecipeConditionShaped(shapedRecipe, condition, this);
        }

        public void toNetwork(FriendlyByteBuf buffer, RecipeConditionShaped recipe) {
            shapedSerializer.toNetwork(buffer, recipe);
        }


    }
}