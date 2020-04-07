package xerca.xercapaint.common.item.crafting;

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
import net.minecraftforge.registries.ForgeRegistryEntry;

import static xerca.xercapaint.common.item.Items.CRAFTING_TAGLESS_SHAPED;

public class RecipeTaglessShaped extends ShapedRecipe {

    public RecipeTaglessShaped(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
    }

    public RecipeTaglessShaped(ShapedRecipe shapedRecipe){
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), shapedRecipe.getRecipeWidth(), shapedRecipe.getRecipeHeight(), shapedRecipe.getIngredients(), shapedRecipe.getRecipeOutput());
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(super.matches(inv, worldIn)){
            for(int j = 0; j < inv.getSizeInventory(); ++j) {
                ItemStack stackInSlot = inv.getStackInSlot(j);
                if (!stackInSlot.isEmpty() && stackInSlot.hasTag()) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack result = super.getCraftingResult(inv);
        if(!result.isEmpty()){
            for(int j = 0; j < inv.getSizeInventory(); ++j) {
                ItemStack stackInSlot = inv.getStackInSlot(j);
                if (!stackInSlot.isEmpty() && stackInSlot.hasTag()) {
                    return ItemStack.EMPTY;
                }
            }

            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return CRAFTING_TAGLESS_SHAPED;
    }

    public static class TaglessSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeTaglessShaped> {
        private static final ShapedRecipe.Serializer shapedSerializer = new ShapedRecipe.Serializer();

        public TaglessSerializer(){}

        public RecipeTaglessShaped read(ResourceLocation recipeId, JsonObject json) {
            ShapedRecipe shapedRecipe = shapedSerializer.read(recipeId, json);
            return new RecipeTaglessShaped(shapedRecipe);
        }

        public RecipeTaglessShaped read(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapedRecipe shapedRecipe = shapedSerializer.read(recipeId, buffer);
            return new RecipeTaglessShaped(shapedRecipe);
        }

        public void write(PacketBuffer buffer, RecipeTaglessShaped recipe) {
            shapedSerializer.write(buffer, recipe);
        }

    }
}