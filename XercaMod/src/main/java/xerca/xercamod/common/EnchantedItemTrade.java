package xerca.xercamod.common;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;

import java.util.Random;

public class EnchantedItemTrade implements VillagerTrades.ITrade {
    private final ItemStack sellingStack;
    private final int emeraldCount;
    private final int maxUses;
    private final int xpValue;
    private final float priceMultiplier;

    public EnchantedItemTrade(Item p_i50535_1_, int emeraldCount, int maxUses, int xpValue) {
        this(p_i50535_1_, emeraldCount, maxUses, xpValue, 0.05F);
    }

    public EnchantedItemTrade(Item sellItem, int emeraldCount, int maxUses, int xpValue, float priceMultiplier) {
        this.sellingStack = new ItemStack(sellItem);
        this.emeraldCount = emeraldCount;
        this.maxUses = maxUses;
        this.xpValue = xpValue;
        this.priceMultiplier = priceMultiplier;
    }

    public MerchantOffer getOffer(Entity trader, Random rand) {
        int i = 5 + rand.nextInt(15);
        ItemStack itemstack = EnchantmentHelper.addRandomEnchantment(rand, new ItemStack(this.sellingStack.getItem()), i, false);
        int j = Math.min(this.emeraldCount + i, 64);
        ItemStack itemstack1 = new ItemStack(Items.EMERALD, j);
        return new MerchantOffer(itemstack1, itemstack, this.maxUses, this.xpValue, this.priceMultiplier);
    }
}
