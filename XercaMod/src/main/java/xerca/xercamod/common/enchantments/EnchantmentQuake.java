package xerca.xercamod.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemWarhammer;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class EnchantmentQuake extends EnchantmentWithConfig {
    public EnchantmentQuake(Rarity rarityIn, EquipmentSlot... slots) {
        super(rarityIn, EnchantmentCategory.WEAPON, slots);
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinCost(int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 9;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    @Override
    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel() {
        return 3;
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