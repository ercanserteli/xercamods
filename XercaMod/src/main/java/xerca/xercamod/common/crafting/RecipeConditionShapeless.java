package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class RecipeConditionShapeless extends ShapelessRecipe {
    private final ForgeConfigSpec.BooleanValue condition;
    private IRecipeSerializer serializer;

    public RecipeConditionShapeless(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn, ForgeConfigSpec.BooleanValue condition) {
        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
        this.condition = condition;
    }

    public RecipeConditionShapeless(ShapelessRecipe shapedRecipe, ForgeConfigSpec.BooleanValue condition, IRecipeSerializer serializer){
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), shapedRecipe.getRecipeOutput(), shapedRecipe.getIngredients());
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

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeConditionShapeless> {
        private static final ShapelessRecipe.Serializer shapelessSerializer = new ShapelessRecipe.Serializer();
        private final ForgeConfigSpec.BooleanValue condition;

        public Serializer(ForgeConfigSpec.BooleanValue condition){
            this.condition = condition;
        }

        public RecipeConditionShapeless read(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe shapelessRecipe = shapelessSerializer.read(recipeId, json);
            return new RecipeConditionShapeless(shapelessRecipe, condition, this);
        }

        public RecipeConditionShapeless read(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapelessRecipe shapelessRecipe = shapelessSerializer.read(recipeId, buffer);
            return new RecipeConditionShapeless(shapelessRecipe, condition, this);
        }

        public void write(PacketBuffer buffer, RecipeConditionShapeless recipe) {
            shapelessSerializer.write(buffer, recipe);
        }


    }
}