package xerca.xercamod.common.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.item.Items;

public class EntityTomato extends ThrowableItemProjectile {

    public EntityTomato(EntityType<? extends EntityTomato> type, Level world) {
        super(type, world);
    }

    public EntityTomato(Level worldIn, LivingEntity throwerIn) {
        super(Entities.TOMATO.get(), throwerIn, worldIn);
    }

    public EntityTomato(Level worldIn, double x, double y, double z) {
        super(Entities.TOMATO.get(), x, y, z, worldIn);
    }

    public EntityTomato(Level worldIn) {
        super(Entities.TOMATO.get(), worldIn);
    }

    public EntityTomato(PlayMessages.SpawnEntity ignoredSpawnEntity, Level world) {
        this(world);
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityRayTraceResult = (EntityHitResult) result;
            entityRayTraceResult.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 1f);
        }

        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            level.playSound(null, result.getLocation().x, result.getLocation().y, result.getLocation().z, SoundEvents.TOMATO_SPLASH.get(), SoundSource.PLAYERS, 1.0f, this.random.nextFloat() * 0.2F + 0.9F);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id)
    {
        if (id == 3)
        {
            for (int j = 0; j < 8; ++j) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ITEM_TOMATO.get())), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.28D, ((double) this.random.nextFloat() - 0.3D) * 0.28D, ((double) this.random.nextFloat() - 0.5D) * 0.28D);
            }
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.ITEM_TOMATO.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Items.ITEM_TOMATO.get());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
