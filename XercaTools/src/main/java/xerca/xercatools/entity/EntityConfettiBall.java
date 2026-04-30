package xerca.xercatools.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import xerca.xercatools.Mod;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.item.Items;
import xerca.xercatools.particle.ConfettiParticles;

public class EntityConfettiBall extends ThrowableItemProjectile {
    private static final byte IMPACT_PARTICLES_EVENT = 3;

    public EntityConfettiBall(EntityType<? extends EntityConfettiBall> type, Level world) {
        super(type, world);
    }

    public EntityConfettiBall(Level worldIn, LivingEntity throwerIn) {
        super(Mod.ENTITY_CONFETTI_BALL, throwerIn, worldIn);
    }

    public EntityConfettiBall(Level worldIn, double x, double y, double z) {
        super(Mod.ENTITY_CONFETTI_BALL, x, y, z, worldIn);
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, IMPACT_PARTICLES_EVENT);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SOUND_CRACK, SoundSource.PLAYERS, 3.0f, this.random.nextFloat() * 0.4F + 0.8F);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == IMPACT_PARTICLES_EVENT) {
            ConfettiParticles.spawnBallBurst(this.level(), this.random, this.position());
            return;
        }
        super.handleEntityEvent(id);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 4 == 0) {
            if (!this.level().isClientSide) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SOUND_CRACK, SoundSource.PLAYERS, 2.0f, this.random.nextFloat() * 0.4F + 0.8F);
            } else {
                ConfettiParticles.spawnBallTrail(this.level(), this.random, this.getX(), this.getY(), this.getZ(), this.getDeltaMovement().normalize());
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.CONFETTI_BALL;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.CONFETTI_BALL);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }
}
