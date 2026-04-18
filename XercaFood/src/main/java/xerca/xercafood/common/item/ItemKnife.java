package xerca.xercafood.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

public class ItemKnife extends Item {
    private static final int maxDamage = 240;

    ItemKnife() {
        super(new Item.Properties().stacksTo(1).durability(maxDamage));
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.IRON.getEnchantmentValue();
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        ItemStack ret = stack.copy();
        ret.setCount(1);
        ret.setDamageValue(stack.getDamageValue() + 1);
        if (ret.getDamageValue() >= ret.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        return ret;
    }
}
