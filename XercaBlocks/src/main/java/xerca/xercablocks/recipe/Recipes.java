package xerca.xercablocks.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercablocks.Mod;

public final class Recipes {
    public static final RecipeType<CarvingRecipe> CARVING_TYPE = new CarvingRecipeType();

    public static final RecipeSerializer<CarvingRecipe> CARVING_SERIALIZER = new RecipeSerializer<>(
            SingleItemRecipe.simpleMapCodec(CarvingRecipe::new),
            SingleItemRecipe.simpleStreamCodec(CarvingRecipe::new));

    private Recipes() {
    }

    public static void registerTypes(RegisterEvent.RegisterHelper<RecipeType<?>> helper) {
        helper.register(Mod.id("carving"), CARVING_TYPE);
    }

    public static void registerSerializers(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register(Mod.id("carving"), CARVING_SERIALIZER);
    }

    private static class CarvingRecipeType implements RecipeType<CarvingRecipe> {
        @Override
        public String toString() {
            return Mod.MOD_ID + ":carving";
        }
    }
}
