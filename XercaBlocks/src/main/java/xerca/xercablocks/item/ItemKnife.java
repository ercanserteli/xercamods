package xerca.xercablocks.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

public final class ItemKnife extends Item {
    private static final int MAX_DAMAGE = 240;

    public ItemKnife() {
        super(new Item.Properties().stacksTo(1).durability(MAX_DAMAGE));
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.IRON.getEnchantmentValue();
    }
}
