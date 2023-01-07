package xerca.xercamod.common.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.item.Items;

public class EntityConfettiBall extends ThrowableItemProjectile {
    public EntityConfettiBall(EntityType<? extends EntityConfettiBall> type, Level world) {
        super(type, world);
    }

    public EntityConfettiBall(Level worldIn, LivingEntity throwerIn) {
        super(Entities.CONFETTI_BALL.get(), throwerIn, worldIn);
    }

    public EntityConfettiBall(Level worldIn, double x, double y, double z) {
        super(Entities.CONFETTI_BALL.get(), x, y, z, worldIn);
    }

    public EntityConfettiBall(PlayMessages.SpawnEntity ignoredSpawnEntity, Level world) {
        super(Entities.CONFETTI_BALL.get(), world);
    }

    private void spawnConfetti(double x, double y, double z) {
        for (int j = 0; j < 12; ++j) {
            this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ITEM_CONFETTI.get())), x, y, z, ((double) this.random.nextFloat() - 0.5D) * 0.3D, ((double) this.random.nextFloat()) * 0.5D, ((double) this.random.nextFloat() - 0.5D) * 0.3D);
        }
    }

    private void spawnConfetti(Vec3 vec) {
        spawnConfetti(vec.x, vec.y, vec.z);
    }

    @Override
    protected void onHit(HitResult result) {
        spawnConfetti(result.getLocation());
        if (!this.level.isClientSide) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.CRACK.get(), SoundSource.PLAYERS, 2.0f, this.random.nextFloat() * 0.4F + 0.8F);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();
        if(this.tickCount % 4 == 0){
            if(!this.level.isClientSide){
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.CRACK.get(), SoundSource.PLAYERS, 2.0f, this.random.nextFloat() * 0.4F + 0.8F);
            }
            else{
                spawnConfetti(this.getX(), this.getY(), this.getZ());
            }
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.ITEM_CONFETTI_BALL.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Items.ITEM_CONFETTI_BALL.get());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}