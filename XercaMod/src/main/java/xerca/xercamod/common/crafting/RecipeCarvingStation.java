package xerca.xercamod.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.item.Items;

public class RecipeCarvingStation extends SingleItemRecipe {
    public RecipeCarvingStation(ResourceLocation resourceLocation, String s, Ingredient ingredient, ItemStack stack) {
        super(Items.CARVING_STATION_TYPE.get(), Items.CARVING.get(), resourceLocation, s, ingredient, stack);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(Container inv, @NotNull Level worldIn) {
        return this.ingredient.test(inv.getItem(0));
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(Blocks.CARVING_STATION.get());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public record Serializer<T extends RecipeCarvingStation>(RecipeCarvingStation.Serializer.IRecipeFactory<T> factory) implements RecipeSerializer<T> {
        @Override
        public @NotNull T fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            String s = GsonHelper.getAsString(json, "group", "");
            Ingredient ingredient;
            if (GsonHelper.isArrayNode(json, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            }

            String s1 = GsonHelper.getAsString(json, "result");
            int i = GsonHelper.getAsInt(json, "count");
            ItemStack itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s1)), i);
            return this.factory.create(recipeId, s, ingredient, itemstack);
        }

        @Override
        public T fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String s = buffer.readUtf(32767);
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            return this.factory.create(recipeId, s, ingredient, itemstack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, T recipe) {
            buffer.writeUtf(recipe.group);
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }

        public interface IRecipeFactory<T extends RecipeCarvingStation> {
            T create(ResourceLocation p_create_1_, String p_create_2_, Ingredient p_create_3_, ItemStack p_create_4_);
        }
    }
}
