package xerca.xercatools.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xerca.xercatools.Mod;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.enchantment.GrabHookEnchantments;
import xerca.xercatools.item.ItemGrabHook;

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
        this.hasGrappling = EnchantmentHelper.getItemEnchantmentLevel(GrabHookEnchantments.grappling(level.registryAccess()), rod) > 0;
        this.hasGentle = EnchantmentHelper.getItemEnchantmentLevel(GrabHookEnchantments.gentleGrab(level.registryAccess()), rod) > 0;
        this.turboLevel = EnchantmentHelper.getItemEnchantmentLevel(GrabHookEnchantments.turboGrab(level.registryAccess()), rod);
        this.speed = DEFAULT_SPEED * (1.0D + this.turboLevel * 0.25D) * pullAmount;

        float pitch = owner.getXRot();
        float yaw = owner.getYRot();
        float f2 = Mth.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = Mth.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -Mth.cos(-pitch * ((float) Math.PI / 180F));
        float f5 = Mth.sin(-pitch * ((float) Math.PI / 180F));
        this.moveTo(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ(), yaw, pitch);

        Vec3 velocity = new Vec3(-f3, -(f5 / f4), -f2);
        double length = velocity.length();
        velocity = velocity.scale(this.speed / length);
        this.setDeltaMovement(velocity);
        this.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * (180F / Math.PI)));
        this.setXRot((float) (Mth.atan2(velocity.y, Math.sqrt(this.distanceToSqr(velocity))) * (180F / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(DATA_OWNER, -1);
        builder.define(DATA_CAUGHT, 0);
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

    public int getAge() {
        return this.age;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 128 * 128;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(@NotNull ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity, this.entityData.get(DATA_OWNER));
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
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
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getInt("age");
        this.inGround = tag.getBoolean("in_ground");
        this.ticksInAir = tag.getInt("ticks_in_air");
        this.returning = tag.getBoolean("returning");
        this.hasGrappling = tag.getBoolean("grappling");
        this.hasGentle = tag.getBoolean("gentle");
        this.turboLevel = tag.getInt("turbo");
        this.speed = tag.getDouble("speed");
        this.entityData.set(DATA_OWNER, tag.getInt("owner"));
        this.entityData.set(DATA_CAUGHT, tag.getInt("caught"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
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
            if (angler == null) {
                this.move(MoverType.SELF, this.getDeltaMovement());
                return;
            }
            int caughtId = this.entityData.get(DATA_CAUGHT);
            if (caughtId > 0 && this.caughtEntity == null) {
                this.caughtEntity = this.level().getEntity(caughtId - 1);
            }
        } else if (angler == null || this.age > 80 || !angler.isAlive() || this.distanceToSqr(angler) > 4096.0D) {
            discardHook();
            return;
        }

        if (this.caughtEntity != null) {
            pullCaughtEntity(angler);
            return;
        }
        if (this.inGround) {
            if (this.hasGrappling) {
                pullUser(angler);
            } else {
                discardHook();
            }
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
                Vec3 velocity = target.subtract(this.position());
                if (velocity.length() < 3.0D) {
                    discardHook();
                    return;
                }
                this.setDeltaMovement(velocity.normalize().scale(this.speed).subtract(0.0D, 0.1D, 0.0D));
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @SuppressFBWarnings(value = "NP", justification = "caughtEntity is assigned from hit result before same-branch dereference.")
    private boolean checkCollision(Player angler) {
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this,
                entity -> !entity.isSpectator() && (entity.isPickable() || entity instanceof ItemEntity) && entity != angler);
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity caught = ((EntityHitResult) hitResult).getEntity();
            if (!(caught instanceof LivingEntity) || caught == angler) {
                return false;
            }

            this.caughtEntity = caught;
            this.entityData.set(DATA_CAUGHT, caught.getId() + 1);
            if (!this.hasGentle) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.HOOK_IMPACT, SoundSource.PLAYERS, 1.0F, this.level().random.nextFloat() * 0.2F + 0.9F);
                caught.hurt(this.damageSources().thrown(this, angler), 3.0F);
                if (!caught.isAlive()) {
                    discardHook();
                    return true;
                }
            } else {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.HOOK_IMPACT, SoundSource.PLAYERS, 0.6F, this.level().random.nextFloat() * 0.2F + 1.5F);
            }

            this.caughtEntity.noPhysics = true;
            this.caughtEntity.stopRiding();
            return true;
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.inGround = true;
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.HOOK_IMPACT, SoundSource.PLAYERS, 0.8F, 0.9F);
            if (this.hasGrappling) {
                this.setDeltaMovement(Vec3.ZERO);
                angler.noPhysics = true;
                angler.stopRiding();
            }
            return true;
        }
        return false;
    }

    private void pullCaughtEntity(Player angler) {
        if (this.caughtEntity != null && this.caughtEntity.isAlive()) {
            Vec3 velocity = angler.position().subtract(this.caughtEntity.position());
            if (velocity.length() > 2.0D) {
                Vec3 pullVelocity = velocity.normalize().scale(this.speed);
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
    }

    private void pullUser(Player angler) {
        Vec3 velocity = this.position().subtract(angler.position());
        if (velocity.length() > 2.0D) {
            angler.setDeltaMovement(velocity.normalize().scale(this.speed));
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
        }
        this.discard();
    }

    private static void clearCastFlag(ItemStack stack) {
        if (stack.getItem() instanceof ItemGrabHook) {
            ItemGrabHook.setCastState(stack, false);
        }
    }
}
