package xerca.xercamod.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import xerca.xercamod.common.item.ItemKnife;

public class EnchantmentPoison extends Enchantment {
    public EnchantmentPoison(Rarity rarityIn, EquipmentSlot... slots) {
        super(rarityIn, EnchantmentCategory.WEAPON, slots);
        this.setRegistryName("enchantment_poison");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinCost(int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 9;
    }

    /**
     * Returns the maximum value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 30;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel() {
        return 3;
    }

    /**
     * This applies specifically to applying at the enchanting table. The other method {@link #canApply(ItemStack)}
     * applies for <i>all possible</i> enchantments.
     * @param stack
     * @return
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ItemKnife;
    }
    /**
     * This applies specifically to applying at the enchanting table. The other method {@link #canApply(ItemStack)}
     * applies for <i>all possible</i> enchantments.
     * @param stack
     * @return
     */
    @Override
    public boolean canEnchant(ItemStack stack) {
        return this.canApplyAtEnchantingTable(stack);
    }
}