package xerca.xercamod.common.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemWarhammer;

public class EnchantmentMaim extends EnchantmentWithConfig {
    public EnchantmentMaim(Rarity rarityIn, EquipmentSlotType... slots) {
        super(rarityIn, EnchantmentType.WEAPON, slots);
        this.setRegistryName("enchantment_maim");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 9;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
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