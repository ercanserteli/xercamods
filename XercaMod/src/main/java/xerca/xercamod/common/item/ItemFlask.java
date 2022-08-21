package xerca.xercamod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemFlask extends Item {
    private static final int baseMaxCharges = 16;
    private final boolean hasMilk;

    public ItemFlask(Properties properties, boolean hasMilk) {
        super(properties);
        this.hasMilk = hasMilk;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        int chug = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_CHUG.get(), stack);
        return switch (chug) {
            case 2 -> 10;
            case 1 -> 21;
            default -> 32;
        };
    }

    /**
     * returns the action that specifies what animation to play when the item is being used
     */
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (getCharges(itemstack) > 0) {
            playerIn.startUsingItem(handIn);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        } else {
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
        }
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public @NotNull ItemStack finishUsingItem(@Nonnull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        int charges = getCharges(stack);
        if(charges > 0){
            if (entityLiving instanceof Player entityplayer) {
                if (!worldIn.isClientSide) {
                    if(hasMilk){
                        entityplayer.curePotionEffects(new ItemStack(net.minecraft.world.item.Items.MILK_BUCKET));
                    }
                    else{
                        for(MobEffectInstance potioneffect : PotionUtils.getMobEffects(stack)) {
                            if (potioneffect.getEffect().isInstantenous()) {
                                potioneffect.getEffect().applyInstantenousEffect(entityplayer, entityplayer, entityLiving, potioneffect.getAmplifier(), 1.0D);
                            } else {
                                entityLiving.addEffect(new MobEffectInstance(potioneffect));
                            }
                        }
                    }
                }
                decrementCharges(stack);

                int useDuration = getUseDuration(stack);
                if(useDuration < 32){
                    entityplayer.getCooldowns().addCooldown(this, (32 - useDuration)/2);
                }

                stack.hurtAndBreak(1, entityplayer, (playerEntity) -> playerEntity.broadcastBreakEvent(playerEntity.getUsedItemHand()));
            }
        }

        if(hasMilk && getCharges(stack) <= 0){
            CompoundTag tag = stack.getTag();
            stack = new ItemStack(Items.FLASK.get());
            stack.setTag(tag);
        }
        return stack;
    }

    public static int getCharges(ItemStack itemstack){
        if(itemstack.hasTag() && itemstack.getTag() != null){
            CompoundTag tag = itemstack.getTag();
            if(tag.contains("charges")){
                return tag.getInt("charges");
            }
        }
        return 0;
    }

    public static int getMaxCharges(ItemStack itemstack){
        int cap = EnchantmentHelper.getItemEnchantmentLevel(xerca.xercamod.common.item.Items.ENCHANTMENT_CAPACITY.get(), itemstack);
        return baseMaxCharges * (cap + 1);
    }

    public static void setCharges(ItemStack itemstack, int charges){
        if(charges >= 0 && charges <= getMaxCharges(itemstack)){
            CompoundTag tag = itemstack.getOrCreateTag();
            tag.putInt("charges", charges);
        }
    }

    private void decrementCharges(ItemStack itemstack){
        CompoundTag tag = itemstack.getOrCreateTag();
        if(tag.contains("charges")){
            int oldCharges = tag.getInt("charges");
            if(oldCharges > 0){
                if(oldCharges == 1){
                    tag.remove("Potion");
                }
                tag.putInt("charges", oldCharges - 1);
            }
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        if(hasMilk){
            return this.getDescriptionId();
        }
        return PotionUtils.getPotion(stack).getName(this.getDescriptionId() + ".effect.");
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        MutableComponent text = Component.translatable("xercamod.ender_flask_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));

        if(hasMilk){
            tooltip.add(Component.literal("Calcium for your bones").withStyle(ChatFormatting.YELLOW));
        }
        else{
            PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        }
        tooltip.add(Component.literal(getCharges(stack) + " charges").withStyle(ChatFormatting.YELLOW));
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
     * but other items can override it (for instance, written books always return true).
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
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
        return ench.category == EnchantmentCategory.BREAKABLE
                || ench == xerca.xercamod.common.item.Items.ENCHANTMENT_CAPACITY.get()
                || ench == xerca.xercamod.common.item.Items.ENCHANTMENT_CHUG.get();
    }
}
