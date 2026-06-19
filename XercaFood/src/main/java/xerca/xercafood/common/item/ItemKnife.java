package xerca.xercafood.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercafood.common.Mod;

public class ItemKnife extends Item {
    private static final int MAX_DAMAGE = 240;

    ItemKnife() {
        super(new Item.Properties().setId(Mod.itemKey("knife")).stacksTo(1).durability(MAX_DAMAGE).enchantable(14));
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        ItemStack remainder = stack.copy();
        remainder.setCount(1);
        remainder.setDamageValue(stack.getDamageValue() + 1);

        if (remainder.getDamageValue() >= remainder.getMaxDamage()) {
            return ItemStack.EMPTY;
        }

        return remainder;
    }
}
