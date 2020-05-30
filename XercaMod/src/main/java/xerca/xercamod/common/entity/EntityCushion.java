package xerca.xercamod.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import xerca.xercamod.common.block.BlockCushion;
import xerca.xercamod.common.item.ItemCushion;

import javax.annotation.Nullable;

public class EntityCushion extends Entity implements IEntityAdditionalSpawnData {
    private ItemCushion item;
    public BlockCushion block;
    private int cushionIndex;

    static BlockCushion[] blockCushions;
    static ItemCushion[] itemCushions;


    public EntityCushion(EntityType<? extends EntityCushion> type, World world) {
        super(type, world);
    }

    public EntityCushion(World worldIn) {
        super(Entities.CUSHION, worldIn);
        this.setNoGravity(false);
    }
    public EntityCushion(World worldIn, double x, double y, double z, ItemCushion item) {
        this(worldIn);
        this.setPosition(x, y, z);
        this.item = item;
        this.block = item.getBlock();
        this.cushionIndex = block.cushionIndex;
    }

    public EntityCushion(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(world);
    }

    @Override
    protected void registerData() {

    }

    @Override
    public void tick(){
        super.tick();
        move(MoverType.SELF, new Vec3d(0, -0.16, 0));
        setBoundingBox(new AxisAlignedBB(posX - 0.5, posY, posZ - 0.5, posX + 0.5, posY + getHeight(), posZ + 0.5));
    }


    @Override
    public boolean hitByEntity(Entity entityIn) {
        return entityIn instanceof PlayerEntity && this.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) entityIn), 0.0F);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.removed && !this.world.isRemote) {
                this.remove();
                this.markVelocityChanged();
                this.onBroken(source.getTrueSource());
            }

            return true;
        }
    }

    public void onBroken(@Nullable Entity brokenEntity) {
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof PlayerEntity) {
                PlayerEntity entityplayer = (PlayerEntity)brokenEntity;
                if (entityplayer.abilities.isCreativeMode) {
                    return;
                }
            }

            this.entityDropItem(item);
        }
    }
    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        cushionIndex = compound.getInt("cushion");
        this.block = blockCushions[cushionIndex];
        this.item = itemCushions[cushionIndex];
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("cushion", cushionIndex);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(cushionIndex);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer) {
        this.cushionIndex = buffer.readInt();
        this.block = blockCushions[cushionIndex];
        this.item = itemCushions[cushionIndex];
    }

    @Override
    public double getMountedYOffset() {
        return -0.125;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
    /**
     * Applies the given player interaction to this Entity.
     */
    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        if (!this.world.isRemote) {
            player.startRiding(this);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!this.world.isRemote) {
                if(!this.isBeingRidden()) {
                    player.startRiding(this);
                }
            }
            return true;
        }
    }
}
