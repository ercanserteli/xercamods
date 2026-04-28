package xerca.xercablocks.mixin;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xerca.xercablocks.menu.CarvingStationMenu;
import xerca.xercablocks.recipe.Recipes;

@SuppressWarnings("ConstantValue")
@Mixin(StonecutterMenu.class)
abstract class MixinStonecutterMenu {
    @Redirect(
            method = "setupRecipeList",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/crafting/RecipeType;STONECUTTING:Lnet/minecraft/world/item/crafting/RecipeType;")
    )
    @SuppressFBWarnings(value = "BC", justification = "Mixin applies to multiple menu runtime types; static analyzer cannot model transformed type.")
    private RecipeType<StonecutterRecipe> useCarvingRecipeTypeForCarvingStation() {
        return CarvingStationMenu.class.isAssignableFrom(this.getClass())
                ? castRecipeType(Recipes.CARVING_TYPE)
                : RecipeType.STONECUTTING;
    }

    @Redirect(
            method = "quickMoveStack",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/crafting/RecipeType;STONECUTTING:Lnet/minecraft/world/item/crafting/RecipeType;")
    )
    @SuppressFBWarnings(value = "BC", justification = "Mixin applies to multiple menu runtime types; static analyzer cannot model transformed type.")
    private RecipeType<StonecutterRecipe> useCarvingRecipeTypeForCarvingQuickMove(Player player, int slot) {
        return CarvingStationMenu.class.isAssignableFrom(this.getClass())
                ? castRecipeType(Recipes.CARVING_TYPE)
                : RecipeType.STONECUTTING;
    }

    @SuppressWarnings("unchecked")
    private static RecipeType<StonecutterRecipe> castRecipeType(RecipeType<?> recipeType) {
        return (RecipeType<StonecutterRecipe>) recipeType;
    }
}
