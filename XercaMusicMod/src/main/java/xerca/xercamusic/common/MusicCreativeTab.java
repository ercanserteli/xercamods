package xerca.xercamusic.common;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamusic.common.item.Items;

import javax.annotation.Nonnull;

public class MusicCreativeTab extends ItemGroup {
    public MusicCreativeTab() {
        super("music_tab");
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(Items.GUITAR);
    }
}