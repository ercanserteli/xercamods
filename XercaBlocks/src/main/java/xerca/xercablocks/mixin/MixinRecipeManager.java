package xerca.xercablocks.mixin;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xerca.xercablocks.recipe.CarvingRecipe;

import java.util.ArrayList;
import java.util.Optional;

@Mixin(RecipeManager.class)
@SuppressFBWarnings({"BC_IMPOSSIBLE_CAST", "NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"})
abstract class MixinRecipeManager {
    @Shadow
    private SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes;

    @Inject(method = "finalizeRecipeLoading", at = @At("TAIL"))
    private void includeCarvingRecipesInStonecutterSync(FeatureFlagSet enabledFeatures, CallbackInfo info) {
        ArrayList<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> entries = new ArrayList<>(stonecutterRecipes.entries());
        for (RecipeHolder<?> holder : ((RecipeManager) (Object) this).getRecipes()) {
            if (holder.value() instanceof CarvingRecipe recipe && recipe.resultDisplay().isEnabled(enabledFeatures)) {
                @SuppressWarnings("unchecked")
                RecipeHolder<StonecutterRecipe> stonecutterHolder = (RecipeHolder<StonecutterRecipe>) holder;
                entries.add(new SelectableRecipe.SingleInputEntry<>(recipe.input(),
                        new SelectableRecipe<>(recipe.resultDisplay(), Optional.of(stonecutterHolder))));
            }
        }
        stonecutterRecipes = new SelectableRecipe.SingleInputSet<>(entries);
    }
}
