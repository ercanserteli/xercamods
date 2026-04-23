package xerca.xercatools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xerca.xercatools.enchantment.WarhammerEnchantments;

import java.util.List;
import java.util.Optional;

public class ItemWarhammer extends Item {
    private final float pushAmount;
    private final Tier material;

    public static ItemAttributeModifiers createAttributes(Tier material, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public ItemWarhammer(Tier mat) {
        super(mat == Tiers.NETHERITE 
                ? new Item.Properties().stacksTo(1).durability(mat.getUses()).fireResistant().attributes(createAttributes(mat, 1.0F + mat.getAttackDamageBonus(), -3.0F))
                : new Item.Properties().stacksTo(1).durability(mat.getUses()).attributes(createAttributes(mat, 1.0F + mat.getAttackDamageBonus(), -3.0F)));
        
        this.material = mat;
        this.pushAmount = getPushFromMaterial(mat);
    }

    private float getPushFromMaterial(Tier mat) {
        if (mat == Tiers.STONE) return 0.15f;
        if (mat == Tiers.IRON) return 0.3f;
        if (mat == Tiers.DIAMOND) return 0.4f;
        return 0.5f; // Gold and Netherite
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.getEnchantmentValue();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level worldIn, BlockState blockIn, @NotNull BlockPos pos, @NotNull LivingEntity entityLiving) {
        if (blockIn.getDestroySpeed(worldIn, pos) != 0.0D) {
            stack.hurtAndBreak(1, entityLiving, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        Ingredient ingr = this.material.getRepairIngredient();
        if (ingr.test(repair)) return true;
        return super.isValidRepairItem(toRepair, repair);
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        playerIn.startUsingItem(hand);
        return InteractionResultHolder.consume(heldItem);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) return;
        if (worldIn.isClientSide) return;

        float useSeconds = (this.getUseDuration(stack, entityLiving) - timeLeft) / 20.0f;
        float f = useSeconds / getFullUseSeconds(worldIn.registryAccess(), stack);
        if (f > 1.f) f = 1.f;

        if (f >= 0.1D) {
            InteractionHand hand = player.getUsedItemHand();
            EquipmentSlot slot = hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            player.swing(hand, true);

            EntityHitResult entityHitResult = findLivingEntityHit(player, worldIn, 5.0D);
            if (entityHitResult != null) {
                Entity target = entityHitResult.getEntity();
                if (target instanceof LivingEntity livingTarget) {
                    attackEntity(player, stack, livingTarget, f, worldIn, slot);
                }
            } else {
                Vec3 start = player.getEyePosition(1.0F);
                Vec3 end = start.add(player.getViewVector(1.0F).scale(5.0D));
                HitResult blockHitResult = worldIn.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    performQuake(player, stack, blockHitResult.getLocation(), f, worldIn, slot);
                }
            }
        }
    }

    public float getPushAmount() {
        return pushAmount;
    }

    private static EntityHitResult findLivingEntityHit(Player player, Level level, double range) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getViewVector(1.0F).scale(range));
        AABB searchBox = player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1.0D);
        Entity closestEntity = null;
        Vec3 closestHitPos = null;
        double closestDistance = range * range;

        for (Entity candidate : level.getEntities(player, searchBox, entity -> entity instanceof LivingEntity living && living.isAlive() && entity.isPickable())) {
            AABB targetBox = candidate.getBoundingBox().inflate(candidate.getPickRadius());
            Optional<Vec3> clipped = targetBox.clip(start, end);
            if (targetBox.contains(start)) {
                closestEntity = candidate;
                closestHitPos = clipped.orElse(start);
                closestDistance = 0.0D;
            } else if (clipped.isPresent()) {
                double hitDistance = start.distanceToSqr(clipped.get());
                if (hitDistance < closestDistance) {
                    closestEntity = candidate;
                    closestHitPos = clipped.get();
                    closestDistance = hitDistance;
                }
            }
        }

        return closestEntity != null && closestHitPos != null ? new EntityHitResult(closestEntity, closestHitPos) : null;
    }

    private static float damageBonusMult(float pullDuration) {
        if (pullDuration >= 0.95F) {
            return 1.75f;
        } else if (pullDuration >= 0.7F) {
            return 1.0f;
        } else if (pullDuration >= 0.4F) {
            return 0.75f;
        } else {
            return 0.5f;
        }
    }

    public static float getFullUseSeconds(RegistryAccess registryAccess, ItemStack stack) {
        float seconds = 1.0f;
        int heavyLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.heavyEnchantment(registryAccess), stack);
        if (heavyLevel > 0) {
            seconds += seconds * 0.1f * heavyLevel;
        } else {
            int quickLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.quickEnchantment(registryAccess), stack);
            if (quickLevel > 0) {
                seconds -= seconds * 0.12f * quickLevel;
            }
        }
        return Math.max(0.1f, seconds);
    }

    private static void attackEntity(Player player, ItemStack stack, LivingEntity target, float pullDuration, Level level, EquipmentSlot slot) {
        float mult = damageBonusMult(pullDuration);
        int heavyLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.heavyEnchantment(level.registryAccess()), stack);
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        float damage = ((float) (attackDamage != null ? attackDamage.getValue() : 0) + heavyLevel * 0.5f) * mult;
        float push = (((ItemWarhammer) stack.getItem()).pushAmount + heavyLevel * 0.15f) * 2.0f * mult;

        int uppercutLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.uppercutEnchantment(level.registryAccess()), stack);
        double bonusVelY = (uppercutLevel * 0.25d) * pullDuration;

        if (pullDuration > 0.9F && player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() &&
                !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && !player.isSprinting()) {
            damage *= 1.5F;
            level.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
            player.crit(target);
        }

        int smiteLevel = EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SMITE), stack);
        int baneLevel = EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BANE_OF_ARTHROPODS), stack);
        if (target.getType().is(EntityTypeTags.UNDEAD)) {
            damage += 2.5f * smiteLevel;
        } else if (target.getType().is(EntityTypeTags.ARTHROPOD)) {
            damage += 2.5f * baneLevel;
        }

        level.playSound(null, target.getX(), target.getY() + 0.5d, target.getZ(), xerca.xercatools.SoundEvents.HAMMER, SoundSource.PLAYERS, 1.0f, level.random.nextFloat() * 0.1F + 0.4F + (2.0f / (damage + heavyLevel)));
        stack.hurtAndBreak(1, player, slot);
        target.hurt(player.damageSources().playerAttack(player), damage);

        int maimLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.maimEnchantment(level.registryAccess()), stack);
        if (maimLevel > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100 + 40 * maimLevel, maimLevel - 1));
        }

        Vec3 knockVector = target.position().subtract(player.position()).normalize().scale(push);
        target.push(knockVector.x, knockVector.y + bonusVelY, knockVector.z);
        target.hurtMarked = true;
    }

    private static void performQuake(Player player, ItemStack stack, Vec3 position, float pullDuration, Level level, EquipmentSlot slot) {
        int quakeLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.quakeEnchantment(level.registryAccess()), stack);
        if (quakeLevel <= 0) {
            return;
        }

        double range = switch (quakeLevel) {
            case 1 -> 9.0;
            case 2 -> 16.0;
            case 3 -> 25.0;
            default -> 0.0;
        };
        if (range <= 0.0D) {
            return;
        }

        float mult = damageBonusMult(pullDuration);
        int heavyLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.heavyEnchantment(level.registryAccess()), stack);
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        float damage = ((float) (attackDamage != null ? attackDamage.getValue() : 0) + heavyLevel * 0.5f) * mult * 0.5f;
        float push = (((ItemWarhammer) stack.getItem()).pushAmount + heavyLevel * 0.15f) * mult;
        float pitch = 2.0f / (damage + heavyLevel);

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                new net.minecraft.world.phys.AABB(player.position().subtract(5, 5, 5), player.position().add(5, 5, 5)),
                entity -> !entity.is(player) && entity.position().distanceToSqr(position) < range);
        for (LivingEntity target : targets) {
            Vec3 knockvec = target.position().subtract(position).normalize().scale(push);
            target.push(knockvec.x, knockvec.y, knockvec.z);
            target.hurt(player.damageSources().playerAttack(player), damage);
            target.hurtMarked = true;
        }

        spawnQuakeParticles(level, position, pullDuration);
        level.playSound(null, position.x, position.y, position.z, xerca.xercatools.SoundEvents.STOMP, SoundSource.PLAYERS,
                (float) Math.min(1.0, Math.log10(10.0 * pullDuration + 1.0)), level.random.nextFloat() * 0.1F + 0.4F + pitch);

        if (!targets.isEmpty()) {
            stack.hurtAndBreak(1, player, slot);
        }
    }

    private static void spawnQuakeParticles(Level level, Vec3 position, float pullDuration) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        int particleCount = (int) (Math.min(1.0, Math.log10(10.0 * pullDuration + 1.0)) * 64.0);
        for (int i = 0; i < particleCount; i++) {
            double posX = position.x + level.random.nextGaussian();
            double posZ = position.z + level.random.nextGaussian();
            Vec3 particlePos = new Vec3(posX, position.y, posZ);
            Vec3 particleVel = particlePos.subtract(position).normalize().scale(0.15);
            serverLevel.sendParticles(ParticleTypes.SMOKE, posX, position.y, posZ, 1, particleVel.x, 0.01D, particleVel.z, 0.0D);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        MutableComponent text = Component.translatable("xercatools.warhammer_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
    }
}
