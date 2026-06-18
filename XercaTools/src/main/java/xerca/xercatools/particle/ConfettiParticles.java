package xerca.xercatools.particle;

import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xerca.xercatools.Mod;

public final class ConfettiParticles {
    private static final int HAND_BURST_COUNT = 64;
    private static final int BALL_BURST_COUNT = 32;
    private static final int BALL_TRAIL_COUNT = 16;
    private static final double DIRECTIONAL_PUSH = 0.42D;
    private static final double RANDOM_SIDE_PUSH = 0.72D;

    private ConfettiParticles() {
    }

    public static void spawnHandBurst(Level level, RandomSource random, double x, double y, double z, Vec3i direction) {
        spawnDirectionalBurst(level, random, x, y, z, direction, HAND_BURST_COUNT);
    }

    public static void spawnBallBurst(Level level, RandomSource random, Vec3 pos) {
        for (int i = 0; i < BALL_BURST_COUNT; ++i) {
            double angle = random.nextDouble() * (Math.TAU);
            double horizontalSpeed = 0.42D + random.nextDouble() * 0.34D;
            double velX = Math.cos(angle) * horizontalSpeed;
            double velY = 0.20D + random.nextDouble() * 0.12D;
            double velZ = Math.sin(angle) * horizontalSpeed;
            level.addParticle(Mod.CONFETTI_PARTICLE, pos.x, pos.y, pos.z, velX, velY, velZ);
        }
    }

    public static void spawnBallTrail(Level level, RandomSource random, double x, double y, double z, Vec3 normal) {
        // fallback for 0 length
        if (normal.lengthSqr() < 1.0E-8D) {
            normal = new Vec3(0.0D, 1.0D, 0.0D);
        }

        // helper vector that is not parallel to the normal
        Vec3 helper = Math.abs(normal.y) < 0.9D
                ? new Vec3(0.0D, 1.0D, 0.0D)
                : new Vec3(1.0D, 0.0D, 0.0D);

        // forming the circle plane
        Vec3 tan = normal.cross(helper).normalize();
        Vec3 bitan = normal.cross(tan).normalize();

        for (int i = 0; i < BALL_TRAIL_COUNT; ++i) {
            double angle = random.nextDouble() * Math.TAU;
            double push = (0.25 + random.nextDouble()) * DIRECTIONAL_PUSH;

            Vec3 velocity = tan.scale(Math.cos(angle) * push).add(bitan.scale(Math.sin(angle) * push));
            level.addParticle(Mod.CONFETTI_PARTICLE, x, y, z, velocity.x, velocity.y, velocity.z);
        }
    }

    private static void spawnDirectionalBurst(Level level, RandomSource random, double x, double y, double z, Vec3i direction, int count) {
        double dirX = direction.getX() * DIRECTIONAL_PUSH;
        double dirY = Math.max(0, direction.getY()) * 0.08D;
        double dirZ = direction.getZ() * DIRECTIONAL_PUSH;
        for (int i = 0; i < count; ++i) {
            double angle = random.nextDouble() * (Math.TAU);
            double force = random.nextDouble() * RANDOM_SIDE_PUSH;
            double velX = dirX + Math.cos(angle) * force;
            double velY = 0.25D + random.nextDouble() * 0.16D + dirY;
            double velZ = dirZ + Math.sin(angle) * force;
            level.addParticle(Mod.CONFETTI_PARTICLE, x, y, z, velX, velY, velZ);
        }
    }
}
