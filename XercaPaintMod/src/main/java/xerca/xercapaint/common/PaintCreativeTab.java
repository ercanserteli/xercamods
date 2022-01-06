package xerca.xercapaint.common;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nonnull;

public class PaintCreativeTab extends ItemGroup {
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
