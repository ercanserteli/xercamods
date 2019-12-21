package xerca.xercamod.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xerca.xercamod.common.Config;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemTea extends Item
{
    public ItemTea(Properties properties) {
        super(properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isTeaEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }
}
