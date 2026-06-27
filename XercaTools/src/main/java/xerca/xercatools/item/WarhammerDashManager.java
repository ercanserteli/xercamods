package xerca.xercatools.item;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Drives the multi-tick forward dash granted by the Dashing warhammer enchantment. When a charged strike is released,
 * the wielder dashes forward for a few ticks. Any target that comes within reach during the dash is struck once.
 */
public final class WarhammerDashManager {
    private static final double HIT_RANGE = 5.0D;
    private static final int DASH_DURATION_TICKS = 5;
    private static final double MAX_DISTANCE_LEVEL_1 = 2.5D;
    private static final double MAX_DISTANCE_LEVEL_2 = 5.0D;

    private static final Map<UUID, DashState> ACTIVE = new ConcurrentHashMap<>();

    private WarhammerDashManager() {
    }

    /**
     * Begins a dash for the given player. The dash distance and speed scale with the charge
     * fraction (a full charge reaches the level's maximum distance) and with the enchantment level.
     *
     * @param alreadyHit whether the release already struck a target at point-blank range
     */
    public static void startDash(Player player, ItemStack stack, EquipmentSlot slot, float pullDuration, int dashLevel, boolean alreadyHit) {
        Level level = player.level();
        if (level.isClientSide || dashLevel <= 0) {
            return;
        }

        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
        if (horizontal.lengthSqr() < 1.0e-6D) {
            return; // looking straight up or down: no meaningful forward direction
        }
        Vec3 direction = horizontal.normalize();

        double maxDistance = dashLevel >= 2 ? MAX_DISTANCE_LEVEL_2 : MAX_DISTANCE_LEVEL_1;
        double distance = maxDistance * pullDuration;
        double speed = distance / DASH_DURATION_TICKS;

        ACTIVE.put(player.getUUID(), new DashState(player, stack, slot, pullDuration, direction, speed, alreadyHit));

        level.playSound(null, player.getX(), player.getY(), player.getZ(), xerca.xercatools.SoundEvents.SWOOSH, player.getSoundSource(), 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
    }

    /**
     * Registered as a server tick callback; advances every active dash.
     */
    public static void onServerTick(MinecraftServer server) {
        if (ACTIVE.isEmpty()) {
            return;
        }
        ACTIVE.values().removeIf(dashState -> !advance(dashState));
    }

    private static boolean advance(DashState state) {
        Player player = state.player;
        if (player.isRemoved() || !player.isAlive() || player.level().isClientSide) {
            return false;
        }

        // Re-apply the forward velocity each tick so the client sustains the lunge, preserving vertical motion.
        player.setDeltaMovement(state.direction.x * state.speed, player.getDeltaMovement().y, state.direction.z * state.speed);
        player.hurtMarked = true;
        player.hasImpulse = true;
        player.fallDistance = 0.0F;

        if (!state.hasHit) {
            EntityHitResult hit = ItemWarhammer.findLivingEntityHit(player, player.level(), HIT_RANGE);
            if (hit != null && hit.getEntity() instanceof LivingEntity target) {
                ItemWarhammer.attackEntity(player, state.stack, target, state.pullDuration, player.level(), state.slot);
                state.hasHit = true;
            }
        }

        --state.ticksRemaining;
        return state.ticksRemaining > 0;
    }

    private static final class DashState {
        private final Player player;
        private final ItemStack stack;
        private final EquipmentSlot slot;
        private final float pullDuration;
        private final Vec3 direction;
        private final double speed;
        private int ticksRemaining;
        private boolean hasHit;

        private DashState(Player player, ItemStack stack, EquipmentSlot slot, float pullDuration, Vec3 direction, double speed, boolean hasHit) {
            this.player = player;
            this.stack = stack;
            this.slot = slot;
            this.pullDuration = pullDuration;
            this.direction = direction;
            this.speed = speed;
            this.ticksRemaining = DASH_DURATION_TICKS;
            this.hasHit = hasHit;
        }
    }
}
