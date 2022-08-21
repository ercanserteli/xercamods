package xerca.xercamod.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemEnderBow;
import xerca.xercamod.common.item.ItemFlask;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class EnchantmentCapacity extends EnchantmentWithConfig {
    public EnchantmentCapacity(Rarity rarityIn, EquipmentSlot... slots) {
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

    @Override
    protected boolean isConfigEnabled(){
        return Config.isEnderFlaskEnabled();
    }

    @Override
    protected boolean isItemCompatible(ItemStack stack){
        return stack.getItem() instanceof ItemEnderBow || stack.getItem() instanceof ItemFlask;
    }
}