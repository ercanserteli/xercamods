package xerca.xercamod.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xerca.xercamod.common.Config;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemGlass extends Item {
    public ItemGlass() {
        super((new Item.Properties()).group(ItemGroup.MISC));
        this.setRegistryName("item_glass");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isFoodEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }
}
