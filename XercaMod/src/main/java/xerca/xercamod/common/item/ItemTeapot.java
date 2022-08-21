package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.block.BlockTeapot;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemTeapot extends BlockItem {
    public static final int maxTea = 7;
    private final int teaAmount;
    private final boolean isHot;

    public ItemTeapot(BlockTeapot blockTeapot, int teaAmount, boolean isHot) {
        super(blockTeapot, (teaAmount == 7 ? new Item.Properties().tab(Items.teaTab) : new Item.Properties()).defaultDurability(maxTea));
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
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.add(Component.literal(isHot ? "Hot" : "Cold"));
        tooltip.add(Component.literal("Tea amount: " + teaAmount));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isTeaEnabled()){
            return;
        }
//        super.fillItemGroup(group, items);
        if (this.allowedIn(group)) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState blockstate = null;
        if(context.getItemInHand().getItem() instanceof ItemTeapot) {
            if(((ItemTeapot) context.getItemInHand().getItem()).isHot) {
                BlockState blockstateForPlacement = this.getBlock().getStateForPlacement(context);
                if(blockstateForPlacement != null) {
                    blockstate = blockstateForPlacement.setValue(BlockTeapot.TEA_AMOUNT, ((ItemTeapot) context.getItemInHand().getItem()).teaAmount);
                }
            }
        } else {
            blockstate = this.getBlock().getStateForPlacement(context);
        }
        return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
    }
}
