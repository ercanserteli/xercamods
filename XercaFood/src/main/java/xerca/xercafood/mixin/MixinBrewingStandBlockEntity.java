package xerca.xercafood.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xerca.xercafood.common.item.Items;

@SuppressWarnings({"UnusedMethod", "UnusedVariable", "unused"})
@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity {
    @Inject(method = "isBrewable", at = @At("HEAD"), cancellable = true)
    private static void isColaExtractBrewable(net.minecraft.world.item.alchemy.PotionBrewing potionBrewing, NonNullList<ItemStack> items, CallbackInfoReturnable<Boolean> cir) {
        if (!items.get(3).is(Items.COLA_POWDER)) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            ItemStack stack = items.get(i);

            if (stack.is(net.minecraft.world.item.Items.POTION)
                    && stack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(method = "doBrew", at = @At("HEAD"), cancellable = true)
    private static void brewColaExtract(Level level, BlockPos pos, NonNullList<ItemStack> items, CallbackInfo ci) {
        if (!items.get(3).is(Items.COLA_POWDER)) {
            return;
        }

        boolean brewedAny = false;

        for (int i = 0; i < 3; i++) {
            ItemStack stack = items.get(i);

            if (stack.is(net.minecraft.world.item.Items.POTION)
                    && stack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER)) {
                items.set(i, new ItemStack(Items.COLA_EXTRACT));
                brewedAny = true;
            }
        }

        if (!brewedAny) {
            return;
        }

        items.get(3).shrink(1);

        if (items.get(3).isEmpty()) {
            items.set(3, ItemStack.EMPTY);
        }

        ci.cancel();
    }
}
