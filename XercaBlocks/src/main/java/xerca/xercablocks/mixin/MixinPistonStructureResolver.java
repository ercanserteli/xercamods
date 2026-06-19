package xerca.xercablocks.mixin;

import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xerca.xercablocks.block.Blocks;

@SuppressWarnings({"UnusedMethod", "UnusedVariable", "unused"})
@Mixin(PistonStructureResolver.class)
public abstract class MixinPistonStructureResolver {
    @Inject(method = "isSticky", at = @At("HEAD"), cancellable = true)
    private static void isSticky(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.ROPE)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
    private static void canStickToEachOther(BlockState first, BlockState second, CallbackInfoReturnable<Boolean> cir) {
        if (first.is(Blocks.ROPE) || second.is(Blocks.ROPE)) {
            cir.setReturnValue(true);
        }
    }
}
