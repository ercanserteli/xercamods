package xerca.xercafood.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

public class ItemDrink extends ItemStackableContainedFood {

    public ItemDrink(Properties properties, Item container) {
        super(properties, container, 16);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }
}
