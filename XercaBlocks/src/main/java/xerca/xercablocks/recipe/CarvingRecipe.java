package xerca.xercablocks.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.NullMarked;
import xerca.xercablocks.Mod;

@NullMarked
public class CarvingRecipe extends StonecutterRecipe {
    public CarvingRecipe(Recipe.CommonInfo commonInfo, Ingredient ingredient, ItemStackTemplate result) {
        super(commonInfo, ingredient, result);
    }

    public static boolean isCarvingOutput(Item item) {
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        if (id.getNamespace().equals(Mod.MOD_ID)) {
            return id.getPath().startsWith("carved_");
        }
        return "minecraft".equals(id.getNamespace()) && id.getPath().startsWith("stripped_")
                && (id.getPath().endsWith("_log") || id.getPath().endsWith("_stem"));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RecipeType<StonecutterRecipe> getType() {
        return (RecipeType) Recipes.CARVING_TYPE;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RecipeSerializer<StonecutterRecipe> getSerializer() {
        return (RecipeSerializer) Recipes.CARVING_SERIALIZER;
    }

}
