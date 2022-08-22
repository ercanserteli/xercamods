package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercamod.common.Config;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.item.Item.Properties;

public class ItemTea extends Item
{
    public ItemTea(Properties properties) {
        super(properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isTeaEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }
}
