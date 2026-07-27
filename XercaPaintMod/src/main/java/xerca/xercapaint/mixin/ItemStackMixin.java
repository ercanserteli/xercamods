package xerca.xercapaint.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xerca.xercapaint.item.ItemCanvas;

/**
 * 1.20.1 resolves the stack limit from the item alone, so signed canvases get their larger limit here.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void xercapaint$signedCanvasStackSize(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof ItemCanvas && ItemCanvas.isSigned(stack)) {
            cir.setReturnValue(ItemCanvas.SIGNED_STACK_SIZE);
        }
    }
}
