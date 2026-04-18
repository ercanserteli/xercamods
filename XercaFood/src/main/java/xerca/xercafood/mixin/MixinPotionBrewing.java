package xerca.xercafood.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xerca.xercafood.common.item.Items;

@Mixin(PotionBrewing.class)
public abstract class MixinPotionBrewing {
    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private void isColaPowderIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(Items.COLA_POWDER)) {
            cir.setReturnValue(true);
        }
    }
}
