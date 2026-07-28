package xerca.xercamusic.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xerca.xercamusic.common.item.ItemMusicSheet;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void xercamusic$signedSheetStackSize(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof ItemMusicSheet) {
            CompoundTag tag = stack.getTag();
            cir.setReturnValue(tag != null && tag.getInt(ItemMusicSheet.KEY_GENERATION) > 0
                    ? ItemMusicSheet.SIGNED_STACK_SIZE : 1);
        }
    }
}
