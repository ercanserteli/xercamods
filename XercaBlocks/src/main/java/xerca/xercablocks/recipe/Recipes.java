package xerca.xercablocks.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import xerca.xercablocks.Mod;

public final class Recipes {
    public static final RecipeType<CarvingRecipe> CARVING_TYPE = new CarvingRecipeType();

    public static final RecipeSerializer<CarvingRecipe> CARVING_SERIALIZER = new RecipeSerializer<>(
            SingleItemRecipe.simpleMapCodec(CarvingRecipe::new),
            SingleItemRecipe.simpleStreamCodec(CarvingRecipe::new));

    private Recipes() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Mod.id("carving"), CARVING_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Mod.id("carving"), CARVING_SERIALIZER);
    }

    private static class CarvingRecipeType implements RecipeType<CarvingRecipe> {
        @Override
        public String toString() {
            return Mod.MOD_ID + ":carving";
        }
    }
}
