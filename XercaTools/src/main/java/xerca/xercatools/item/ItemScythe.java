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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
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
import org.jetbrains.annotations.Nullable;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.enchantment.ScytheEnchantments;
import xerca.xercatools.entity.EntityHealthOrb;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemScythe extends Item {
    private final ToolMaterial tier;
    public static final float FULL_USE_SECONDS = 1.0F;
    private static final Map<EntityType<?>, Item> VANILLA_HEADS = Map.of(
            EntityType.ZOMBIE, net.minecraft.world.item.Items.ZOMBIE_HEAD,
            EntityType.CREEPER, net.minecraft.world.item.Items.CREEPER_HEAD,
            EntityType.SKELETON, net.minecraft.world.item.Items.SKELETON_SKULL,
            EntityType.WITHER_SKELETON, net.minecraft.world.item.Items.WITHER_SKELETON_SKULL,
            EntityType.ENDER_DRAGON, net.minecraft.world.item.Items.DRAGON_HEAD,
            EntityType.PIGLIN, net.minecraft.world.item.Items.PIGLIN_HEAD
    );
    private static final Map<EntityType<?>, String[]> CUSTOM_HEADS = Map.ofEntries(
            Map.entry(EntityType.COW, new String[]{"cow", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBlNGU2ZmJmNWYzZGNmOTQ0MjJhMWYzMTk0NDhmMTUyMzY5ZDE3OWRiZmJjZGYwMGU1YmZlODQ5NWZhOTc3In19fQ=="}),
            Map.entry(EntityType.IRON_GOLEM, new String[]{"iron_golem", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM2Y2Q3MjAyYzM0ZTc4ZjMwNzMwOTAzNDlmN2Q5NzNiMjg4YWY1ZTViNzMzNGRkMjQ5MDEwYjNmMjcwNzhmOSJ9fX0="}),
            Map.entry(EntityType.OCELOT, new String[]{"ocelot", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE4YjZiNzk3ODMzNjhkZmUwMDQyOTg1MTEwZGEzNjZmOWM3ODhiNDUwOTdhM2VhNmQwZDlhNzUzZTlmNDJjNiJ9fX0="}),
            Map.entry(EntityType.BLAZE, new String[]{"blaze", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA2ZTM0MmY5MGVjNTM4YWFhMTU1MmIyMjRmMjY0YTA0MDg0MDkwMmUxMjZkOTFlY2U2MTM5YWE1YjNjN2NjMyJ9fX0="}),
            Map.entry(EntityType.PIG, new String[]{"pig", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTU2MmEzN2I4NzFmOTY0YmZjM2UxMzExZWE2NzJhYWEwMzk4NGE1ZGM0NzIxNTRhMzRkYzI1YWYxNTdlMzgyYiJ9fX0="}),
            Map.entry(EntityType.SLIME, new String[]{"slime", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZjMjdiMDEzZjFiZjMzNDQ4NjllODFlNWM2MTAwMjdiYzQ1ZWM1Yjc5NTE0ZmRjOTZlMDFkZjFiN2UzYTM4NyJ9fX0="}),
            Map.entry(EntityType.VILLAGER, new String[]{"villager", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRiZDgzMjgxM2FjMzhlNjg2NDg5MzhkN2EzMmY2YmEyOTgwMWFhZjMxNzQwNDM2N2YyMTRiNzhiNGQ0NzU0YyJ9fX0="}),
            Map.entry(EntityType.CAVE_SPIDER, new String[]{"cave_spider", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdiMDcwNjNhNjg3NGZhM2UyMjU0OGUwMjA2MmJkNzMzYzI1ODg1OTI5ODA5NjI0MTgwYWViYjg1MTU1N2Y2YSJ9fX0="}),
            Map.entry(EntityType.ENDERMAN, new String[]{"enderman", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIwOWEzNzUyNTEwZTkxNGIwYmRjOTA5NmIzOTJiYjM1OWY3YThlOGE5NTY2YTAyZTdmNjZmYWZmOGQ2Zjg5ZSJ9fX0="}),
            Map.entry(EntityType.MAGMA_CUBE, new String[]{"magma_cube", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDkwZDYxZThjZTk1MTFhMGEyYjVlYTI3NDJjYjFlZjM2MTMxMzgwZWQ0MTI5ZTFiMTYzY2U4ZmYwMDBkZThlYSJ9fX0="}),
            Map.entry(EntityType.ZOMBIFIED_PIGLIN, new String[]{"zombified_piglin", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE2ZDE2N2M1NzQ0ZWQxNGViYzAyZjQ0N2YzMjYxNDA1OTM2MmI3ZDJlY2I4MDhmZjA2MTY1ZDJjMzQzYmVmMiJ9fX0="}),
            Map.entry(EntityType.SPIDER, new String[]{"spider", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYxYTQ5NTQxYTgzNmFhOGY0Zjc2ZTBkNGNiMmZmMDQ4ODhjNjJmOTQxMWVhMTBjYmFjZjFmMmE1NDQyNDI0MCJ9fX0="}),
            Map.entry(EntityType.CHICKEN, new String[]{"chicken", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE2YjhlOTgzODljNTQxYmIzNjQ1Mzg1MGJjYmQxZjdiYzVhNTdkYTYyZGNjNTA1MDYwNDA5NzM3ZWM1YjcyYSJ9fX0="}),
            Map.entry(EntityType.GHAST, new String[]{"ghast", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE0ZTQyZWIxNWEwODgxM2E2YTZmNjFmMTBhYTI4ODAxOWZhMGZhZTEwNmEyOTUzZGRiNDZmNzdlZTJkNzdmIn19fQ=="}),
            Map.entry(EntityType.MOOSHROOM, new String[]{"mooshroom", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTIzY2ZjNTU4MjQ1NGZjZjk5MDZmODQxZmRhMmNjNmFlODk2Y2Y0NTU4MjFjNGFkYTE5OThkZTcwODc3Y2M4NiJ9fX0="}),
            Map.entry(EntityType.SHEEP, new String[]{"sheep", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NhMzhjY2Y0MTdlOTljYTlkNDdlZWIxNWE4YTMwZWRiMTUwN2FhNTJiNjc4YzIyMGM3MTdjNDc0YWE2ZmUzZSJ9fX0="}),
            Map.entry(EntityType.SQUID, new String[]{"squid", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU4OTEwMWQ1Y2M3NGFhNDU4MDIxYTA2MGY2Mjg5YTUxYTM1YTdkMzRkOGNhZGRmYzNjZGYzYjJjOWEwNzFhIn19fQ=="})
    );

    public static ItemAttributeModifiers createAttributes(ToolMaterial tier) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 3.0F + tier.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.6F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public ItemScythe(ToolMaterial tier, String name) {
        super(tier == ToolMaterial.NETHERITE
                ? new Item.Properties().setId(xerca.xercatools.Mod.itemKey(name)).durability(tier.durability()).fireResistant().enchantable(tier.enchantmentValue()).repairable(tier.repairItems()).attributes(createAttributes(tier))
                : new Item.Properties().setId(xerca.xercatools.Mod.itemKey(name)).durability(tier.durability()).enchantable(tier.enchantmentValue()).repairable(tier.repairItems()).attributes(createAttributes(tier)));
        this.tier = tier;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state) ? 1.0F : 0.0F;
        }
        return isHarvestablePlant(state) ? 1.0F : super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof Player player && isHarvestablePlant(state)) {
            harvestNeighbourCrops(level, pos, player, stack);
        }
        if (state.getDestroySpeed(level, pos) != 0.0D) {
            stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        if (attacker instanceof Player player) {
            handleDevourKill(stack, target, player);
        }
    }

    public static void applyAttackEffects(ItemStack stack, Player player, Entity entity) {
        handleSweepingAttack(stack, player, entity);
        handleDevourHit(stack, player, entity);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.guillotineEnchantment(level.registryAccess()), heldItem) > 0) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
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
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player) || level.isClientSide) {
            return false;
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.guillotineEnchantment(level.registryAccess()), stack) <= 0) {
            return false;
        }

        float pull = (this.getUseDuration(stack, livingEntity) - timeLeft) / 20.0F;
        if (pull < 0.9F) {
            return false;
        }

        player.swing(player.getUsedItemHand(), true);
        EntityHitResult entityHitResult = findLivingEntityHit(player, level, 5.0D);
        if (entityHitResult == null || !(entityHitResult.getEntity() instanceof LivingEntity target)) {
            return false;
        }

        EquipmentSlot slot = player.getUsedItemHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
        float damage = getScytheAttackDamage(player) * 1.3F + getEnchantmentDamageBonus(level, stack, target);
        stack.hurtAndBreak(1, player, slot);
        boolean killed = target.hurtOrSimulate(player.damageSources().playerAttack(player), damage) && target.isDeadOrDying();

        if (killed) {
            level.playSound(null, target.getX(), target.getY() + 0.5D, target.getZ(), SoundEvents.BEHEAD, SoundSource.PLAYERS, 1.0F, level.random.nextFloat() * 0.2F + 0.9F);
            spawnBeheadParticles(level, target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D));
            spawnHead(target);
        } else {
            level.playSound(null, target.getX(), target.getY() + 0.5D, target.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, level.random.nextFloat() * 0.2F + 0.9F);
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        MutableComponent text = Component.translatable("xercatools.scythe_tooltip");
        tooltip.accept(text.withStyle(ChatFormatting.BLUE));
        var registries = context.registries();
        if (registries != null
                && EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.guillotineEnchantment(registries), stack) > 0) {
            tooltip.accept(Component.translatable("xercatools.guillotine_tooltip").withStyle(ChatFormatting.YELLOW));
        }
    }

    public void harvestNeighbourCrops(Level level, BlockPos pos, Player player, ItemStack stack) {
        int sweeping = EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SWEEPING_EDGE), stack);
        if (sweeping <= 0) {
            return;
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
    }

    private static void handleSweepingAttack(ItemStack stack, Player player, Entity target) {
        int sweepingLevel = EnchantmentHelper.getItemEnchantmentLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SWEEPING_EDGE), stack);
        if (sweepingLevel <= 0 || !(target instanceof LivingEntity)) {
            return;
        }

        float cooldownStrength = player.getAttackStrengthScale(0.5F);
        boolean cooledAttack = cooldownStrength > 0.9F;
        double delta = player.getKnownMovement().horizontalDistance();
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

        int devourLevel = EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.devourEnchantment(player.level().registryAccess()), stack);
        if (devourLevel <= 0 || player.level().isClientSide || player.getAttackStrengthScale(0.5F) <= 0.9F) {
            return;
        }

        if (player.getRandom().nextFloat() < 0.25F * devourLevel) {
            EntityHealthOrb.award((ServerLevel) player.level(), target, player, 1);
            player.level().playSound(null, player, SoundEvents.SNEAK_HIT, SoundSource.PLAYERS, 0.8F, 0.9F + player.getRandom().nextFloat() * 0.2F);
        }
    }

    private static void handleDevourKill(ItemStack stack, LivingEntity target, Player player) {
        int devourLevel = EnchantmentHelper.getItemEnchantmentLevel(ScytheEnchantments.devourEnchantment(player.level().registryAccess()), stack);
        if (devourLevel > 0 && player.level() instanceof ServerLevel serverLevel && target.isDeadOrDying()) {
            int devourCount = player.level().random.nextInt(devourLevel, devourLevel * 2 + 1);
            EntityHealthOrb.award(serverLevel, target, player, devourCount);
        }
    }

    private static void spawnBeheadParticles(Level level, Vec3 position) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (int i = 0; i < 96; i++) {
            double velX = (level.random.nextDouble() - 0.5D) * 0.28D;
            double velY = level.random.nextDouble() * 0.5D + 0.15D;
            double velZ = (level.random.nextDouble() - 0.5D) * 0.28D;
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(net.minecraft.world.item.Items.NETHER_WART_BLOCK)),
                    position.x, position.y, position.z, 0, velX, velY, velZ, 1.0D);
        }
    }

    private static void spawnHead(LivingEntity target) {
        ItemStack head;
        if (target instanceof Player playerTarget) {
            head = new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD);
            head.set(DataComponents.PROFILE, new net.minecraft.world.item.component.ResolvableProfile(playerTarget.getGameProfile()));
        } else {
            head = getMobHead(target.getType());

            if (head != null && head.is(net.minecraft.world.item.Items.PLAYER_HEAD)) {
                head.set(DataComponents.CUSTOM_NAME, Component.literal(target.getType().getDescription().getString() + " Head"));
            }
        }

        if (head != null) {
            Containers.dropItemStack(target.level(), target.getX(), target.getY(), target.getZ(), head);
        }
    }

    private static @Nullable ItemStack getMobHead(EntityType<?> type) {
        Item vanillaHead = VANILLA_HEADS.get(type);
        if (vanillaHead != null) {
            return new ItemStack(vanillaHead);
        }

        String[] customHead = CUSTOM_HEADS.get(type);
        if (customHead != null) {
            return createCustomMobHead(customHead[0], customHead[1]);
        }

        return null;
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

    private static @Nullable EntityHitResult findLivingEntityHit(Player player, Level level, double range) {
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

        return closestEntity != null ? new EntityHitResult(closestEntity, closestHitPos) : null;
    }
}
