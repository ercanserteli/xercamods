package xerca.xercatools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercatools.enchantment.FlaskEnchantments;
import xerca.xercatools.enchantment.GrabHookEnchantments;
import xerca.xercatools.enchantment.KnifeEnchantments;
import xerca.xercatools.entity.EntityConfettiBall;
import xerca.xercatools.entity.EntityGrabHook;
import xerca.xercatools.entity.EntityHealthOrb;
import xerca.xercatools.item.ItemConfetti;
import xerca.xercatools.item.ItemKnife;
import xerca.xercatools.item.ItemScythe;
import xerca.xercatools.item.Items;
import xerca.xercatools.packet.ConfettiParticlePacket;

import java.util.Collection;

public class Mod implements ModInitializer {
    private enum EnchantTargetType {
        WARHAMMER,
        SCYTHE,
        GRAB_HOOK,
        KNIFE,
        FLASK,
        POTION_LAUNCHER,
        OTHER
    }

    public static final String MOD_ID = "xercatools";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final EntityType<EntityGrabHook> HOOK = EntityType.Builder.<EntityGrabHook>of(EntityGrabHook::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(8)
            .updateInterval(2)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, id("hook")));
    public static final EntityType<EntityHealthOrb> HEALTH_ORB = EntityType.Builder.<EntityHealthOrb>of(EntityHealthOrb::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, id("health_orb")));
    public static final EntityType<EntityConfettiBall> ENTITY_CONFETTI_BALL = EntityType.Builder.<EntityConfettiBall>of(EntityConfettiBall::new, MobCategory.MISC)
            .sized(0.25f, 0.25f).updateInterval(10).build(ResourceKey.create(Registries.ENTITY_TYPE, id("confetti_ball")));
    public static final SimpleParticleType CONFETTI_PARTICLE = FabricParticleTypes.simple();

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceKey<Item> itemKey(String path) {
        return ResourceKey.create(Registries.ITEM, id(path));
    }

    @Override
    public void onInitialize() {
        SoundEvents.register();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("hook"), HOOK);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("health_orb"), HEALTH_ORB);
        Items.register();

        PayloadTypeRegistry.playS2C().register(ConfettiParticlePacket.PACKET_ID, ConfettiParticlePacket.PACKET_CODEC);

        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("confetti_ball"), ENTITY_CONFETTI_BALL);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("confetti_particle"), CONFETTI_PARTICLE);

        DispenserBlock.registerBehavior(Items.CONFETTI_BALL, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stackIn) {
                Position position = DispenserBlock.getDispensePosition(source);
                EntityConfettiBall projectile = new EntityConfettiBall(source.level(), position.x(), position.y(), position.z());
                projectile.setItem(stackIn.copyWithCount(1));
                projectile.shoot(source.state().getValue(DispenserBlock.FACING).getStepX(), source.state().getValue(DispenserBlock.FACING).getStepY() + 0.1F, source.state().getValue(DispenserBlock.FACING).getStepZ(), 1.1F, 6.0F);
                source.level().addFreshEntity(projectile);
                stackIn.shrink(1);
                return stackIn;
            }
        });
        DispenserBlock.registerBehavior(Items.CONFETTI, new Mod.ConfettiDispenseItemBehavior());

        registerEnchantmentRules();
        registerCombatHooks();
        ServerTickEvents.END_SERVER_TICK.register(xerca.xercatools.item.WarhammerDashManager::onServerTick);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(Items.WOODEN_SCYTHE);
            entries.accept(Items.STONE_SCYTHE);
            entries.accept(Items.COPPER_SCYTHE);
            entries.accept(Items.IRON_SCYTHE);
            entries.accept(Items.GOLDEN_SCYTHE);
            entries.accept(Items.DIAMOND_SCYTHE);
            entries.accept(Items.NETHERITE_SCYTHE);
            entries.accept(Items.STONE_KNIFE);
            entries.accept(Items.COPPER_KNIFE);
            entries.accept(Items.IRON_KNIFE);
            entries.accept(Items.GOLDEN_KNIFE);
            entries.accept(Items.DIAMOND_KNIFE);
            entries.accept(Items.NETHERITE_KNIFE);
            entries.accept(Items.GRAB_HOOK);
            entries.accept(Items.FLASK);
            entries.accept(Items.ENDER_BOW);
            entries.accept(Items.CONFETTI);
            entries.accept(Items.CONFETTI_BALL);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(Items.WOODEN_SCYTHE);
            entries.accept(Items.STONE_SCYTHE);
            entries.accept(Items.COPPER_SCYTHE);
            entries.accept(Items.IRON_SCYTHE);
            entries.accept(Items.GOLDEN_SCYTHE);
            entries.accept(Items.DIAMOND_SCYTHE);
            entries.accept(Items.NETHERITE_SCYTHE);
            entries.accept(Items.STONE_WARHAMMER);
            entries.accept(Items.COPPER_WARHAMMER);
            entries.accept(Items.IRON_WARHAMMER);
            entries.accept(Items.GOLD_WARHAMMER);
            entries.accept(Items.DIAMOND_WARHAMMER);
            entries.accept(Items.NETHERITE_WARHAMMER);
            entries.accept(Items.STONE_KNIFE);
            entries.accept(Items.COPPER_KNIFE);
            entries.accept(Items.IRON_KNIFE);
            entries.accept(Items.GOLDEN_KNIFE);
            entries.accept(Items.DIAMOND_KNIFE);
            entries.accept(Items.NETHERITE_KNIFE);
            entries.accept(Items.GRAB_HOOK);
        });
        LOGGER.info(MOD_ID + " initialized");
    }

    private void registerEnchantmentRules() {
        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> resolveEnchantmentRule(enchantment, target));
    }

    private TriState resolveEnchantmentRule(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment, ItemStack target) {
        return switch (detectEnchantTargetType(target)) {
            case WARHAMMER -> isWarhammerEnchantment(enchantment) ? TriState.TRUE : TriState.DEFAULT;
            case SCYTHE -> isScytheEnchantment(enchantment) ? TriState.TRUE : TriState.DEFAULT;
            case GRAB_HOOK -> isGrabHookEnchantment(enchantment) ? TriState.TRUE : TriState.DEFAULT;
            case KNIFE -> isKnifeEnchantment(enchantment) ? TriState.TRUE : TriState.DEFAULT;
            case FLASK -> isFlaskEnchantment(enchantment) ? TriState.TRUE : TriState.DEFAULT;
            case POTION_LAUNCHER -> isPotionLauncherEnchantment(enchantment) ? TriState.TRUE : TriState.DEFAULT;
            case OTHER -> TriState.DEFAULT;
        };
    }

    private EnchantTargetType detectEnchantTargetType(ItemStack target) {
        if (isWarhammer(target)) {
            return EnchantTargetType.WARHAMMER;
        }
        if (isScythe(target)) {
            return EnchantTargetType.SCYTHE;
        }
        if (isGrabHook(target)) {
            return EnchantTargetType.GRAB_HOOK;
        }
        if (isKnife(target)) {
            return EnchantTargetType.KNIFE;
        }
        if (isFlask(target)) {
            return EnchantTargetType.FLASK;
        }
        if (isPotionLauncher(target)) {
            return EnchantTargetType.POTION_LAUNCHER;
        }
        return EnchantTargetType.OTHER;
    }

    private boolean isWarhammerEnchantment(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(Enchantments.SMITE)
                || enchantment.is(Enchantments.BANE_OF_ARTHROPODS)
                || enchantment.is(Enchantments.LOOTING);
    }

    private boolean isScytheEnchantment(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(Enchantments.FORTUNE)
                || enchantment.is(Enchantments.SHARPNESS)
                || enchantment.is(Enchantments.SMITE)
                || enchantment.is(Enchantments.BANE_OF_ARTHROPODS)
                || enchantment.is(Enchantments.SWEEPING_EDGE)
                || enchantment.is(Enchantments.LOOTING);
    }

    private boolean isGrabHookEnchantment(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(GrabHookEnchantments.GRAPPLING)
                || enchantment.is(GrabHookEnchantments.TURBO_GRAB)
                || enchantment.is(GrabHookEnchantments.GENTLE_GRAB);
    }

    private boolean isKnifeEnchantment(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(Enchantments.SHARPNESS)
                || enchantment.is(Enchantments.LOOTING)
                || enchantment.is(KnifeEnchantments.POISON)
                || enchantment.is(KnifeEnchantments.STEALTH);
    }

    private boolean isFlaskEnchantment(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(FlaskEnchantments.CAPACITY)
                || enchantment.is(FlaskEnchantments.CHUG);
    }

    private boolean isPotionLauncherEnchantment(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(FlaskEnchantments.CAPACITY)
                || enchantment.is(FlaskEnchantments.RANGE);
    }

    private void registerCombatHooks() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (isScythe(stack)) {
                ItemScythe.applyAttackEffects(stack, player, entity);
            }
            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand != net.minecraft.world.InteractionHand.OFF_HAND || !(entity instanceof LivingEntity target)) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getOffhandItem();
            if (!isKnife(stack) || player.getCooldowns().isOnCooldown(stack)) {
                return InteractionResult.PASS;
            }

            if (level instanceof ServerLevel serverLevel) {
                float damage = ItemKnife.getOffhandDamage(level, stack, target, player);
                float healthBefore = target.getHealth();
                target.hurtServer(serverLevel, player.damageSources().playerAttack(player), damage);
                float dealt = healthBefore - target.getHealth();
                if (dealt > 2.0F) {
                    serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.DAMAGE_INDICATOR,
                            target.getX(), target.getY(0.5), target.getZ(), (int) (dealt * 0.5), 0.1, 0.0, 0.1, 0.2);
                }
                stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.OFFHAND);
                int poisonLevel = EnchantmentHelper.getItemEnchantmentLevel(KnifeEnchantments.poisonEnchantment(level.registryAccess()), stack);
                if (poisonLevel > 0) {
                    target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 30 + 30 * poisonLevel, poisonLevel - 1));
                    player.magicCrit(target);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 1.0F);
                } else {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }

            player.swing(hand, true);
            player.getCooldowns().addCooldown(stack, 15);
            return InteractionResult.SUCCESS;
        });
    }

    private static boolean isWarhammer(ItemStack stack) {
        return stack.is(Items.STONE_WARHAMMER)
                || stack.is(Items.COPPER_WARHAMMER)
                || stack.is(Items.IRON_WARHAMMER)
                || stack.is(Items.GOLD_WARHAMMER)
                || stack.is(Items.DIAMOND_WARHAMMER)
                || stack.is(Items.NETHERITE_WARHAMMER);
    }

    private static boolean isScythe(ItemStack stack) {
        return stack.is(Items.WOODEN_SCYTHE)
                || stack.is(Items.STONE_SCYTHE)
                || stack.is(Items.COPPER_SCYTHE)
                || stack.is(Items.IRON_SCYTHE)
                || stack.is(Items.GOLDEN_SCYTHE)
                || stack.is(Items.DIAMOND_SCYTHE)
                || stack.is(Items.NETHERITE_SCYTHE);
    }

    private static boolean isGrabHook(ItemStack stack) {
        return stack.is(Items.GRAB_HOOK);
    }

    private static boolean isKnife(ItemStack stack) {
        return stack.is(Items.STONE_KNIFE)
                || stack.is(Items.COPPER_KNIFE)
                || stack.is(Items.IRON_KNIFE)
                || stack.is(Items.GOLDEN_KNIFE)
                || stack.is(Items.DIAMOND_KNIFE)
                || stack.is(Items.NETHERITE_KNIFE);
    }

    private static boolean isFlask(ItemStack stack) {
        return stack.is(Items.FLASK);
    }

    private static boolean isPotionLauncher(ItemStack stack) {
        return stack.is(Items.ENDER_BOW);
    }

    public static void sendToClient(ServerPlayer player, ConfettiParticlePacket packet) {
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendToClientsAround(ServerLevel level, Vec3 pos, double radius, ConfettiParticlePacket packet) {
        Collection<ServerPlayer> players = PlayerLookup.around(level, pos, radius);
        for (ServerPlayer player : players) {
            sendToClient(player, packet);
        }
    }

    public static class ConfettiDispenseItemBehavior extends DefaultDispenseItemBehavior {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            stack.shrink(1);
            return stack;
        }

        /**
         * Play the dispense sound from the specified block.
         */
        @Override
        protected void playSound(BlockSource source) {
            var pos = DispenserBlock.getDispensePosition(source);
            ItemConfetti.playSound(source.level(), null, pos.x(), pos.y(), pos.z());
        }

        /**
         * Order clients to display dispense particles from the specified block and facing.
         */
        @Override
        protected void playAnimation(BlockSource source, Direction facingIn) {
            var pos = DispenserBlock.getDispensePosition(source);
            double x = pos.x() + facingIn.getStepX();
            double y = pos.y() + facingIn.getStepY();
            double z = pos.z() + facingIn.getStepZ();
            ConfettiParticlePacket pack = new ConfettiParticlePacket(x, y, z, facingIn.getUnitVec3i());
            sendToClientsAround(source.level(), new Vec3(x, y, z), 64, pack);
        }
    }
}
