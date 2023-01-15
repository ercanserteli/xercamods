package xerca.xercafood.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class ItemDrink extends ItemStackableContainedFood {

    public ItemDrink(Properties properties, Item container) {
        super(properties, container, 16);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
}
