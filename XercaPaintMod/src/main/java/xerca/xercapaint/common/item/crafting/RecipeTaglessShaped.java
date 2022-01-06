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
        super(shapedRecipe.getId(), shapedRecipe.getGroup(), shapedRecipe.getRecipeWidth(), shapedRecipe.getRecipeHeight(), shapedRecipe.getIngredients(), shapedRecipe.getResultItem());
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if(super.matches(inv, worldIn)){
            for(int j = 0; j < inv.getContainerSize(); ++j) {
                ItemStack stackInSlot = inv.getItem(j);
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
    public ItemStack assemble(CraftingInventory inv) {
        ItemStack result = super.assemble(inv);
        if(!result.isEmpty()){
            for(int j = 0; j < inv.getContainerSize(); ++j) {
                ItemStack stackInSlot = inv.getItem(j);
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

        public RecipeTaglessShaped fromJson(ResourceLocation recipeId, JsonObject json) {
            ShapedRecipe shapedRecipe = shapedSerializer.fromJson(recipeId, json);
            return new RecipeTaglessShaped(shapedRecipe);
        }

        public RecipeTaglessShaped fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapedRecipe shapedRecipe = shapedSerializer.fromNetwork(recipeId, buffer);
            return new RecipeTaglessShaped(shapedRecipe);
        }

        public void toNetwork(PacketBuffer buffer, RecipeTaglessShaped recipe) {
            shapedSerializer.toNetwork(buffer, recipe);
        }

    }
}