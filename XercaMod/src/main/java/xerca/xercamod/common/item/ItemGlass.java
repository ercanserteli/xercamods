package xerca.xercamod.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ItemGlass extends Item {
    public ItemGlass() {
        super((new Item.Properties()).group(ItemGroup.MISC));
        this.setRegistryName("item_glass");
    }
}
