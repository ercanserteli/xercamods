package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmokingRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Supplier;

public class RecipeConditionSmoking extends SmokingRecipe {
    private final Supplier<Boolean> condition;
    private IRecipeSerializer serializer;

    public RecipeConditionSmoking(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn, Supplier<Boolean> condition) {
        super(idIn, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
        this.condition = condition;
    }

    public RecipeConditionSmoking(SmokingRecipe SmokingRecipe, Supplier<Boolean> condition, IRecipeSerializer serializer){
        super(SmokingRecipe.getId(), SmokingRecipe.getGroup(), SmokingRecipe.getIngredients().get(0), SmokingRecipe.getRecipeOutput(), SmokingRecipe.getExperience(), SmokingRecipe.getCookTime());
        this.condition = condition;
        this.serializer = serializer;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if(!condition.get()){
            return false;
        }
        return super.matches(inv, worldIn);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        if(!condition.get()){
            return ItemStack.EMPTY;
        }
        return super.getCraftingResult(inv);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public void setSerializer(IRecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeConditionSmoking> {
        private static final CookingRecipeSerializer<SmokingRecipe> furnaceSerializer = IRecipeSerializer.SMOKING;
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        public RecipeConditionSmoking read(ResourceLocation recipeId, JsonObject json) {
            SmokingRecipe SmokingRecipe = furnaceSerializer.read(recipeId, json);
            return new RecipeConditionSmoking(SmokingRecipe, condition, this);
        }

        public RecipeConditionSmoking read(ResourceLocation recipeId, PacketBuffer buffer) {
            SmokingRecipe SmokingRecipe = furnaceSerializer.read(recipeId, buffer);
            return new RecipeConditionSmoking(SmokingRecipe, condition, this);
        }

        public void write(PacketBuffer buffer, RecipeConditionSmoking recipe) {
            furnaceSerializer.write(buffer, recipe);
        }


    }
}