package xerca.xercablocks.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.jspecify.annotations.NullMarked;
import xerca.xercablocks.Mod;

@NullMarked
public class CarvingRecipe extends StonecutterRecipe {
    public CarvingRecipe(String group, Ingredient ingredient, ItemStack result) {
        super(group, ingredient, result);
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
