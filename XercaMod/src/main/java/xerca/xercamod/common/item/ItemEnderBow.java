package xerca.xercamod.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemEnderBow extends Item {
    private static final int baseMaxCharges = 8;

    public ItemEnderBow() {
        super(new Item.Properties().group(ItemGroup.BREWING).maxStackSize(1).maxDamage(160));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (getCharges(stack) > 0) {
            float range = EnchantmentHelper.getEnchantmentLevel(xerca.xercamod.common.item.Items.ENCHANTMENT_RANGE, stack) + 1;
            if(range > 1){
                range *= 0.8f;
            }
            worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            if (!worldIn.isRemote) {
                PotionEntity potionentity = new PotionEntity(worldIn, playerIn);
                ItemStack potionStack = new ItemStack(isLingering(stack) ? Items.LINGERING_POTION : Items.SPLASH_POTION);
                PotionUtils.addPotionToItemStack(potionStack, PotionUtils.getPotionFromItem(stack));
                potionentity.setItem(potionStack);
                potionentity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -10.0F, 0.5F * range, 1.0F / range);
                worldIn.addEntity(potionentity);

                decrementCharges(stack);
                stack.damageItem(1, playerIn, (playerEntity) -> {
                    playerEntity.sendBreakAnimation(handIn);
                });
            }
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        } else {
            return new ActionResult<>(ActionResultType.FAIL, stack);
        }
    }

    public static boolean isLingering(ItemStack itemstack){
        if(itemstack.hasTag()){
            CompoundNBT tag = itemstack.getTag();
            if(tag.contains("isLinger")){
                return tag.getBoolean("isLinger");
            }
        }
        return false;
    }

    public static int getCharges(ItemStack itemstack){
        if(itemstack.hasTag()){
            CompoundNBT tag = itemstack.getTag();
            if(tag.contains("charges")){
                return tag.getInt("charges");
            }
        }
        return 0;
    }

    private boolean decrementCharges(ItemStack itemstack){
        CompoundNBT tag = itemstack.getOrCreateTag();
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
    public String getTranslationKey(ItemStack stack) {
        return PotionUtils.getPotionFromItem(stack).getNamePrefixed(this.getTranslationKey() + ".effect.");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        tooltip.add(new StringTextComponent(getCharges(stack) + " charges").func_240699_a_(TextFormatting.YELLOW));
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return super.hasEffect(stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isEnderFlaskEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench)
    {
        return ench.type == EnchantmentType.BREAKABLE || ench == xerca.xercamod.common.item.Items.ENCHANTMENT_RANGE
                || ench == xerca.xercamod.common.item.Items.ENCHANTMENT_CAPACITY;
    }
}
