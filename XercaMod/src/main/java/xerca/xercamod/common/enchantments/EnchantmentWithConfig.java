package xerca.xercamod.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public abstract class EnchantmentWithConfig extends Enchantment {
    protected EnchantmentWithConfig(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
        super(rarityIn, typeIn, slots);
    }

    protected abstract boolean isConfigEnabled();

    protected abstract boolean isItemCompatible(ItemStack stack);

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return isConfigEnabled() && isItemCompatible(stack);
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return this.canApplyAtEnchantingTable(stack);
    }

    /**
     * Checks if the enchantment can be sold by villagers in their trades.
     */
    public boolean canVillagerTrade() {
        return isConfigEnabled();
    }

    /**
     * Checks if the enchantment can be applied to loot table drops.
     */
    public boolean canGenerateInLoot() {
        return isConfigEnabled();
    }

    /**
     * Is this enchantment allowed to be enchanted on books via Enchantment Table
     * @return false to disable the vanilla feature
     */
    public boolean isAllowedOnBooks() {
        return isConfigEnabled();
    }
}
