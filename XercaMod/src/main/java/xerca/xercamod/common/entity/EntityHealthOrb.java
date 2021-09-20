package xerca.xercamod.common.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import xerca.xercamod.common.SoundEvents;

import javax.annotation.Nullable;

public class EntityHealthOrb extends Entity implements IEntityAdditionalSpawnData {
    private static final int LIFETIME = 6000;
    private static final int ENTITY_SCAN_PERIOD = 20;
    private static final int MAX_FOLLOW_DIST = 8;
    private static final int ORB_GROUPS_PER_AREA = 40;
    private static final double ORB_MERGE_DISTANCE = 0.5D;
    private int age;
    private int health = 5;
    private int count = 1;
    private Player followingPlayer;
    private Player donorPlayer;

    public EntityHealthOrb(Level level, double x, double y, double z, @Nullable Player donorPlayer) {
        this(Entities.HEALTH_ORB, level);
        this.setPos(x, y, z);
        this.setYRot((float)(this.random.nextDouble() * 360.0D));
        this.setDeltaMovement((this.random.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D);
        this.donorPlayer = donorPlayer;
    }

    public EntityHealthOrb(EntityType<? extends EntityHealthOrb> entityType, Level level) {
        super(entityType, level);
    }

    public EntityHealthOrb(FMLPlayMessages.SpawnEntity spawnEntity, Level world) {
        this(world);
    }

    public EntityHealthOrb(Level worldIn) {
        super(Entities.HEALTH_ORB, worldIn);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    protected void defineSynchedData() {
    }

    public void tick() {
        super.tick();
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.isEyeInFluid(FluidTags.WATER)) {
            this.setUnderwaterMovement();
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
        }

        if (this.level.getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
            this.setDeltaMovement((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), (double)0.2F, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
        }

        if (!this.level.noCollision(this.getBoundingBox())) {
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }

        if (this.tickCount % 20 == 1) {
            this.scanForEntities();
        }

        if (this.followingPlayer != null && (this.followingPlayer.isSpectator() || this.followingPlayer.isDeadOrDying())) {
            this.followingPlayer = null;
        }

        if (this.followingPlayer != null) {
            Vec3 vec3 = new Vec3(this.followingPlayer.getX() - this.getX(), this.followingPlayer.getY() + (double)this.followingPlayer.getEyeHeight() / 2.0D - this.getY(), this.followingPlayer.getZ() - this.getZ());
            double d0 = vec3.lengthSqr();
            if (d0 < 16.0D) {
                double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                this.setDeltaMovement(this.getDeltaMovement().add(vec3.normalize().scale(d1 * d1 * 0.1D)));
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        float f = 0.98F;
        if (this.onGround) {
            BlockPos pos =new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
            f = this.level.getBlockState(pos).getFriction(this.level, pos, this) * 0.98F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.98D, (double)f));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
        }

        ++this.age;
        if (this.age >= 6000) {
            this.discard();
        }

    }

    private void scanForEntities() {
        if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 16.0D) {
            if(donorPlayer != null){
                this.followingPlayer = this.level.getNearestPlayer(TargetingConditions.forNonCombat().range(4.0D), donorPlayer);
            }
            else{
                this.followingPlayer = this.level.getNearestPlayer(this, 4.0D);
            }
        }

        if (this.level instanceof ServerLevel) {
            for(EntityHealthOrb healthOrb : this.level.getEntities(EntityTypeTest.forClass(EntityHealthOrb.class), this.getBoundingBox().inflate(0.5D), this::canMerge)) {
                this.merge(healthOrb);
            }
        }
    }

    public static void award(ServerLevel level, Entity donor, int val) {
        if(val > 0) {
            Vec3 pos = donor.getEyePosition();
            for (int i = 0; i < val; ++i) {
                if (!tryMergeToExisting(level, pos)) {
                    level.addFreshEntity(new EntityHealthOrb(level, pos.x(), pos.y(), pos.z(), donor instanceof Player ? (Player)donor : null));
                }
            }
        }
    }

    private static boolean tryMergeToExisting(ServerLevel p_147097_, Vec3 p_147098_) {
        AABB aabb = AABB.ofSize(p_147098_, 1.0D, 1.0D, 1.0D);
        int i = p_147097_.getRandom().nextInt(40);
        List<EntityHealthOrb> list = p_147097_.getEntities(EntityTypeTest.forClass(EntityHealthOrb.class), aabb,
                (p_147081_) -> canMerge(p_147081_, i));
        if (!list.isEmpty()) {
            EntityHealthOrb healthOrb = list.get(0);
            ++healthOrb.count;
            healthOrb.age = 0;
            return true;
        } else {
            return false;
        }
    }

    private boolean canMerge(EntityHealthOrb healthOrb) {
        return healthOrb != this && canMerge(healthOrb, this.getId());
    }

    private static boolean canMerge(EntityHealthOrb healthOrb, int id) {
        return !healthOrb.isRemoved() && (healthOrb.getId() - id) % 40 == 0;
    }

    private void merge(EntityHealthOrb healthOrb) {
        this.count += healthOrb.count;
        this.age = Math.min(this.age, healthOrb.age);
        healthOrb.discard();
    }

    private void setUnderwaterMovement() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x * (double)0.99F, Math.min(vec3.y + (double)5.0E-4F, (double)0.06F), vec3.z * (double)0.99F);
    }

    protected void doWaterSplashEffect() {
    }

    public boolean hurt(DamageSource source, float damage) {
        if (this.level.isClientSide || this.isRemoved()) return false; //Forge: Fixes MC-53850
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markHurt();
            this.health = (int)((float)this.health - damage);
            if (this.health <= 0) {
                this.discard();
            }

            return true;
        }
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort("Health", (short)this.health);
        tag.putShort("Age", (short)this.age);
        tag.putInt("Count", this.count);
        tag.putInt("DonorId", this.donorPlayer != null ? donorPlayer.getId() : -1);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        this.health = tag.getShort("Health");
        this.age = tag.getShort("Age");
        this.count = Math.max(tag.getInt("Count"), 1);
        int donorId = tag.getInt("DonorId");
        if(donorId >= 0){
            Entity donor = level.getEntity(donorId);
            if(donor != null && donor instanceof Player){
                donorPlayer = (Player)donor;
            }
        }
    }

    public void playerTouch(Player player) {
        if (!this.level.isClientSide && player != donorPlayer) {
            if (player.takeXpDelay == 0) {
                player.level.playSound(null, player, SoundEvents.ABSORB, getSoundSource(), 1.0f, 0.8f + random.nextFloat()*0.4f);
                player.takeXpDelay = 2;
                player.setHealth(player.getHealth() + 1);

                --this.count;
                if (this.count == 0) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(this.donorPlayer != null ? donorPlayer.getId() : -1);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        int donorId = additionalData.readInt();
        if(donorId >= 0){
            Entity donor = level.getEntity(donorId);
            if(donor instanceof Player){
                donorPlayer = (Player)donor;
            }
        }
    }
}
