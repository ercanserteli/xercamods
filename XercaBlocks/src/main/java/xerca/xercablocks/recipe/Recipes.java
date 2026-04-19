package xerca.xercablocks.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import xerca.xercablocks.Mod;

public final class Recipes {
    public static final RecipeType<CarvingRecipe> CARVING_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return Mod.MOD_ID + ":carving";
        }
    };

    public static final RecipeSerializer<CarvingRecipe> CARVING_SERIALIZER = new CarvingRecipeSerializer();

    private Recipes() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Mod.id("carving"), CARVING_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Mod.id("carving"), CARVING_SERIALIZER);
    }

    private static final class CarvingRecipeSerializer extends SingleItemRecipe.Serializer<CarvingRecipe> {
        private CarvingRecipeSerializer() {
            super(CarvingRecipe::new);
        }
    }
}
