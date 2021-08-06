package xerca.xercapaint.common;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nonnull;

public class PaintCreativeTab extends CreativeModeTab {
    public PaintCreativeTab() {
        super("paint_tab");
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ItemStack makeIcon() {
        return new ItemStack(Items.ITEM_PALETTE);
    }
}
