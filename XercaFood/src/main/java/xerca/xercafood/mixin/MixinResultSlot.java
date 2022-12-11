package xerca.xercafood.mixin;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xerca.xercafood.common.item.Items;

@Mixin(ResultSlot.class)
public abstract class MixinResultSlot {
    @Shadow
    @Final
    private CraftingContainer craftSlots;

    @Inject(method = "checkTakeAchievements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;onCraftedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;I)V"))
    private void checkTakeAchievements(ItemStack itemStack, CallbackInfo ci) {
        Item result = itemStack.getItem();
        // Handling knife usage in repairing
        if (result == Items.ITEM_KNIFE) {
            for (int i = 0; i < craftSlots.getContainerSize(); ++i) {
                ItemStack item = craftSlots.getItem(i);
                if (item.getItem() == Items.ITEM_KNIFE) {
                    craftSlots.setItem(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
    }
}