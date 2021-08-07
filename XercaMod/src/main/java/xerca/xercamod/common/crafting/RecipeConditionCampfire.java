package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Supplier;

public class RecipeConditionCampfire extends CampfireCookingRecipe {
    private final Supplier<Boolean> condition;
    private RecipeSerializer serializer;

    public RecipeConditionCampfire(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn, Supplier<Boolean> condition) {
        super(idIn, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
        this.condition = condition;
    }

    public RecipeConditionCampfire(CampfireCookingRecipe CampfireCookingRecipe, Supplier<Boolean> condition, RecipeSerializer serializer){
        super(CampfireCookingRecipe.getId(), CampfireCookingRecipe.getGroup(), CampfireCookingRecipe.getIngredients().get(0), CampfireCookingRecipe.getResultItem(), CampfireCookingRecipe.getExperience(), CampfireCookingRecipe.getCookingTime());
        this.condition = condition;
        this.serializer = serializer;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(Container inv, Level worldIn) {
        if(!condition.get()){
            return false;
        }
        return super.matches(inv, worldIn);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack assemble(Container inv) {
        if(!condition.get()){
            return ItemStack.EMPTY;
        }
        return super.assemble(inv);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public void setSerializer(RecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RecipeConditionCampfire> {
        private static final SimpleCookingSerializer<CampfireCookingRecipe> furnaceSerializer = RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
        private final Supplier<Boolean> condition;

        public Serializer(Supplier<Boolean> condition){
            this.condition = condition;
        }

        public RecipeConditionCampfire fromJson(ResourceLocation recipeId, JsonObject json) {
            CampfireCookingRecipe CampfireCookingRecipe = furnaceSerializer.fromJson(recipeId, json);
            return new RecipeConditionCampfire(CampfireCookingRecipe, condition, this);
        }

        public RecipeConditionCampfire fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            CampfireCookingRecipe CampfireCookingRecipe = furnaceSerializer.fromNetwork(recipeId, buffer);
            return new RecipeConditionCampfire(CampfireCookingRecipe, condition, this);
        }

        public void toNetwork(FriendlyByteBuf buffer, RecipeConditionCampfire recipe) {
            furnaceSerializer.toNetwork(buffer, recipe);
        }


    }
}