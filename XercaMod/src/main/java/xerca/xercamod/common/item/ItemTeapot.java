package xerca.xercamod.common.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.block.BlockTeapot;
import xerca.xercamod.common.block.Blocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemTeapot extends BlockItem {
    public static final int maxTea = 7;
    private final int teaAmount;
    private final boolean isHot;

    public ItemTeapot(BlockTeapot blockTeapot, int teaAmount, boolean isHot) {
        super(blockTeapot, (teaAmount == 7 ? new Item.Properties().group(Items.teaTab) : new Item.Properties()).defaultMaxDamage(maxTea));
        this.teaAmount = teaAmount;
        this.isHot = isHot;
        String baseName = isHot ? "item_hot_teapot_" : "item_full_teapot_";
        this.setRegistryName(baseName + teaAmount);
    }

    public int getTeaAmount() {
        return teaAmount;
    }

    public boolean isHot() {
        return isHot;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(isHot ? "Hot" : "Cold"));
        tooltip.add(new StringTextComponent("Tea amount: " + teaAmount));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isTeaEnabled()){
            return;
        }
//        super.fillItemGroup(group, items);
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    @Nullable
    protected BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate;
        if(context.getItem().getItem() instanceof ItemTeapot){
            if(!((ItemTeapot) context.getItem().getItem()).isHot){
                blockstate = null;
            }
            else{
                blockstate = this.getBlock().getStateForPlacement(context).with(BlockTeapot.TEA_AMOUNT, ((ItemTeapot) context.getItem().getItem()).teaAmount);
            }
        }else{
            blockstate = this.getBlock().getStateForPlacement(context);
        }
        return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
    }
}
