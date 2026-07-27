package xerca.xercatools;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
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
import xerca.xercatools.packet.ConfettiParticlePacketHandler;

@net.neoforged.fml.common.Mod(Mod.MOD_ID)
public class Mod {
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
    // Entity types are built during registration: EntityType.Builder.build writes to a frozen registry.
    public static EntityType<EntityGrabHook> HOOK;
    public static EntityType<EntityHealthOrb> HEALTH_ORB;
    public static EntityType<EntityConfettiBall> ENTITY_CONFETTI_BALL;
    public static final SimpleParticleType CONFETTI_PARTICLE = new SimpleParticleType(false);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::onRegisterPayloads);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(this::onAttackEntity);
        NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
        LOGGER.info("{} initialized", MOD_ID);
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, SoundEvents::register);
        event.register(Registries.ITEM, Items::registerItems);
        event.register(Registries.RECIPE_SERIALIZER, Items::registerRecipeSerializers);
        event.register(Registries.PARTICLE_TYPE, helper -> helper.register(id("confetti_particle"), CONFETTI_PARTICLE));
        event.register(Registries.ENTITY_TYPE, helper -> {
            HOOK = EntityType.Builder.<EntityGrabHook>of(EntityGrabHook::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(8).updateInterval(2).build("hook");
            HEALTH_ORB = EntityType.Builder.<EntityHealthOrb>of(EntityHealthOrb::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).build("health_orb");
            ENTITY_CONFETTI_BALL = EntityType.Builder.<EntityConfettiBall>of(EntityConfettiBall::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).updateInterval(10).build("confetti_ball");
            helper.register(id("hook"), HOOK);
            helper.register(id("health_orb"), HEALTH_ORB);
            helper.register(id("confetti_ball"), ENTITY_CONFETTI_BALL);
        });
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(ConfettiParticlePacket.PACKET_ID, ConfettiParticlePacket.PACKET_CODEC, ConfettiParticlePacketHandler::handle);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
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
        });
    }

    private void onServerTick(ServerTickEvent.Post event) {
        xerca.xercatools.item.WarhammerDashManager.onServerTick(event.getServer());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(Items.WOODEN_SCYTHE);
            event.accept(Items.STONE_SCYTHE);
            event.accept(Items.IRON_SCYTHE);
            event.accept(Items.GOLDEN_SCYTHE);
            event.accept(Items.DIAMOND_SCYTHE);
            event.accept(Items.NETHERITE_SCYTHE);
            event.accept(Items.STONE_KNIFE);
            event.accept(Items.IRON_KNIFE);
            event.accept(Items.GOLDEN_KNIFE);
            event.accept(Items.DIAMOND_KNIFE);
            event.accept(Items.NETHERITE_KNIFE);
            event.accept(Items.GRAB_HOOK);
            event.accept(Items.FLASK);
            event.accept(Items.ENDER_BOW);
            event.accept(Items.CONFETTI);
            event.accept(Items.CONFETTI_BALL);
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(Items.WOODEN_SCYTHE);
            event.accept(Items.STONE_SCYTHE);
            event.accept(Items.IRON_SCYTHE);
            event.accept(Items.GOLDEN_SCYTHE);
            event.accept(Items.DIAMOND_SCYTHE);
            event.accept(Items.NETHERITE_SCYTHE);
            event.accept(Items.STONE_WARHAMMER);
            event.accept(Items.IRON_WARHAMMER);
            event.accept(Items.GOLD_WARHAMMER);
            event.accept(Items.DIAMOND_WARHAMMER);
            event.accept(Items.NETHERITE_WARHAMMER);
            event.accept(Items.STONE_KNIFE);
            event.accept(Items.IRON_KNIFE);
            event.accept(Items.GOLDEN_KNIFE);
            event.accept(Items.DIAMOND_KNIFE);
            event.accept(Items.NETHERITE_KNIFE);
            event.accept(Items.GRAB_HOOK);
        }
    }

    public static boolean toolSupportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return switch (detectEnchantTargetType(stack)) {
            case WARHAMMER -> isWarhammerEnchantment(enchantment);
            case SCYTHE -> isScytheEnchantment(enchantment);
            case GRAB_HOOK -> isGrabHookEnchantment(enchantment);
            case KNIFE -> isKnifeEnchantment(enchantment);
            case FLASK -> isFlaskEnchantment(enchantment);
            case POTION_LAUNCHER -> isPotionLauncherEnchantment(enchantment);
            case OTHER -> false;
        };
    }

    private static EnchantTargetType detectEnchantTargetType(ItemStack target) {
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

    private static boolean isWarhammerEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(Enchantments.SMITE)
                || enchantment.is(Enchantments.BANE_OF_ARTHROPODS)
                || enchantment.is(Enchantments.LOOTING);
    }

    private static boolean isScytheEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(Enchantments.FORTUNE)
                || enchantment.is(Enchantments.SHARPNESS)
                || enchantment.is(Enchantments.SMITE)
                || enchantment.is(Enchantments.BANE_OF_ARTHROPODS)
                || enchantment.is(Enchantments.SWEEPING_EDGE)
                || enchantment.is(Enchantments.LOOTING);
    }

    private static boolean isGrabHookEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(GrabHookEnchantments.GRAPPLING)
                || enchantment.is(GrabHookEnchantments.TURBO_GRAB)
                || enchantment.is(GrabHookEnchantments.GENTLE_GRAB);
    }

    private static boolean isKnifeEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(Enchantments.SHARPNESS)
                || enchantment.is(Enchantments.LOOTING)
                || enchantment.is(KnifeEnchantments.POISON)
                || enchantment.is(KnifeEnchantments.STEALTH);
    }

    private static boolean isFlaskEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(FlaskEnchantments.CAPACITY)
                || enchantment.is(FlaskEnchantments.CHUG);
    }

    private static boolean isPotionLauncherEnchantment(Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.UNBREAKING)
                || enchantment.is(Enchantments.MENDING)
                || enchantment.is(FlaskEnchantments.CAPACITY)
                || enchantment.is(FlaskEnchantments.RANGE);
    }

    private void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (isScythe(stack)) {
            ItemScythe.applyAttackEffects(stack, player, event.getTarget());
        }
    }

    private void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = handleKnifeOffhand(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
        if (result == InteractionResult.SUCCESS) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    public static InteractionResult handleKnifeOffhand(Player player, Level level, InteractionHand hand, Entity entity) {
        if (hand != InteractionHand.OFF_HAND || !(entity instanceof LivingEntity target)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getOffhandItem();
        if (!isKnife(stack) || player.getCooldowns().isOnCooldown(stack.getItem())) {
            return InteractionResult.PASS;
        }

        if (level instanceof ServerLevel serverLevel) {
            float damage = ItemKnife.getOffhandDamage(level, stack, target, player);
            float healthBefore = target.getHealth();
            target.hurt(player.damageSources().playerAttack(player), damage);
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

        player.swing(InteractionHand.OFF_HAND, true);
        player.getCooldowns().addCooldown(stack.getItem(), 15);
        return InteractionResult.SUCCESS;
    }

    private static boolean isWarhammer(ItemStack stack) {
        return stack.is(Items.STONE_WARHAMMER)
                || stack.is(Items.IRON_WARHAMMER)
                || stack.is(Items.GOLD_WARHAMMER)
                || stack.is(Items.DIAMOND_WARHAMMER)
                || stack.is(Items.NETHERITE_WARHAMMER);
    }

    private static boolean isScythe(ItemStack stack) {
        return stack.is(Items.WOODEN_SCYTHE)
                || stack.is(Items.STONE_SCYTHE)
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
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToClientsAround(ServerLevel level, Vec3 pos, double radius, ConfettiParticlePacket packet) {
        PacketDistributor.sendToPlayersNear(level, null, pos.x, pos.y, pos.z, radius, packet);
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
            ConfettiParticlePacket pack = new ConfettiParticlePacket(x, y, z, facingIn.getNormal());
            sendToClientsAround(source.level(), new Vec3(x, y, z), 64, pack);
        }
    }
}
