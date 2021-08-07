package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercamod.common.Config;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemGlass extends Item {
    public ItemGlass() {
        super((new Item.Properties()).tab(CreativeModeTab.TAB_MISC));
        this.setRegistryName("item_glass");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isFoodEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }
}
