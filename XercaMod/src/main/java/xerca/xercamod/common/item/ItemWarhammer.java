package xerca.xercamod.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.packets.HammerAttackPacket;
import xerca.xercamod.common.packets.HammerQuakePacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemWarhammer extends Item {
    private final float weaponDamage;
    private final float pushAmount;
    private final Tiers material;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public ItemWarhammer(String name, Tiers mat) {
        super(mat.equals(Tiers.NETHERITE)
                ? new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(1).defaultDurability(mat.getUses()).fireResistant()
                : new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(1).defaultDurability(mat.getUses()));

        this.setRegistryName(name);
        this.material = mat;
        this.weaponDamage = 1.0F + mat.getAttackDamageBonus();
        this.pushAmount = getPushFromMaterial(mat);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.weaponDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.0, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    private float getPushFromMaterial(Tiers mat) {
        float push;
        if (mat == Tiers.STONE) {
            push = 0.15f;
        } else if (mat == Tiers.IRON) {
            push = 0.3f;
        } else if (mat == Tiers.DIAMOND) {
            push = 0.4f;
        } else { //Gold and netherite have the same push
            push = 0.5f;
        }
        return push;
    }

    @Override
    public int getEnchantmentValue()
    {
        return this.material.getEnchantmentValue();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment.category == EnchantmentCategory.BREAKABLE || enchantment == Items.ENCHANTMENT_HEAVY ||
                enchantment == Items.ENCHANTMENT_QUAKE ||  enchantment == Items.ENCHANTMENT_MAIM ||
                enchantment == Items.ENCHANTMENT_QUICK ||enchantment == Items.ENCHANTMENT_UPPERCUT ||
                enchantment == Enchantments.SMITE || enchantment == Enchantments.BANE_OF_ARTHROPODS ||
                enchantment == Enchantments.MOB_LOOTING;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState blockIn, BlockPos pos, LivingEntity entityLiving) {
        if ((double) blockIn.getDestroySpeed(worldIn, pos) != 0.0D) {
            stack.hurtAndBreak(1, entityLiving, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState blockIn) {
        return blockIn.getBlock() == Blocks.STONE;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        Ingredient ingr = this.material.getRepairIngredient();
        if (ingr.test(repair)) return true;
        return super.isValidRepairItem(toRepair, repair);
    }

    @Nonnull
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot equipmentSlot, ItemStack stack) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Nonnull
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public float getFullUseSeconds(ItemStack stack){
        float seconds = 1.0f;
        if(stack.isEnchanted()){
            int heavyLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_HEAVY, stack);
            if(heavyLevel > 0){
                float multiplier = 0.1f*(heavyLevel);
                seconds += seconds*multiplier;
            }
            else{
                int quickLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_QUICK, stack);
                float multiplier = 0.12f*(quickLevel);
                seconds -= seconds*multiplier;
            }
        }
        return seconds;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        playerIn.startUsingItem(hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player)) return;

        Player player = (Player) entityLiving;

        // Number of seconds that the item has been used for
        float useSeconds = (this.getUseDuration(stack) - timeLeft) / 20.0f;
        float f = useSeconds / getFullUseSeconds(stack);
        if(f > 1.f) f = 1.f;

        if ((double) f >= 0.1D) {
            player.swing(InteractionHand.MAIN_HAND);
            if (worldIn.isClientSide) {
                Minecraft mine = Minecraft.getInstance();
                if (mine.hitResult != null){
                    if(mine.hitResult.getType() == HitResult.Type.ENTITY) {
                        Entity target = ((EntityHitResult) mine.hitResult).getEntity();
                        HammerAttackPacket pack = new HammerAttackPacket(f, target.getId());
                        XercaMod.NETWORK_HANDLER.sendToServer(pack);
                    }
                    else if(mine.hitResult.getType() == HitResult.Type.BLOCK){
                        if(EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_QUAKE, stack) > 0){
                            HammerQuakePacket pack = new HammerQuakePacket(mine.hitResult.getLocation(), f);
                            XercaMod.NETWORK_HANDLER.sendToServer(pack);
                        }
                    }
                }

            }
        }
    }

    public float getPushAmount() {
        return pushAmount;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isWarhammerEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        TranslatableComponent text = new TranslatableComponent("xercamod.warhammer_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
    }
}
