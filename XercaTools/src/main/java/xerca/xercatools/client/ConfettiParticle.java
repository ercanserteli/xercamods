package xerca.xercatools.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class ConfettiParticle extends TextureSheetParticle {
    private static final float PIECE_GRID = 8.0F;
    private final float rollSpeed;
    private final float swayPhase;
    private final float swayAmount;
    private final float uo;
    private final float vo;

    private ConfettiParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.friction = 0.82F;
        this.gravity = 0.12F;
        this.quadSize *= 0.5F;
        this.hasPhysics = true;
        this.lifetime = 100 + this.random.nextInt(40);
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
        this.oRoll = this.roll;
        this.rollSpeed = (this.random.nextFloat() - 0.5F) * 0.28F;
        this.swayPhase = this.random.nextFloat() * Mth.TWO_PI;
        this.swayAmount = 0.0015F + this.random.nextFloat() * 0.0020F;
        this.uo = this.random.nextFloat() * (PIECE_GRID - 1.0F);
        this.vo = this.random.nextFloat() * (PIECE_GRID - 1.0F);
        this.pickSprite(spriteSet);
        this.setParticleSpeed(xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void tick() {
        this.oRoll = this.roll;
        super.tick();
        if (this.removed) {
            return;
        }

        this.roll += this.rollSpeed;
        float sway = Mth.sin(this.age * 0.35F + this.swayPhase) * this.swayAmount;
        this.xd += sway;
        this.zd += Mth.cos(this.age * 0.35F + this.swayPhase) * this.swayAmount;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU(this.uo / PIECE_GRID);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU((this.uo + 1.0F) / PIECE_GRID);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / PIECE_GRID);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / PIECE_GRID);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ConfettiParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
