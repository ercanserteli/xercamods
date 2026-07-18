package xerca.xercablocks.mixin;

import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xerca.xercablocks.menu.CarvingStationMenu;
import xerca.xercablocks.recipe.CarvingRecipe;

import java.util.List;

@Mixin(StonecutterMenu.class)
abstract class MixinStonecutterMenu {
    @Redirect(
            method = {"setupRecipeList", "quickMoveStack"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeAccess;stonecutterRecipes()Lnet/minecraft/world/item/crafting/SelectableRecipe$SingleInputSet;")
    )
    private SelectableRecipe.SingleInputSet<StonecutterRecipe> selectRecipesForMenu(RecipeAccess recipeAccess) {
        boolean carvingStation = CarvingStationMenu.class.isAssignableFrom(this.getClass());
        List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> entries = recipeAccess.stonecutterRecipes().entries().stream()
                .filter(entry -> isCarvingRecipe(entry) == carvingStation)
                .toList();
        return new SelectableRecipe.SingleInputSet<>(entries);
    }

    private static boolean isCarvingRecipe(SelectableRecipe.SingleInputEntry<StonecutterRecipe> entry) {
        if (entry.recipe().recipe().isPresent()) {
            return entry.recipe().recipe().orElseThrow().value() instanceof CarvingRecipe;
        }
        if (entry.recipe().optionDisplay() instanceof SlotDisplay.ItemStackSlotDisplay(
                net.minecraft.world.item.ItemStack stack
        )) {
            return CarvingRecipe.isCarvingOutput(stack.getItem());
        }
        return false;
    }
}
