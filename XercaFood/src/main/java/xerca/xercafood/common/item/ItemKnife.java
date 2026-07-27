package xerca.xercafood.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

public class ItemKnife extends Item {
    private static final int MAX_DAMAGE = 240;

    ItemKnife() {
        super(new Item.Properties().stacksTo(1).durability(MAX_DAMAGE));
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.IRON.getEnchantmentValue();
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack remainder = stack.copy();
        remainder.setCount(1);
        remainder.setDamageValue(stack.getDamageValue() + 1);

        if (remainder.getDamageValue() >= remainder.getMaxDamage()) {
            return ItemStack.EMPTY;
        }

        return remainder;
    }
}
