package xerca.xercamod.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.entity.EntityHook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static xerca.xercamod.common.item.Items.ENCHANTMENT_GRAPPLING;
import static xerca.xercamod.common.item.Items.ENCHANTMENT_GUILLOTINE;

public class ItemGrabHook extends FishingRodItem {

    public ItemGrabHook() {
        super((new Item.Properties()).group(ItemGroup.COMBAT).defaultMaxDamage(210));
        this.setRegistryName("item_grab_hook");
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        playerIn.setActiveHand(hand);
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        float useSeconds = (this.getUseDuration(stack) - timeLeft) / 20.0f;
        if(useSeconds > 1.f) useSeconds = 1.f;

        if(useSeconds > 0.1f && entityLiving instanceof PlayerEntity){
            PlayerEntity playerIn = (PlayerEntity) entityLiving;
            Hand hand;
            if(playerIn.getHeldItemMainhand().getItem() instanceof ItemGrabHook){
                hand = Hand.MAIN_HAND;
            }
            else if(playerIn.getHeldItemOffhand().getItem() instanceof ItemGrabHook){
                hand = Hand.OFF_HAND;
            }
            else{
                return;
            }

            final ItemStack heldItem = playerIn.getHeldItem(hand);
            playerIn.getCooldownTracker().setCooldown(this, 40);
            heldItem.damageItem(1, playerIn, (p) -> p.sendBreakAnimation(hand));
            if (!worldIn.isRemote) {
                worldIn.addEntity(new EntityHook(worldIn, playerIn, heldItem, useSeconds));
            }

            playerIn.swingArm(hand);
        }
    }

    @Nonnull
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment.type == EnchantmentType.BREAKABLE || enchantment == Items.ENCHANTMENT_GRAPPLING;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
//        XercaMod.LOGGER.warn("GRAB_HOOK_ENABLE: " + Config.GRAB_HOOK_ENABLE.get());
        if(!Config.isGrabHookEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        TranslationTextComponent text = new TranslationTextComponent("xercamod.grap_hook_tooltip");
        tooltip.add(text.mergeStyle(TextFormatting.BLUE));
        if(EnchantmentHelper.getEnchantmentLevel(ENCHANTMENT_GRAPPLING, stack) > 0){
            TranslationTextComponent textGrappling = new TranslationTextComponent("xercamod.grappling_tooltip");
            tooltip.add(textGrappling.mergeStyle(TextFormatting.YELLOW));
        }
    }
}
