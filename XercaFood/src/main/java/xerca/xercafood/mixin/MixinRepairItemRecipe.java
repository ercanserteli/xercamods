package xerca.xercafood.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xerca.xercafood.common.KnifeCompat;

@SuppressWarnings({"UnusedMethod", "UnusedVariable", "unused"})
@Mixin(RepairItemRecipe.class)
public abstract class MixinRepairItemRecipe {
    @Inject(method = "canCombine", at = @At("HEAD"), cancellable = true)
    private static void disableKnifeRepair(ItemStack first, ItemStack second, CallbackInfoReturnable<Boolean> cir) {
        if (KnifeCompat.isKnife(first) || KnifeCompat.isKnife(second)) {
            cir.setReturnValue(false);
        }
    }
}
