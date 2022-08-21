package xerca.xercamod.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public abstract class EnchantmentWithConfig extends Enchantment {
    protected EnchantmentWithConfig(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    protected abstract boolean isConfigEnabled();

    protected abstract boolean isItemCompatible(ItemStack stack);

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return isConfigEnabled() && isItemCompatible(stack);
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack stack) {
        return this.canApplyAtEnchantingTable(stack);
    }

    /**
     * Checks if the enchantment can be sold by villagers in their trades.
     */
    public boolean isTradeable() {
        return isConfigEnabled();
    }

    /**
     * Checks if the enchantment can be applied to loot table drops.
     */
    public boolean isDiscoverable() {
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
