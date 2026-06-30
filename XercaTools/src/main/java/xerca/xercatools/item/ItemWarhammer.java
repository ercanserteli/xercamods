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
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xerca.xercatools.enchantment.WarhammerEnchantments;

import java.util.List;
import java.util.Optional;

public class ItemWarhammer extends Item {
    private final float pushAmount;
    private final ToolMaterial material;

    public static ItemAttributeModifiers createAttributes(float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public ItemWarhammer(ToolMaterial mat, String name) {
        super(mat == ToolMaterial.NETHERITE
                ? new Item.Properties().setId(xerca.xercatools.Mod.itemKey(name)).stacksTo(1).durability(mat.durability()).fireResistant().enchantable(mat.enchantmentValue()).repairable(mat.repairItems()).attributes(createAttributes(1.0F + mat.attackDamageBonus(), -3.0F))
                : new Item.Properties().setId(xerca.xercatools.Mod.itemKey(name)).stacksTo(1).durability(mat.durability()).enchantable(mat.enchantmentValue()).repairable(mat.repairItems()).attributes(createAttributes(1.0F + mat.attackDamageBonus(), -3.0F)));

        this.material = mat;
        this.pushAmount = getPushFromMaterial(mat);
    }

    private float getPushFromMaterial(ToolMaterial mat) {
        if (mat == ToolMaterial.STONE) return 0.15f;
        if (mat == ToolMaterial.IRON) return 0.3f;
        if (mat == ToolMaterial.DIAMOND) return 0.4f;
        return 0.5f; // Gold and Netherite
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState blockIn, BlockPos pos, LivingEntity entityLiving) {
        if (blockIn.getDestroySpeed(worldIn, pos) != 0.0D) {
            stack.hurtAndBreak(1, entityLiving, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        playerIn.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide || !(entity instanceof Player)) return;
        int windBurstLevel = EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.WIND_BURST), stack);
        if (windBurstLevel <= 0) return;

        float threshold = 0.9f * getFullUseSeconds(level.registryAccess(), stack);
        float used = (getUseDuration(stack, entity) - remainingUseDuration) / 20.0f;
        float prevUsed = (getUseDuration(stack, entity) - (remainingUseDuration + 1)) / 20.0f;
        if (used >= threshold && prevUsed < threshold) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    net.minecraft.sounds.SoundEvents.BREEZE_CHARGE, SoundSource.PLAYERS, 0.8F, 1.0F);
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) return false;
        if (worldIn.isClientSide) return false;

        float useSeconds = (this.getUseDuration(stack, entityLiving) - timeLeft) / 20.0f;
        float f = useSeconds / getFullUseSeconds(worldIn.registryAccess(), stack);
        if (f > 1.f) f = 1.f;

        if (f >= 0.1D) {
            InteractionHand hand = player.getUsedItemHand();
            EquipmentSlot slot = hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            player.swing(hand, true);

            boolean hitTarget = false;
            EntityHitResult entityHitResult = findLivingEntityHit(player, worldIn, 5.0D);
            if (entityHitResult != null) {
                Entity target = entityHitResult.getEntity();
                if (target instanceof LivingEntity livingTarget) {
                    attackEntity(player, stack, livingTarget, f, worldIn, slot);
                    hitTarget = true;
                }
            } else {
                Vec3 start = player.getEyePosition(1.0F);
                Vec3 end = start.add(player.getViewVector(1.0F).scale(5.0D));
                HitResult blockHitResult = worldIn.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    performQuake(player, stack, blockHitResult.getLocation(), f, worldIn, slot);
                }
                fireWindBurst(player, stack, f, worldIn);
            }

            int dashLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.dashingEnchantment(worldIn.registryAccess()), stack);
            if (dashLevel > 0) {
                WarhammerDashManager.startDash(player, stack, slot, f, dashLevel, hitTarget);
            }
            return true;
        }
        return false;
    }

    static @Nullable EntityHitResult findLivingEntityHit(Player player, Level level, double range) {
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

        return closestEntity != null ? new EntityHitResult(closestEntity, closestHitPos) : null;
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

    private static int getDensityLevel(RegistryAccess registryAccess, ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(
                registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.DENSITY), stack);
    }

    public static float getFullUseSeconds(RegistryAccess registryAccess, ItemStack stack) {
        float seconds = 1.0f;
        int densityLevel = getDensityLevel(registryAccess, stack);
        if (densityLevel > 0) {
            seconds += seconds * 0.1f * densityLevel;
        } else {
            int quickLevel = EnchantmentHelper.getItemEnchantmentLevel(WarhammerEnchantments.quickEnchantment(registryAccess), stack);
            if (quickLevel > 0) {
                seconds -= seconds * 0.12f * quickLevel;
            }
        }
        return Math.max(0.1f, seconds);
    }

    static void attackEntity(Player player, ItemStack stack, LivingEntity target, float pullDuration, Level level, EquipmentSlot slot) {
        float mult = damageBonusMult(pullDuration);
        int densityLevel = getDensityLevel(level.registryAccess(), stack);
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        float damage = ((float) (attackDamage != null ? attackDamage.getValue() : 0) + densityLevel * 0.5f) * mult;
        float push = (((ItemWarhammer) stack.getItem()).pushAmount + densityLevel * 0.15f) * 2.0f * mult;

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

        level.playSound(null, target.getX(), target.getY() + 0.5d, target.getZ(), xerca.xercatools.SoundEvents.HAMMER, SoundSource.PLAYERS, 1.0f, level.random.nextFloat() * 0.1F + 0.4F + (2.0f / (damage + densityLevel)));
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
        int densityLevel = getDensityLevel(level.registryAccess(), stack);
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        float damage = ((float) (attackDamage != null ? attackDamage.getValue() : 0) + densityLevel * 0.5f) * mult * 0.5f;
        float push = (((ItemWarhammer) stack.getItem()).pushAmount + densityLevel * 0.15f) * mult;
        float pitch = 2.0f / (damage + densityLevel);

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

    private static void fireWindBurst(Player player, ItemStack stack, float pullDuration, Level level) {
        if (pullDuration <= 0.9f) {
            return;
        }
        int windBurstLevel = EnchantmentHelper.getItemEnchantmentLevel(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.WIND_BURST), stack);
        if (windBurstLevel <= 0) {
            return;
        }

        WindCharge windCharge = new WindCharge(player, level, player.getX(), player.getEyePosition().y, player.getZ());
        float velocity = 1.0f + 0.5f * windBurstLevel;
        windCharge.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
        level.addFreshEntity(windCharge);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.WIND_CHARGE_THROW,
                SoundSource.PLAYERS, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        MutableComponent text = Component.translatable("xercatools.warhammer_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
    }
}
