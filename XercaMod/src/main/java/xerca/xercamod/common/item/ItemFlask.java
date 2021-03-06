package xerca.xercamod.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemFlask extends Item {
    private static final int baseMaxCharges = 16;
    private boolean hasMilk;

    public ItemFlask(Properties properties, String registryName, boolean hasMilk) {
        super(properties);
        this.setRegistryName(registryName);
        this.hasMilk = hasMilk;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getUseDuration(ItemStack stack) {
        int chug = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_CHUG, stack);
        switch (chug){
            case 2:
                return 10;
            case 1:
                return 21;
            default:
                return 32;
        }
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (getCharges(itemstack) > 0) {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        } else {
            return new ActionResult<>(ActionResultType.FAIL, itemstack);
        }
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, LivingEntity entityLiving) {
        int charges = getCharges(stack);
        if(charges > 0){
            if (entityLiving instanceof PlayerEntity) {
                PlayerEntity entityplayer = (PlayerEntity)entityLiving;
                if (!worldIn.isRemote) {
                    if(hasMilk){
                        entityplayer.curePotionEffects(new ItemStack(net.minecraft.item.Items.MILK_BUCKET));
                    }
                    else{
                        for(EffectInstance potioneffect : PotionUtils.getEffectsFromStack(stack)) {
                            if (potioneffect.getPotion().isInstant()) {
                                potioneffect.getPotion().affectEntity(entityplayer, entityplayer, entityLiving, potioneffect.getAmplifier(), 1.0D);
                            } else {
                                entityLiving.addPotionEffect(new EffectInstance(potioneffect));
                            }
                        }
                    }
                }
                decrementCharges(stack);

                stack.damageItem(1, entityplayer, (playerEntity) -> {
                    playerEntity.sendBreakAnimation(playerEntity.getActiveHand());
                });
            }
        }

        if(hasMilk && getCharges(stack) <= 0){
            CompoundNBT tag = stack.getTag();
            stack = new ItemStack(Items.FLASK);
            stack.setTag(tag);
        }
        return stack;
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

    public static int getMaxCharges(ItemStack itemstack){
        int cap = EnchantmentHelper.getEnchantmentLevel(xerca.xercamod.common.item.Items.ENCHANTMENT_CAPACITY, itemstack);
        return baseMaxCharges * (cap + 1);
    }

    public static void setCharges(ItemStack itemstack, int charges){
        if(charges >= 0 && charges <= getMaxCharges(itemstack)){
            CompoundNBT tag = itemstack.getOrCreateTag();
            tag.putInt("charges", charges);
        }
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

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    @Override
    public String getTranslationKey(ItemStack stack) {
        if(hasMilk){
            return this.getTranslationKey();
        }
        return PotionUtils.getPotionFromItem(stack).getNamePrefixed(this.getTranslationKey() + ".effect.");
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        TranslationTextComponent text = new TranslationTextComponent("xercamod.ender_flask_tooltip");
        tooltip.add(text.mergeStyle(TextFormatting.BLUE));

        if(hasMilk){
            tooltip.add(new StringTextComponent("Calcium for your bones").mergeStyle(TextFormatting.YELLOW));
        }
        else{
            PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        }
        tooltip.add(new StringTextComponent(getCharges(stack) + " charges").mergeStyle(TextFormatting.YELLOW));
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
     * but other items can override it (for instance, written books always return true).
     *
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
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
        return ench.type == EnchantmentType.BREAKABLE
                || ench == xerca.xercamod.common.item.Items.ENCHANTMENT_CAPACITY
                || ench == xerca.xercamod.common.item.Items.ENCHANTMENT_CHUG;
    }
}
