package xerca.xercamod.common;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.item.Items;

public class TeaCreativeTab extends ItemGroup {
    public TeaCreativeTab() {
        super("tea_tab");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(Items.ITEM_FULL_TEAPOT_1);
    }
}
