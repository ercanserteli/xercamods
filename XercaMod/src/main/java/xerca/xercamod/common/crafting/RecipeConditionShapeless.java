package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Supplier;

public class RecipeConditionShapeless extends ShapelessRecipe {
    private final Supplier<Boolean> condition;
    private RecipeSerializer serializer;

    public RecipeConditionShapeless(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn, Supplier<Boolean> condition) {
        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
        this.condition = condition;
    }

    public RecipeConditionShapeless(ShapelessRecipe shapedRecipe, Supplier<Boolean> condition, RecipeSerializer serializer){
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), shapedRecipe.getResultItem(), shapedRecipe.getIngredients());
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

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RecipeConditionShapeless> {
        private static final ShapelessRecipe.Serializer shapelessSerializer = new ShapelessRecipe.Serializer();
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        public RecipeConditionShapeless fromJson(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe shapelessRecipe = shapelessSerializer.fromJson(recipeId, json);
            return new RecipeConditionShapeless(shapelessRecipe, condition, this);
        }

        public RecipeConditionShapeless fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ShapelessRecipe shapelessRecipe = shapelessSerializer.fromNetwork(recipeId, buffer);
            return new RecipeConditionShapeless(shapelessRecipe, condition, this);
        }

        public void toNetwork(FriendlyByteBuf buffer, RecipeConditionShapeless recipe) {
            shapelessSerializer.toNetwork(buffer, recipe);
        }


    }
}