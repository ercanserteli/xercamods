package xerca.xercatools.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xerca.xercatools.Mod;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.enchantment.GrabHookEnchantments;
import xerca.xercatools.item.ItemGrabHook;

import java.util.Objects;

public class EntityGrabHook extends Entity {
    private static final EntityDataAccessor<Integer> DATA_OWNER = SynchedEntityData.defineId(EntityGrabHook.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CAUGHT = SynchedEntityData.defineId(EntityGrabHook.class, EntityDataSerializers.INT);
    private static final double DEFAULT_SPEED = 1.5D;

    private int age;
    private boolean inGround;
    private int ticksInAir;
    private @Nullable Entity caughtEntity;
    private boolean returning;
    private boolean hasGrappling;
    private boolean hasGentle;
    private int turboLevel;
    private double speed;
    private @Nullable Player cachedOwner;

    public EntityGrabHook(EntityType<? extends EntityGrabHook> type, Level level) {
        super(type, level);
    }

    public EntityGrabHook(Level level) {
        this(Mod.HOOK, level);
    }

    public EntityGrabHook(Level level, Player owner, ItemStack rod, float pullAmount) {
        this(level);
        this.entityData.set(DATA_OWNER, owner.getId());
        this.cachedOwner = owner;
        this.hasGrappling = EnchantmentHelper.getItemEnchantmentLevel(GrabHookEnchantments.grapplingEnchantment(level.registryAccess()), rod) > 0;
        this.hasGentle = EnchantmentHelper.getItemEnchantmentLevel(GrabHookEnchantments.gentleGrab(level.registryAccess()), rod) > 0;
        this.turboLevel = EnchantmentHelper.getItemEnchantmentLevel(GrabHookEnchantments.turboGrab(level.registryAccess()), rod);
        this.speed = DEFAULT_SPEED * (1.0D + this.turboLevel * 0.25D) * pullAmount;

        float pitch = owner.getXRot();
        float yaw = owner.getYRot();
        float f2 = Mth.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-pitch * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-pitch * ((float) Math.PI / 180F));
        this.snapTo(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ(), yaw, pitch);

        Vec3 velocity = new Vec3(-f3, -(f5 / f4), -f2);
        double length = velocity.length();
        velocity = velocity.scale(this.speed / length);
        this.setDeltaMovement(velocity);
        //noinspection SuspiciousNameCombination
        this.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * (180F / Math.PI)));
        this.setXRot((float) (Mth.atan2(velocity.y, Math.sqrt(this.distanceToSqr(velocity))) * (180F / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntityGrabHook other)) {
            return false;
        }
        return Objects.equals(this.getUUID(), other.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(EntityGrabHook.class, this.getUUID());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(DATA_OWNER, -1);
        builder.define(DATA_CAUGHT, 0);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    public @Nullable Player getAngler() {
        if (this.cachedOwner != null && this.cachedOwner.isAlive()) {
            return this.cachedOwner;
        }
        Entity entity = this.level().getEntity(this.entityData.get(DATA_OWNER));
        if (entity instanceof Player player) {
            this.cachedOwner = player;
            return player;
        }
        return null;
    }

    public boolean isReturning() {
        return this.returning;
    }

    public @Nullable Entity getCaughtEntity() {
        return this.caughtEntity;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 128 * 128;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity, this.entityData.get(DATA_OWNER));
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        int ownerId = packet.getData();
        if (ownerId > 0) {
            this.entityData.set(DATA_OWNER, ownerId);
            Entity owner = this.level().getEntity(ownerId);
            if (owner instanceof Player player) {
                this.cachedOwner = player;
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        this.age = tag.getIntOr("age", 0);
        this.inGround = tag.getBooleanOr("in_ground", false);
        this.ticksInAir = tag.getIntOr("ticks_in_air", 0);
        this.returning = tag.getBooleanOr("returning", false);
        this.hasGrappling = tag.getBooleanOr("grappling", false);
        this.hasGentle = tag.getBooleanOr("gentle", false);
        this.turboLevel = tag.getIntOr("turbo", 0);
        this.speed = tag.getDoubleOr("speed", 0.0D);
        this.entityData.set(DATA_OWNER, tag.getIntOr("owner", 0));
        this.entityData.set(DATA_CAUGHT, tag.getIntOr("caught", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        tag.putInt("age", this.age);
        tag.putBoolean("in_ground", this.inGround);
        tag.putInt("ticks_in_air", this.ticksInAir);
        tag.putBoolean("returning", this.returning);
        tag.putBoolean("grappling", this.hasGrappling);
        tag.putBoolean("gentle", this.hasGentle);
        tag.putInt("turbo", this.turboLevel);
        tag.putDouble("speed", this.speed);
        tag.putInt("owner", this.entityData.get(DATA_OWNER));
        tag.putInt("caught", this.entityData.get(DATA_CAUGHT));
    }

    @Override
    public void tick() {
        super.tick();
        this.age++;

        Player angler = this.getAngler();
        if (this.level().isClientSide) {
            handleClientTick(angler);
        } else if (shouldDiscardOnServer(angler)) {
            discardHook();
            return;
        }

        if (angler == null) {
            return;
        }

        if (handleCaughtOrGroundedState(angler)) {
            return;
        }

        ++this.ticksInAir;
        if (this.ticksInAir == 20) {
            setReturning();
        }

        if (!this.level().isClientSide) {
            if (checkCollision(angler)) {
                return;
            }
            if (this.returning) {
                Vec3 target = angler.position().add(0.0D, angler.getEyeHeight(), 0.0D);
                Vec3 distance = target.subtract(this.position());
                if (distance.length() < 3.0D) {
                    discardHook();
                    return;
                }
                this.setDeltaMovement(distance.normalize().scale(this.speed).subtract(0.0D, 0.1D, 0.0D));
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private boolean checkCollision(Player angler) {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this,
                entity -> !entity.isSpectator() && (entity.isPickable() || entity instanceof ItemEntity) && entity != angler);
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity caught = ((EntityHitResult) hitResult).getEntity();
            if (!(caught instanceof LivingEntity) || caught == angler) {
                return false;
            }

            setCaughtEntity(caught, angler);
            return true;
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.inGround = true;
            float pitch = 0.9F + random.nextFloat() * 0.2F;
            this.level().playSound(null, this, net.minecraft.sounds.SoundEvents.GOAT_RAM_IMPACT, SoundSource.PLAYERS, 1.0F, pitch);
            this.level().playSound(null, angler, net.minecraft.sounds.SoundEvents.GOAT_RAM_IMPACT, SoundSource.PLAYERS, 0.75F, pitch);
            this.level().playSound(null, angler, SoundEvents.HOOK_CLINK, SoundSource.PLAYERS, 0.5F, pitch);
            if (this.hasGrappling) {
                this.setDeltaMovement(Vec3.ZERO);
                angler.noPhysics = true;
                angler.stopRiding();
            }
            return true;
        }
        return false;
    }

    private void handleClientTick(@Nullable Player angler) {
        if (angler == null) {
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            int caughtId = this.entityData.get(DATA_CAUGHT);
            if (caughtId > 0 && this.caughtEntity == null) {
                this.caughtEntity = this.level().getEntity(caughtId - 1);
            }
        }
    }

    private boolean shouldDiscardOnServer(@Nullable Player angler) {
        return angler == null || this.age > 80 || !angler.isAlive() || this.distanceToSqr(angler) > 4096.0D;
    }

    private boolean handleCaughtOrGroundedState(Player angler) {
        if (this.caughtEntity != null) {
            pullCaughtEntity(angler);
            return true;
        }
        if (!this.inGround) {
            return false;
        }
        if (this.hasGrappling) {
            pullUser(angler);
        } else {
            discardHook();
        }
        return true;
    }

    private void setCaughtEntity(Entity caught, Player angler) {
        this.caughtEntity = caught;
        this.entityData.set(DATA_CAUGHT, caught.getId() + 1);
        float pitch = 0.9F + random.nextFloat() * 0.2F;
        if (!this.hasGentle) {
            this.level().playSound(null, this, net.minecraft.sounds.SoundEvents.BEE_STING, SoundSource.PLAYERS, 3.0F, pitch);
            this.level().playSound(null, angler, net.minecraft.sounds.SoundEvents.BEE_STING, SoundSource.PLAYERS, 1.0F, pitch);
            this.level().playSound(null, angler, SoundEvents.HOOK_CLINK, SoundSource.PLAYERS, 0.25F, pitch);
            caught.hurt(this.damageSources().thrown(this, angler), 3.0F);
        } else {
            this.level().playSound(null, this, net.minecraft.sounds.SoundEvents.CHAIN_HIT, SoundSource.PLAYERS, 2.0F, pitch);
            this.level().playSound(null, angler, net.minecraft.sounds.SoundEvents.CHAIN_HIT, SoundSource.PLAYERS, 1.0F, pitch);
            this.level().playSound(null, angler, SoundEvents.HOOK_CLINK, SoundSource.PLAYERS, 0.25F, pitch);
        }

        caught.noPhysics = true;
        caught.stopRiding();
    }

    private void pullCaughtEntity(Player angler) {
        if (this.caughtEntity != null && !this.caughtEntity.isRemoved()) {
            Vec3 distance = angler.position().subtract(this.caughtEntity.position());
            if (distance.length() > 2.0D) {
                Vec3 pullVelocity = distance.normalize().scale(this.speed);
                this.caughtEntity.setDeltaMovement(pullVelocity);
                this.caughtEntity.hurtMarked = true;
                this.caughtEntity.hasImpulse = true;
            } else {
                discardHook();
                return;
            }
            double height = this.caughtEntity.getBbHeight() + 0.5D;
            this.setPos(this.caughtEntity.getX(), this.caughtEntity.getBoundingBox().minY + height * 0.8D, this.caughtEntity.getZ());
            return;
        }
        this.caughtEntity = null;
        discardHook();
    }

    private void pullUser(Player angler) {
        Vec3 distance = this.position().subtract(angler.position());
        if (distance.length() > 2.0D) {
            angler.setDeltaMovement(distance.normalize().scale(this.speed));
            angler.noPhysics = true;
            angler.hasImpulse = true;
            angler.hurtMarked = true;
        } else {
            discardHook();
        }
    }

    private void setReturning() {
        if (!this.returning) {
            this.returning = true;
        }
    }

    private void discardHook() {
        if (this.isRemoved()) {
            return;
        }
        if (this.caughtEntity != null) {
            this.caughtEntity.noPhysics = false;
        }
        Player angler = this.getAngler();
        if (angler != null) {
            angler.noPhysics = false;
            clearCastFlag(angler.getMainHandItem());
            clearCastFlag(angler.getOffhandItem());
            angler.level().playSound(null, angler, SoundEvents.HOOK_RETURN, SoundSource.PLAYERS, 1.0f, 0.9f + random.nextFloat() * 0.2f);
        }
        this.discard();
    }

    private static void clearCastFlag(ItemStack stack) {
        if (stack.getItem() instanceof ItemGrabHook) {
            ItemGrabHook.setCastState(stack, false);
        }
    }
}
