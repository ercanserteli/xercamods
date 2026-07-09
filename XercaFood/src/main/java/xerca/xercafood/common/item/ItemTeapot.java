package xerca.xercafood.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xerca.xercafood.common.block.BlockTeapot;

import java.util.function.Consumer;

public class ItemTeapot extends BlockItem {
    private final int teaAmount;
    private final boolean isHot;

    public ItemTeapot(BlockTeapot blockTeapot, int teaAmount, boolean isHot) {
        super(blockTeapot, new Item.Properties().setId(xerca.xercafood.common.Mod.itemKey((isHot ? "hot_teapot_" : "full_teapot_") + teaAmount)));
        this.teaAmount = teaAmount;
        this.isHot = isHot;
    }

    public int getTeaAmount() {
        return teaAmount;
    }

    public boolean isHot() {
        return isHot;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(Component.literal(isHot ? "Hot" : "Cold"));
        tooltip.accept(Component.literal("Tea amount: " + teaAmount));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState blockstate;
        if (context.getItemInHand().getItem() instanceof ItemTeapot itemTeapot) {
            if (!itemTeapot.isHot) {
                blockstate = null;
            } else {
                BlockState baseState = this.getBlock().getStateForPlacement(context);
                blockstate = baseState != null
                        ? baseState.setValue(BlockTeapot.TEA_AMOUNT, ((ItemTeapot) context.getItemInHand().getItem()).teaAmount)
                        : null;
            }
        } else {
            blockstate = this.getBlock().getStateForPlacement(context);
        }
        return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
    }
}
