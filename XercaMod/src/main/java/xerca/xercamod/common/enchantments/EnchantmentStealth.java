package xerca.xercamod.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.item.ItemKnife;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class EnchantmentStealth extends Enchantment {
    public EnchantmentStealth(Rarity rarityIn, EquipmentSlot... slots) {
        super(rarityIn, EnchantmentCategory.WEAPON, slots);
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 9;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ItemKnife;
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack stack) {
        return this.canApplyAtEnchantingTable(stack);
    }
}