package xerca.xercamod.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemWarhammer;

public class EnchantmentHeavy extends EnchantmentWithConfig {
    public EnchantmentHeavy(Rarity rarityIn, EquipmentSlot... slots) {
        super(rarityIn, EnchantmentCategory.WEAPON, slots);
        this.setRegistryName("enchantment_heavy");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinCost(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 10;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    @Override
    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 15;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof EnchantmentQuick);
    }

    @Override
    protected boolean isConfigEnabled() {
        return Config.isWarhammerEnabled();
    }

    @Override
    protected boolean isItemCompatible(ItemStack stack) {
        return stack.getItem() instanceof ItemWarhammer;
    }
}