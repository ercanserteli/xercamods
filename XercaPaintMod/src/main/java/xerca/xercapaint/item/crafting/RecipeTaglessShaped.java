package xerca.xercapaint.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import xerca.xercapaint.item.Items;

import static xerca.xercapaint.item.Items.CRAFTING_TAGLESS_SHAPED;

public class RecipeTaglessShaped extends ShapedRecipe {
    public static final MapCodec<RecipeTaglessShaped> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                    CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(recipe -> recipe.bookInfo),
                    ShapedRecipePattern.MAP_CODEC.forGetter(RecipeTaglessShaped::getPattern),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result))
            .apply(instance, RecipeTaglessShaped::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeTaglessShaped> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
            ShapedRecipePattern.STREAM_CODEC, RecipeTaglessShaped::getPattern,
            ItemStackTemplate.STREAM_CODEC, recipe -> recipe.result,
            RecipeTaglessShaped::new);

    private final ShapedRecipePattern pattern;
    private final ItemStackTemplate result;

    public RecipeTaglessShaped(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo, pattern, result);
        this.pattern = pattern;
        this.result = result;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        if (super.matches(inv, worldIn)) {
            for (int j = 0; j < inv.size(); ++j) {
                ItemStack stackInSlot = inv.getItem(j);
                if (!stackInSlot.isEmpty() && stackInSlot.get(Items.CANVAS_PIXELS) != null) {
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
    public ItemStack assemble(CraftingInput inv) {
        ItemStack result = super.assemble(inv);
        if (!result.isEmpty()) {
            for (int j = 0; j < inv.size(); ++j) {
                ItemStack stackInSlot = inv.getItem(j);
                if (!stackInSlot.isEmpty() && stackInSlot.get(Items.CANVAS_PIXELS) != null) {
                    return ItemStack.EMPTY;
                }
            }

            return result;
        }
        return ItemStack.EMPTY;
    }

    public ShapedRecipePattern getPattern() {
        return this.pattern;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        return (RecipeSerializer) CRAFTING_TAGLESS_SHAPED;
    }
}
