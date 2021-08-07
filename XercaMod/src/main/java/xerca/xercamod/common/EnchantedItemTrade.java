package xerca.xercamod.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Random;

public class EnchantedItemTrade implements VillagerTrades.ItemListing {
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
        ItemStack itemstack = EnchantmentHelper.enchantItem(rand, new ItemStack(this.sellingStack.getItem()), i, false);
        int j = Math.min(this.emeraldCount + i, 64);
        ItemStack itemstack1 = new ItemStack(Items.EMERALD, j);
        return new MerchantOffer(itemstack1, itemstack, this.maxUses, this.xpValue, this.priceMultiplier);
    }
}
