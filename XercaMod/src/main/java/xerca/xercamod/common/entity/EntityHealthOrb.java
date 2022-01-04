package xerca.xercamod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
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
import java.util.List;

public class EntityHealthOrb extends Entity implements IEntityAdditionalSpawnData {
    private static final int LIFETIME = 500;
    private static final int ENTITY_SCAN_PERIOD = 10;
    private static final int ORB_GROUPS_PER_AREA = 10;
    private int age;
    private int health = 5;
    private int count = 1;
    private Player followingPlayer;
    private Player donorPlayer;
    private Player attackingPlayer;

    public EntityHealthOrb(Level level, double x, double y, double z, @Nullable Player donorPlayer, @Nullable Player attackingPlayer) {
        this(Entities.HEALTH_ORB, level);
        this.setPos(x, y, z);
        this.setYRot((float)(this.random.nextDouble() * 360.0D));
        this.setDeltaMovement((this.random.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D, this.random.nextDouble() * 0.2D * 2.0D, (this.random.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D);
        this.donorPlayer = donorPlayer;
        this.attackingPlayer = attackingPlayer;
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
            this.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        if (!this.level.noCollision(this.getBoundingBox())) {
            this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
        }

        if (this.tickCount % ENTITY_SCAN_PERIOD == 1) {
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

        this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.98D, f));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
        }

        ++this.age;
        if (this.age >= LIFETIME) {
            this.discard();
        }

    }

    private void scanForEntities() {
        if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 36.0D) {
            if(attackingPlayer != null && attackingPlayer.distanceToSqr(this) <= 36.0D){
                followingPlayer = attackingPlayer;
            }
            else{
                if(donorPlayer != null){
                    this.followingPlayer = this.level.getNearestPlayer(TargetingConditions.forNonCombat().range(5.0D), donorPlayer);
                }
                else{
                    this.followingPlayer = this.level.getNearestPlayer(this, 5.0D);
                }
            }
        }

        if (this.level instanceof ServerLevel) {
            for(EntityHealthOrb healthOrb : this.level.getEntities(EntityTypeTest.forClass(EntityHealthOrb.class), this.getBoundingBox().inflate(0.5D), this::canMerge)) {
                this.merge(healthOrb);
            }
        }
    }

    public static void award(ServerLevel level, Entity donor, Entity attacker, int val) {
        if(val > 0) {
            Vec3 pos = donor.getEyePosition();
            for (int i = 0; i < val; ++i) {
                if (!tryMergeToExisting(level, pos)) {
                    level.addFreshEntity(new EntityHealthOrb(level, pos.x(), pos.y(), pos.z(), donor instanceof Player ? (Player)donor : null, attacker instanceof Player ? (Player)attacker : null));
                }
            }
        }
    }

    private static boolean tryMergeToExisting(ServerLevel p_147097_, Vec3 p_147098_) {
        AABB aabb = AABB.ofSize(p_147098_, 1.0D, 1.0D, 1.0D);
        int i = p_147097_.getRandom().nextInt(ORB_GROUPS_PER_AREA);
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
        return !healthOrb.isRemoved() && (healthOrb.getId() - id) % ORB_GROUPS_PER_AREA == 0;
    }

    private void merge(EntityHealthOrb healthOrb) {
        this.count += healthOrb.count;
        this.age = Math.min(this.age, healthOrb.age);
        healthOrb.discard();
    }

    private void setUnderwaterMovement() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x * (double)0.99F, Math.min(vec3.y + (double)5.0E-4F, 0.06F), vec3.z * (double)0.99F);
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
        tag.putInt("AttackerId", this.attackingPlayer != null ? attackingPlayer.getId() : -1);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        this.health = tag.getShort("Health");
        this.age = tag.getShort("Age");
        this.count = Math.max(tag.getInt("Count"), 1);
        int donorId = tag.getInt("DonorId");
        int attackerId = tag.getInt("AttackerId");
        if(donorId >= 0){
            Entity donor = level.getEntity(donorId);
            if(donor instanceof Player playerDonor){
                donorPlayer = playerDonor;
            }
        }
        if(attackerId >= 0){
            Entity attacker = level.getEntity(attackerId);
            if(attacker instanceof Player playerAttacker){
                attackingPlayer = playerAttacker;
            }
        }
    }

    public void playerTouch(Player player) {
        if (!this.level.isClientSide && !player.equals(donorPlayer) && (age > 80 || player.equals(attackingPlayer))) {
            if (player.takeXpDelay == 0) {
                player.level.playSound(null, player, SoundEvents.ABSORB, getSoundSource(), 1.0f, 0.8f + random.nextFloat()*0.4f);
                player.takeXpDelay = 1;
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
        buffer.writeInt(this.attackingPlayer != null ? attackingPlayer.getId() : -1);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        int donorId = additionalData.readInt();
        if(donorId >= 0){
            Entity donor = level.getEntity(donorId);
            if(donor instanceof Player playerDonor){
                donorPlayer = playerDonor;
            }
        }
        int attackerId = additionalData.readInt();
        if(attackerId >= 0){
            Entity attacker = level.getEntity(attackerId);
            if(attacker instanceof Player playerAttacker){
                attackingPlayer = playerAttacker;
            }
        }
    }
}
