package xerca.xercamod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import xerca.xercamod.common.block.BlockCushion;
import xerca.xercamod.common.item.ItemCushion;

import javax.annotation.Nullable;

public class EntityCushion extends Entity implements IEntityAdditionalSpawnData {
    private ItemCushion item;
    public BlockCushion block;
    private int cushionIndex;

    static BlockCushion[] blockCushions;
    static ItemCushion[] itemCushions;


    public EntityCushion(EntityType<? extends EntityCushion> type, Level world) {
        super(type, world);
    }

    public EntityCushion(Level worldIn) {
        super(Entities.CUSHION, worldIn);
        this.setNoGravity(false);
    }
    public EntityCushion(Level worldIn, double x, double y, double z, ItemCushion item) {
        this(worldIn);
        this.setPos(x, y, z);
        this.item = item;
        this.block = item.getBlock();
        this.cushionIndex = block.cushionIndex;
    }

    public EntityCushion(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(world);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick(){
        super.tick();
        move(MoverType.SELF, new Vec3(0, -0.16, 0));
        setBoundingBox(new AABB(getX() - 0.5, getY(), getZ() - 0.5, getX() + 0.5, getY() + getBbHeight(), getZ() + 0.5));
    }


    @Override
    public boolean skipAttackInteraction(Entity entityIn) {
        return entityIn instanceof Player && this.hurt(DamageSource.playerAttack((Player) entityIn), 0.0F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.isRemoved() && !this.level.isClientSide) {
                this.remove(RemovalReason.DISCARDED);
                this.markHurt();
                this.onBroken(source.getEntity());
            }

            return true;
        }
    }

    public void onBroken(@Nullable Entity brokenEntity) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.WOOL_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof Player) {
                Player entityplayer = (Player)brokenEntity;
                if (entityplayer.getAbilities().instabuild) {
                    return;
                }
            }

            this.spawnAtLocation(item);
        }
    }
    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        cushionIndex = compound.getInt("cushion");
        this.block = blockCushions[cushionIndex];
        this.item = itemCushions[cushionIndex];
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("cushion", cushionIndex);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(cushionIndex);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.cushionIndex = buffer.readInt();
        this.block = blockCushions[cushionIndex];
        this.item = itemCushions[cushionIndex];
    }

    @Override
    public double getPassengersRidingOffset() {
        return -0.125;
    }

    @Override
    public boolean isPickable() {
        return true;
    }
    /**
     * Applies the given player interaction to this Entity.
     */
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!this.level.isClientSide) {
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSteppingCarefully()) {
            return InteractionResult.PASS;
        } else {
            if (!this.level.isClientSide) {
                if(!this.isVehicle()) {
                    player.startRiding(this);
                }
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(item);
    }
}
