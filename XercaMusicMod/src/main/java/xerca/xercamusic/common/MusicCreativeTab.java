package xerca.xercamusic.common;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.item.Items;

import javax.annotation.Nonnull;

public class MusicCreativeTab extends CreativeModeTab {
    public MusicCreativeTab() {
        super("music_tab");
    }

    @Override
    @Nonnull
    public ItemStack makeIcon() {
        return new ItemStack(Items.GUITAR.get());
    }
}
