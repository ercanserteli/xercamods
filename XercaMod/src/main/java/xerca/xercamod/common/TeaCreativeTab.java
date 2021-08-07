package xerca.xercamod.common;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.item.Items;

public class TeaCreativeTab extends CreativeModeTab {
    public TeaCreativeTab() {
        super("tea_tab");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack makeIcon() {
        return new ItemStack(Items.ITEM_FULL_TEAPOT_1);
    }
}
