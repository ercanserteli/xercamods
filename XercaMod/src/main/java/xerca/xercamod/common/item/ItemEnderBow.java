package xerca.xercamod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemEnderBow extends Item {
    public ItemEnderBow() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_BREWING).stacksTo(1).durability(160));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (getCharges(stack) > 0) {
            float range = EnchantmentHelper.getItemEnchantmentLevel(xerca.xercamod.common.item.Items.ENCHANTMENT_RANGE, stack) + 1;
            if(range > 1){
                range *= 0.8f;
            }
            worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);
            if (!worldIn.isClientSide) {
                ThrownPotion potionentity = new ThrownPotion(worldIn, playerIn);
                ItemStack potionStack = new ItemStack(isLingering(stack) ? Items.LINGERING_POTION : Items.SPLASH_POTION);
                PotionUtils.setPotion(potionStack, PotionUtils.getPotion(stack));
                potionentity.setItem(potionStack);
                potionentity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), -10.0F, 0.5F * range, 1.0F / range);
                worldIn.addFreshEntity(potionentity);

                decrementCharges(stack);
                stack.hurtAndBreak(1, playerIn, (playerEntity) -> {
                    playerEntity.broadcastBreakEvent(handIn);
                });
            }
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else {
            return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
        }
    }

    public static boolean isLingering(ItemStack itemstack){
        if(itemstack.hasTag()){
            CompoundTag tag = itemstack.getTag();
            if(tag.contains("isLinger")){
                return tag.getBoolean("isLinger");
            }
        }
        return false;
    }

    public static int getCharges(ItemStack itemstack){
        if(itemstack.hasTag()){
            CompoundTag tag = itemstack.getTag();
            if(tag.contains("charges")){
                return tag.getInt("charges");
            }
        }
        return 0;
    }

    private boolean decrementCharges(ItemStack itemstack){
        CompoundTag tag = itemstack.getOrCreateTag();
        if(tag.contains("charges")){
            int oldCharges = tag.getInt("charges");
            if(oldCharges > 0){
                if(oldCharges == 1){
                    tag.remove("Potion");
                }
                tag.putInt("charges", oldCharges - 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return PotionUtils.getPotion(stack).getName(this.getDescriptionId() + ".effect.");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        TranslatableComponent text = new TranslatableComponent("xercamod.ender_bow_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));

        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        tooltip.add(new TextComponent(getCharges(stack) + " charges").withStyle(ChatFormatting.YELLOW));
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isEnderFlaskEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench)
    {
        return ench.category == EnchantmentCategory.BREAKABLE || ench == xerca.xercamod.common.item.Items.ENCHANTMENT_RANGE
                || ench == xerca.xercamod.common.item.Items.ENCHANTMENT_CAPACITY;
    }
}
