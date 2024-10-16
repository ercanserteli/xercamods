package xerca.xercapaint.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.item.Items;

import java.lang.reflect.Field;

import static xerca.xercapaint.item.Items.CRAFTING_TAGLESS_SHAPED;

public class RecipeTaglessShaped extends ShapedRecipe {
    public RecipeTaglessShaped(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification){
        super(group, category, pattern, result, showNotification);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
        if(super.matches(inv, worldIn)){
            for(int j = 0; j < inv.getContainerSize(); ++j) {
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
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv, @NotNull HolderLookup.Provider provider) {
        ItemStack result = super.assemble(inv, provider);
        if(!result.isEmpty()){
            for(int j = 0; j < inv.getContainerSize(); ++j) {
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
        try {
            Class<?> cls = ShapedRecipe.class;
            MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
            String runtimeFieldName = resolver.mapFieldName(
                    "intermediary",
                    resolver.unmapClassName("intermediary", cls.getName()),
                    "field_47320",
                    "Lnet/minecraft/world/item/crafting/ShapedRecipePattern;"
            );

            Field patternField = cls.getDeclaredField(runtimeFieldName);
            patternField.setAccessible(true);
            return (ShapedRecipePattern) patternField.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access pattern field in ShapedRecipe", e);
        }
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CRAFTING_TAGLESS_SHAPED;
    }

    public static class TaglessSerializer implements RecipeSerializer<RecipeTaglessShaped> {
        public static final MapCodec<RecipeTaglessShaped> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(RecipeTaglessShaped::getPattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(shapedRecipe -> shapedRecipe.getResultItem(RegistryAccess.EMPTY)),
                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification))
                .apply(instance, RecipeTaglessShaped::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, RecipeTaglessShaped> STREAM_CODEC = StreamCodec.of(RecipeTaglessShaped.TaglessSerializer::toNetwork, RecipeTaglessShaped.TaglessSerializer::fromNetwork);

        @Override
        public @NotNull MapCodec<RecipeTaglessShaped> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RecipeTaglessShaped> streamCodec() {
            return STREAM_CODEC;
        }

        private static RecipeTaglessShaped fromNetwork(RegistryFriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            CraftingBookCategory craftingBookCategory = buffer.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedRecipePattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buffer);
            boolean bl = buffer.readBoolean();
            return new RecipeTaglessShaped(string, craftingBookCategory, shapedRecipePattern, itemStack, bl);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, RecipeTaglessShaped recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeEnum(recipe.category());
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.getPattern());
            ItemStack.STREAM_CODEC.encode(buffer, recipe.getResultItem(RegistryAccess.EMPTY));
            buffer.writeBoolean(recipe.showNotification());
        }
    }
}