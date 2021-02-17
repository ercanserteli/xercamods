package xerca.xercamod.common.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.item.ItemEnderBow;
import xerca.xercamod.common.item.ItemFlask;

public class EnchantmentCapacity extends EnchantmentWithConfig {
    public EnchantmentCapacity(Rarity rarityIn, EquipmentSlotType... slots) {
        super(rarityIn, EnchantmentType.WEAPON, slots);
        this.setRegistryName("enchantment_capacity");
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 9;
    }

    /**
     * Returns the maximum value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 30;
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