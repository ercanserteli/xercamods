package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Supplier;

public class RecipeConditionSmelting extends FurnaceRecipe {
    private final Supplier<Boolean> condition;
    private IRecipeSerializer serializer;

    public RecipeConditionSmelting(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn, Supplier<Boolean> condition) {
        super(idIn, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
        this.condition = condition;
    }

    public RecipeConditionSmelting(FurnaceRecipe furnaceRecipe, Supplier<Boolean> condition, IRecipeSerializer serializer){
        super(furnaceRecipe.getId(), furnaceRecipe.getGroup(), furnaceRecipe.getIngredients().get(0), furnaceRecipe.getRecipeOutput(), furnaceRecipe.getExperience(), furnaceRecipe.getCookTime());
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

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeConditionSmelting> {
        private static final CookingRecipeSerializer<FurnaceRecipe> furnaceSerializer = IRecipeSerializer.SMELTING;
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        public RecipeConditionSmelting read(ResourceLocation recipeId, JsonObject json) {
            FurnaceRecipe furnaceRecipe = furnaceSerializer.read(recipeId, json);
            return new RecipeConditionSmelting(furnaceRecipe, condition, this);
        }

        public RecipeConditionSmelting read(ResourceLocation recipeId, PacketBuffer buffer) {
            FurnaceRecipe furnaceRecipe = furnaceSerializer.read(recipeId, buffer);
            return new RecipeConditionSmelting(furnaceRecipe, condition, this);
        }

        public void write(PacketBuffer buffer, RecipeConditionSmelting recipe) {
            furnaceSerializer.write(buffer, recipe);
        }


    }
}