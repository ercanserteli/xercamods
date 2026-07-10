package xerca.xercatools.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xerca.xercatools.Mod;
import xerca.xercatools.SoundEvents;

import java.util.List;
import java.util.Objects;

public class EntityHealthOrb extends Entity {
    private static final int LIFETIME = 500;
    private static final int ENTITY_SCAN_PERIOD = 10;
    private static final int ORB_GROUPS_PER_AREA = 6;

    private int age;
    private int health = 5;
    private int count = 1;
    private @Nullable Player followingPlayer;
    private @Nullable Player donorPlayer;
    private @Nullable Player attackingPlayer;

    public EntityHealthOrb(EntityType<? extends EntityHealthOrb> entityType, Level level) {
        super(entityType, level);
    }

    public EntityHealthOrb(Level level, double x, double y, double z, @Nullable Player donorPlayer, @Nullable Player attackingPlayer) {
        this(Mod.HEALTH_ORB, level);
        this.setPos(x, y, z);
        this.setYRot((float) (this.random.nextDouble() * 360.0D));
        this.setDeltaMovement(
                (this.random.nextDouble() * 0.2F - 0.1F) * 2.0D,
                this.random.nextDouble() * 0.2D * 2.0D,
                (this.random.nextDouble() * 0.2F - 0.1F) * 2.0D
        );
        this.donorPlayer = donorPlayer;
        this.attackingPlayer = attackingPlayer;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntityHealthOrb other)) {
            return false;
        }
        return Objects.equals(this.getUUID(), other.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(EntityHealthOrb.class, this.getUUID());
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // This entity does not need synced data beyond the base Entity state.
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.isEyeInFluid(FluidTags.WATER)) {
            setUnderwaterMovement();
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }

        if (this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
            this.setDeltaMovement(
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F,
                    0.2F,
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
            );
        }

        if (!this.level().noCollision(this.getBoundingBox())) {
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }

        if (this.tickCount % ENTITY_SCAN_PERIOD == 1) {
            scanForEntities();
        }

        if (this.followingPlayer != null && (this.followingPlayer.isSpectator() || this.followingPlayer.isDeadOrDying())) {
            this.followingPlayer = null;
        }

        Player following = this.followingPlayer;
        if (following != null) {
            Vec3 toPlayer = new Vec3(
                    following.getX() - this.getX(),
                    following.getY() + following.getEyeHeight() / 2.0D - this.getY(),
                    following.getZ() - this.getZ()
            );
            double distSq = toPlayer.lengthSqr();
            if (distSq < 16.0D) {
                double pull = 1.0D - Math.sqrt(distSq) / 4.0D;
                this.setDeltaMovement(this.getDeltaMovement().add(toPlayer.normalize().scale(pull * pull * 0.1D)));
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        float friction = 0.98F;
        this.setDeltaMovement(this.getDeltaMovement().multiply(friction, 0.98D, friction));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
        }

        ++this.age;
        if (this.age >= LIFETIME) {
            this.discard();
        }
    }

    private void scanForEntities() {
        if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 36.0D) {
            Player donor = this.donorPlayer;
            if (attackingPlayer != null && attackingPlayer.distanceToSqr(this) <= 36.0D) {
                followingPlayer = attackingPlayer;
            } else if (donor != null) {
                this.followingPlayer = this.level().getNearestPlayer(donor.getX(), donor.getY(), donor.getZ(), 5.0D, player -> player.isAlive() && !player.isSpectator());
            } else {
                this.followingPlayer = this.level().getNearestPlayer(this, 5.0D);
            }
        }

        if (this.level() instanceof ServerLevel) {
            for (EntityHealthOrb other : this.level().getEntities(EntityTypeTest.forClass(EntityHealthOrb.class), this.getBoundingBox().inflate(0.5D), this::canMerge)) {
                this.merge(other);
            }
        }
    }

    public static void award(ServerLevel level, Entity donor, Entity attacker, int val) {
        if (val <= 0) return;
        Vec3 pos = donor.getEyePosition();
        for (int i = 0; i < val; ++i) {
            if (!tryMergeToExisting(level, pos)) {
                level.addFreshEntity(new EntityHealthOrb(level, pos.x(), pos.y(), pos.z(),
                        donor instanceof Player p ? p : null,
                        attacker instanceof Player p ? p : null));
            }
        }
    }

    private static boolean tryMergeToExisting(ServerLevel level, Vec3 pos) {
        AABB aabb = AABB.ofSize(pos, 1.0D, 1.0D, 1.0D);
        int groupId = level.getRandom().nextInt(ORB_GROUPS_PER_AREA);
        List<EntityHealthOrb> list = level.getEntities(EntityTypeTest.forClass(EntityHealthOrb.class), aabb,
                orb -> canMerge(orb, groupId));
        if (!list.isEmpty()) {
            EntityHealthOrb existing = list.getFirst();
            ++existing.count;
            existing.age = 0;
            return true;
        }
        return false;
    }

    private boolean canMerge(EntityHealthOrb other) {
        return other != this && canMerge(other, this.getId());
    }

    private static boolean canMerge(EntityHealthOrb orb, int id) {
        return !orb.isRemoved() && (orb.getId() - id) % ORB_GROUPS_PER_AREA == 0;
    }

    private void merge(EntityHealthOrb other) {
        this.count += other.count;
        this.age = Math.min(this.age, other.age);
        other.discard();
    }

    private void setUnderwaterMovement() {
        Vec3 vel = this.getDeltaMovement();
        this.setDeltaMovement(vel.x * 0.99F, Math.min(vel.y + 5.0E-4F, 0.06F), vel.z * 0.99F);
    }

    @Override
    protected void doWaterSplashEffect() {
        // Orbs do not create a splash effect.
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.isRemoved()) return false;
        if (this.isInvulnerableToBase(source)) return false;
        this.markHurt();
        this.health = (int) (this.health - damage);
        if (this.health <= 0) {
            this.discard();
        }
        return true;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput tag) {
        tag.putShort("Health", (short) this.health);
        tag.putShort("Age", (short) this.age);
        tag.putInt("Count", this.count);
        tag.putInt("DonorId", this.donorPlayer != null ? this.donorPlayer.getId() : -1);
        tag.putInt("AttackerId", this.attackingPlayer != null ? this.attackingPlayer.getId() : -1);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput tag) {
        this.health = tag.getShortOr("Health", (short) 0);
        this.age = tag.getShortOr("Age", (short) 0);
        this.count = Math.max(tag.getIntOr("Count", 0), 1);
        int donorId = tag.getIntOr("DonorId", -1);
        if (donorId >= 0 && level().getEntity(donorId) instanceof Player p) {
            donorPlayer = p;
        }
        int attackerId = tag.getIntOr("AttackerId", -1);
        if (attackerId >= 0 && level().getEntity(attackerId) instanceof Player p) {
            attackingPlayer = p;
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && !player.equals(donorPlayer) && (age > 80 || player.equals(attackingPlayer)) && player.takeXpDelay == 0) {
            player.level().playSound(null, player, SoundEvents.ABSORB, SoundSource.PLAYERS, 1.0f, 0.8f + random.nextFloat() * 0.4f);
            player.takeXpDelay = 1;
            player.setHealth(player.getHealth() + 1);
            --this.count;
            if (this.count == 0) {
                this.discard();
            }
        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }
}
