package xerca.xercatools.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.enchantment.ScytheEnchantments;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ItemScythe extends Item {
    private final Tier tier;

    public static ItemAttributeModifiers createAttributes(Tier tier) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 3.0F + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.6F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public ItemScythe(Tier tier) {
        super(tier == Tiers.NETHERITE
                ? new Item.Properties().durability(tier.getUses()).fireResistant().attributes(createAttributes(tier))
                : new Item.Properties().durability(tier.getUses()).attributes(createAttributes(tier)));
        this.tier = tier;
    }

    public static float getFullUseSeconds(ItemStack stack) {
        return 1.0F;
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state) ? 1.0F : 0.0F;
        }
        return isHarvestablePlant(state) ? 1.0F : super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        if (entity instanceof Player player && isHarvestablePlant(state)) {
            harvestNeighbourCrops(level, pos, player, stack);
        }
        if (state.getDestroySpeed(level, pos) != 0.0D) {
            stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        if (attacker instanceof Player player) {
            handleDevourKill(stack, target, player);
        }
        return true;
    }

    public static void applyAttackEffects(ItemStack stack, Player player, Entity entity) {
        handleSweepingAttack(stack, player, entity);
        handleDevourHit(stack, player, entity);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.guillotine(level.registryAccess()), heldItem) > 0) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(heldItem);
        }
        return InteractionResultHolder.pass(heldItem);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player) || level.isClientSide) {
            return;
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.guillotine(level.registryAccess()), stack) <= 0) {
            return;
        }

        float pull = (this.getUseDuration(stack, livingEntity) - timeLeft) / 20.0F;
        if (pull < 0.9F) {
            return;
        }

        player.swing(player.getUsedItemHand(), true);
        EntityHitResult entityHitResult = findLivingEntityHit(player, level, 5.0D);
        if (entityHitResult == null || !(entityHitResult.getEntity() instanceof LivingEntity target)) {
            return;
        }

        EquipmentSlot slot = player.getUsedItemHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
        float damage = getScytheAttackDamage(player) * 1.3F + getEnchantmentDamageBonus(level, stack, target);
        stack.hurtAndBreak(1, player, slot);
        boolean killed = target.hurt(player.damageSources().playerAttack(player), damage) && target.isDeadOrDying();

        if (killed) {
            level.playSound(null, target.getX(), target.getY() + 0.5D, target.getZ(), SoundEvents.BEHEAD, SoundSource.PLAYERS, 1.0F, level.random.nextFloat() * 0.2F + 0.9F);
            spawnBeheadParticles(level, target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D));
            spawnHead(target);
        } else {
            level.playSound(null, target.getX(), target.getY() + 0.5D, target.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, level.random.nextFloat() * 0.2F + 0.9F);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        MutableComponent text = Component.translatable("xercatools.scythe_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
        if (EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.guillotine(context.registries()), stack) > 0) {
            tooltip.add(Component.translatable("xercatools.guillotine_tooltip").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        if (this.tier.getRepairIngredient().test(repair)) {
            return true;
        }
        return super.isValidRepairItem(toRepair, repair);
    }

    public boolean harvestNeighbourCrops(Level level, BlockPos pos, Player player, ItemStack stack) {
        int sweeping = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SWEEPING_EDGE), stack);
        if (sweeping <= 0) {
            return false;
        }

        harvestIfReady(level, pos.south(), player);
        harvestIfReady(level, pos.north(), player);
        harvestIfReady(level, pos.east(), player);
        harvestIfReady(level, pos.west(), player);

        if (sweeping > 1) {
            harvestIfReady(level, pos.north().east(), player);
            harvestIfReady(level, pos.north().west(), player);
            harvestIfReady(level, pos.south().east(), player);
            harvestIfReady(level, pos.south().west(), player);
        }
        return false;
    }

    private static void handleSweepingAttack(ItemStack stack, Player player, Entity target) {
        int sweepingLevel = EnchantmentHelper.getItemEnchantmentLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SWEEPING_EDGE), stack);
        if (sweepingLevel <= 0 || !(target instanceof LivingEntity)) {
            return;
        }

        float cooldownStrength = player.getAttackStrengthScale(0.5F);
        boolean cooledAttack = cooldownStrength > 0.9F;
        double delta = player.walkDist - player.walkDistO;
        boolean critical = cooledAttack && player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable()
                && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger()
                && !player.isSprinting();
        boolean cooledSprintAttack = player.isSprinting() && cooledAttack;
        boolean sweep = cooledAttack && !critical && !cooledSprintAttack && player.onGround() && delta < player.getSpeed();
        if (!sweep) {
            return;
        }

        float damage = getScytheAttackDamage(player) * (0.2F + cooldownStrength * cooldownStrength * 0.8F)
                + getEnchantmentDamageBonus(player.level(), stack, (LivingEntity) target) * cooldownStrength;
        float sweepDamage = 1.0F + damage * (sweepingLevel == 1 ? 0.5F : 0.67F);

        for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
            if (nearby != player && nearby != target && !player.isAlliedTo(nearby)
                    && (!(nearby instanceof ArmorStand armorStand) || !armorStand.isMarker())
                    && player.distanceToSqr(nearby) < 9.0D) {
                nearby.knockback(0.4F, net.minecraft.util.Mth.sin(player.getYRot() * ((float) Math.PI / 180F)), -net.minecraft.util.Mth.cos(player.getYRot() * ((float) Math.PI / 180F)));
                nearby.hurt(player.damageSources().playerAttack(player), sweepDamage);
            }
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.sweepAttack();
    }

    private static void handleDevourHit(ItemStack stack, Player player, Entity entity) {
        if (!(entity instanceof LivingEntity target)) {
            return;
        }

        int devourLevel = EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.devour(player.level().registryAccess()), stack);
        if (devourLevel <= 0 || player.level().isClientSide || player.getAttackStrengthScale(0.5F) <= 0.9F) {
            return;
        }

        if (player.getRandom().nextFloat() < 0.25F * devourLevel) {
            healFromDevour((ServerLevel) player.level(), target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D), player, 1);
            player.level().playSound(null, player, SoundEvents.SNEAK_HIT, SoundSource.PLAYERS, 0.8F, 0.9F + player.getRandom().nextFloat() * 0.2F);
        }
    }

    private static void handleDevourKill(ItemStack stack, LivingEntity target, Player player) {
        int devourLevel = EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.devour(player.level().registryAccess()), stack);
        if (devourLevel > 0 && player.level() instanceof ServerLevel serverLevel && target.isDeadOrDying()) {
            healFromDevour(serverLevel, target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D), player, devourLevel * 2);
        }
    }

    private static void healFromDevour(ServerLevel level, Vec3 position, Player player, int amount) {
        if (amount <= 0) {
            return;
        }

        player.heal(amount);
        level.playSound(null, player, SoundEvents.ABSORB, SoundSource.PLAYERS, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.4F);
        for (int i = 0; i < amount * 3; i++) {
            double velX = (level.random.nextDouble() - 0.5D) * 0.2D;
            double velY = level.random.nextDouble() * 0.2D + 0.02D;
            double velZ = (level.random.nextDouble() - 0.5D) * 0.2D;
            level.sendParticles(ParticleTypes.HEART, position.x, position.y, position.z, 1, velX, velY, velZ, 0.0D);
        }
    }

    private static void spawnBeheadParticles(Level level, Vec3 position) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (int i = 0; i < 96; i++) {
            double velX = (level.random.nextDouble() - 0.5D) * 0.28D;
            double velY = (level.random.nextDouble() - 0.3D) * 0.28D;
            double velZ = (level.random.nextDouble() - 0.5D) * 0.28D;
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(net.minecraft.world.item.Items.NETHER_WART_BLOCK)),
                    position.x, position.y, position.z, 1, velX, velY, velZ, 0.0D);
        }
    }

    private static void spawnHead(LivingEntity target) {
        ItemStack head;
        if (target instanceof Player playerTarget) {
            head = new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD);
            head.set(DataComponents.PROFILE, new net.minecraft.world.item.component.ResolvableProfile(playerTarget.getGameProfile()));
        } else {
            if (target.getType() == EntityType.ZOMBIE) {
                head = new ItemStack(net.minecraft.world.item.Items.ZOMBIE_HEAD);
            } else if (target.getType() == EntityType.CREEPER) {
                head = new ItemStack(net.minecraft.world.item.Items.CREEPER_HEAD);
            } else if (target.getType() == EntityType.SKELETON) {
                head = new ItemStack(net.minecraft.world.item.Items.SKELETON_SKULL);
            } else if (target.getType() == EntityType.WITHER_SKELETON) {
                head = new ItemStack(net.minecraft.world.item.Items.WITHER_SKELETON_SKULL);
            } else if (target.getType() == EntityType.ENDER_DRAGON) {
                head = new ItemStack(net.minecraft.world.item.Items.DRAGON_HEAD);
            } else if (target.getType() == EntityType.PIGLIN) {
                head = new ItemStack(net.minecraft.world.item.Items.PIGLIN_HEAD);
            } else if (target.getType() == EntityType.COW) {
                head = createCustomMobHead("cow", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBlNGU2ZmJmNWYzZGNmOTQ0MjJhMWYzMTk0NDhmMTUyMzY5ZDE3OWRiZmJjZGYwMGU1YmZlODQ5NWZhOTc3In19fQ==");
            } else if (target.getType() == EntityType.IRON_GOLEM) {
                head = createCustomMobHead("iron_golem", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM2Y2Q3MjAyYzM0ZTc4ZjMwNzMwOTAzNDlmN2Q5NzNiMjg4YWY1ZTViNzMzNGRkMjQ5MDEwYjNmMjcwNzhmOSJ9fX0=");
            } else if (target.getType() == EntityType.OCELOT) {
                head = createCustomMobHead("ocelot", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE4YjZiNzk3ODMzNjhkZmUwMDQyOTg1MTEwZGEzNjZmOWM3ODhiNDUwOTdhM2VhNmQwZDlhNzUzZTlmNDJjNiJ9fX0=");
            } else if (target.getType() == EntityType.BLAZE) {
                head = createCustomMobHead("blaze", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA2ZTM0MmY5MGVjNTM4YWFhMTU1MmIyMjRmMjY0YTA0MDg0MDkwMmUxMjZkOTFlY2U2MTM5YWE1YjNjN2NjMyJ9fX0=");
            } else if (target.getType() == EntityType.PIG) {
                head = createCustomMobHead("pig", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTU2MmEzN2I4NzFmOTY0YmZjM2UxMzExZWE2NzJhYWEwMzk4NGE1ZGM0NzIxNTRhMzRkYzI1YWYxNTdlMzgyYiJ9fX0=");
            } else if (target.getType() == EntityType.SLIME) {
                head = createCustomMobHead("slime", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZjMjdiMDEzZjFiZjMzNDQ4NjllODFlNWM2MTAwMjdiYzQ1ZWM1Yjc5NTE0ZmRjOTZlMDFkZjFiN2UzYTM4NyJ9fX0=");
            } else if (target.getType() == EntityType.VILLAGER) {
                head = createCustomMobHead("villager", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRiZDgzMjgxM2FjMzhlNjg2NDg5MzhkN2EzMmY2YmEyOTgwMWFhZjMxNzQwNDM2N2YyMTRiNzhiNGQ0NzU0YyJ9fX0=");
            } else if (target.getType() == EntityType.CAVE_SPIDER) {
                head = createCustomMobHead("cave_spider", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdiMDcwNjNhNjg3NGZhM2UyMjU0OGUwMjA2MmJkNzMzYzI1ODg1OTI5ODA5NjI0MTgwYWViYjg1MTU1N2Y2YSJ9fX0=");
            } else if (target.getType() == EntityType.ENDERMAN) {
                head = createCustomMobHead("enderman", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIwOWEzNzUyNTEwZTkxNGIwYmRjOTA5NmIzOTJiYjM1OWY3YThlOGE5NTY2YTAyZTdmNjZmYWZmOGQ2Zjg5ZSJ9fX0=");
            } else if (target.getType() == EntityType.MAGMA_CUBE) {
                head = createCustomMobHead("magma_cube", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDkwZDYxZThjZTk1MTFhMGEyYjVlYTI3NDJjYjFlZjM2MTMxMzgwZWQ0MTI5ZTFiMTYzY2U4ZmYwMDBkZThlYSJ9fX0=");
            } else if (target.getType() == EntityType.ZOMBIFIED_PIGLIN) {
                head = createCustomMobHead("zombified_piglin", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE2ZDE2N2M1NzQ0ZWQxNGViYzAyZjQ0N2YzMjYxNDA1OTM2MmI3ZDJlY2I4MDhmZjA2MTY1ZDJjMzQzYmVmMiJ9fX0=");
            } else if (target.getType() == EntityType.SPIDER) {
                head = createCustomMobHead("spider", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYxYTQ5NTQxYTgzNmFhOGY0Zjc2ZTBkNGNiMmZmMDQ4ODhjNjJmOTQxMWVhMTBjYmFjZjFmMmE1NDQyNDI0MCJ9fX0=");
            } else if (target.getType() == EntityType.CHICKEN) {
                head = createCustomMobHead("chicken", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE2YjhlOTgzODljNTQxYmIzNjQ1Mzg1MGJjYmQxZjdiYzVhNTdkYTYyZGNjNTA1MDYwNDA5NzM3ZWM1YjcyYSJ9fX0=");
            } else if (target.getType() == EntityType.GHAST) {
                head = createCustomMobHead("ghast", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE0ZTQyZWIxNWEwODgxM2E2YTZmNjFmMTBhYTI4ODAxOWZhMGZhZTEwNmEyOTUzZGRiNDZmNzdlZTJkNzdmIn19fQ==");
            } else if (target.getType() == EntityType.MOOSHROOM) {
                head = createCustomMobHead("mooshroom", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTIzY2ZjNTU4MjQ1NGZjZjk5MDZmODQxZmRhMmNjNmFlODk2Y2Y0NTU4MjFjNGFkYTE5OThkZTcwODc3Y2M4NiJ9fX0=");
            } else if (target.getType() == EntityType.SHEEP) {
                head = createCustomMobHead("sheep", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NhMzhjY2Y0MTdlOTljYTlkNDdlZWIxNWE4YTMwZWRiMTUwN2FhNTJiNjc4YzIyMGM3MTdjNDc0YWE2ZmUzZSJ9fX0=");
            } else if (target.getType() == EntityType.SQUID) {
                head = createCustomMobHead("squid", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU4OTEwMWQ1Y2M3NGFhNDU4MDIxYTA2MGY2Mjg5YTUxYTM1YTdkMzRkOGNhZGRmYzNjZGYzYjJjOWEwNzFhIn19fQ==");
            } else {
                head = null;
            }

            if (head != null && head.is(net.minecraft.world.item.Items.PLAYER_HEAD)) {
                head.set(DataComponents.CUSTOM_NAME, Component.literal(target.getType().getDescription().getString() + " Head"));
            }
        }

        if (head != null) {
            Containers.dropItemStack(target.level(), target.getX(), target.getY(), target.getZ(), head);
        }
    }

    private static ItemStack createCustomMobHead(String key, String texture) {
        ItemStack head = new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD);
        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(("xercatools:" + key).getBytes(StandardCharsets.UTF_8)), key);
        profile.getProperties().put("textures", new Property("textures", texture));
        head.set(DataComponents.PROFILE, new net.minecraft.world.item.component.ResolvableProfile(profile));
        return head;
    }

    private static void harvestIfReady(Level level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        if (isHarvestablePlant(state)) {
            level.destroyBlock(pos, true, player);
        }
    }

    private static boolean isHarvestablePlant(BlockState state) {
        if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        }
        return state.is(Blocks.TALL_GRASS) || state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN);
    }

    private static float getScytheAttackDamage(Player player) {
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        return attackDamage == null ? 0.0F : (float) attackDamage.getValue();
    }

    private static float getEnchantmentDamageBonus(Level level, ItemStack stack, LivingEntity target) {
        int smiteLevel = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SMITE), stack);
        int baneLevel = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BANE_OF_ARTHROPODS), stack);
        int sharpnessLevel = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS), stack);
        float damage = sharpnessLevel > 0 ? 0.5F * sharpnessLevel + 0.5F : 0.0F;
        if (target.getType().is(EntityTypeTags.UNDEAD)) {
            damage += 2.5F * smiteLevel;
        }
        if (target.getType().is(EntityTypeTags.ARTHROPOD)) {
            damage += 2.5F * baneLevel;
        }
        return damage;
    }

    private static EntityHitResult findLivingEntityHit(Player player, Level level, double range) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getViewVector(1.0F).scale(range));
        level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
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
}
