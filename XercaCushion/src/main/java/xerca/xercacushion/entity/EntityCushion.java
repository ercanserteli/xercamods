package xerca.xercacushion.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xerca.xercacushion.Mod;
import xerca.xercacushion.block.BlockCushion;
import xerca.xercacushion.block.Blocks;
import xerca.xercacushion.item.Items;

public class EntityCushion extends Entity {
    private static final double PISTON_CLEARANCE = 0.01D;
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(EntityCushion.class, EntityDataSerializers.INT);

    public EntityCushion(EntityType<? extends EntityCushion> type, Level level) {
        super(type, level);
        this.setNoGravity(false);
    }

    public EntityCushion(Level level) {
        this(Mod.CUSHION, level);
    }

    public EntityCushion(Level level, double x, double y, double z, int variant) {
        this(level);
        this.setVariant(variant);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(DATA_VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    public BlockCushion getVariantBlock() {
        return Blocks.all()[getVariant()];
    }

    private boolean hasSupportBelow() {
        AABB supportBox = this.getBoundingBox().deflate(1.0E-3D, 0.0D, 1.0E-3D).move(0.0D, -0.05D, 0.0D);
        return this.level().getBlockCollisions(this, supportBox).iterator().hasNext();
    }

    private double getSupportTopY() {
        double entityBottom = this.getBoundingBox().minY;
        double supportTop = Double.NEGATIVE_INFINITY;
        AABB supportBox = this.getBoundingBox().deflate(1.0E-3D, 0.0D, 1.0E-3D).move(0.0D, -0.05D, 0.0D);
        for (VoxelShape shape : this.level().getBlockCollisions(this, supportBox)) {
            supportTop = Math.max(supportTop, shape.bounds().maxY);
        }
        return supportTop > Double.NEGATIVE_INFINITY ? supportTop : entityBottom;
    }

    private static double removePistonClearance(double movement) {
        if (movement > 0.0D) {
            return Math.max(0.0D, movement - PISTON_CLEARANCE);
        }
        return Math.min(0.0D, movement + PISTON_CLEARANCE);
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        if (type == MoverType.PISTON) {
            movement = new Vec3(
                    removePistonClearance(movement.x),
                    removePistonClearance(movement.y),
                    removePistonClearance(movement.z)
            );
        }
        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();
        super.move(type, movement);

        boolean moved = this.getX() != oldX || this.getY() != oldY || this.getZ() != oldZ;
        if (moved && !this.isRemoved() && this.level() instanceof ServerLevel serverLevel
                && !serverLevel.getEntitiesOfClass(EntityCushion.class, this.getBoundingBox(),
                cushion -> cushion != this && !cushion.isRemoved()).isEmpty()) {
            this.discard();
            this.markHurt();
            this.onBroken(serverLevel, null);
        }
    }

    @Override
    public void tick() {
        super.tick();
        double verticalSpeed = this.getDeltaMovement().y - 0.08D;
        this.move(MoverType.SELF, new Vec3(0.0D, verticalSpeed, 0.0D));

        boolean supported = this.verticalCollisionBelow || this.hasSupportBelow();
        this.setOnGround(supported);

        if (supported) {
            double supportTop = this.getSupportTopY();
            if (this.getY() > supportTop + 1.0E-3D) {
                this.setPos(this.getX(), supportTop, this.getZ());
            }
            verticalSpeed = 0.0D;
        } else {
            verticalSpeed = this.getDeltaMovement().y * 0.98D;
        }
        this.setDeltaMovement(0.0D, verticalSpeed, 0.0D);
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            this.hurtServer(serverLevel, this.damageSources().playerAttack(player), 0.0F);
        }
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (this.isInvulnerableToBase(source)) {
            return false;
        }

        if (!this.isRemoved()) {
            this.discard();
            this.markHurt();
            this.onBroken(serverLevel, source.getEntity());
        }

        return true;
    }

    private void onBroken(ServerLevel serverLevel, @Nullable Entity breaker) {
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }

        this.playSound(SoundEvents.WOOL_BREAK, 1.0F, 1.0F);
        if (breaker instanceof Player player && player.getAbilities().instabuild) {
            return;
        }

        this.spawnAtLocation(serverLevel, new ItemStack(Items.byVariant(getVariant())));
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.setVariant(input.getIntOr("cushion", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("cushion", this.getVariant());
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float partialTick) {
        return new Vec3(0.0D, 0.125D, 0.0D);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        if (!this.level().isClientSide) {
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!this.level().isClientSide && !this.isVehicle()) {
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.byVariant(getVariant()));
    }

}
