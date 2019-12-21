package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistryEntry;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.Items;

import java.util.function.Supplier;

public class RecipeConditionShaped extends ShapedRecipe {
    private final Supplier<Boolean> condition;
    private IRecipeSerializer serializer;

    public RecipeConditionShaped(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, Supplier<Boolean> condition) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
        this.condition = condition;
    }

    public RecipeConditionShaped(ShapedRecipe shapedRecipe, Supplier<Boolean> condition, IRecipeSerializer serializer){
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), shapedRecipe.getRecipeWidth(), shapedRecipe.getRecipeHeight(), shapedRecipe.getIngredients(), shapedRecipe.getRecipeOutput());
        this.condition = condition;
        this.serializer = serializer;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(!condition.get()){
            return false;
        }
        return super.matches(inv, worldIn);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(CraftingInventory inv) {
        if(!condition.get()){
            return ItemStack.EMPTY;
        }
        return super.getCraftingResult(inv);
    }

    public IRecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public void setSerializer(IRecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeConditionShaped> {
        private static final ShapedRecipe.Serializer shapedSerializer = new ShapedRecipe.Serializer();
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        public RecipeConditionShaped read(ResourceLocation recipeId, JsonObject json) {
            ShapedRecipe shapedRecipe = shapedSerializer.read(recipeId, json);
            return new RecipeConditionShaped(shapedRecipe, condition, this);
        }

        public RecipeConditionShaped read(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapedRecipe shapedRecipe = shapedSerializer.read(recipeId, buffer);
            return new RecipeConditionShaped(shapedRecipe, condition, this);
        }

        public void write(PacketBuffer buffer, RecipeConditionShaped recipe) {
            shapedSerializer.write(buffer, recipe);
        }


    }
}