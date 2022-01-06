package xerca.xercamod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import xerca.xercamod.common.HookReturningEvent;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.item.Items;

public class EntityHook extends Entity implements IEntityAdditionalSpawnData {
    private static final EntityDataAccessor<Integer> cau_ent = SynchedEntityData.<Integer>defineId(EntityHook.class, EntityDataSerializers.INT);
    private static final double DEFAULT_SPEED = 1.5D;
    private int xTile;
    private int yTile;
    private int zTile;
    private int age = 0;
    private boolean inGround;
    private Player angler;
    private int ticksInAir;
    public Entity caughtEntity;
    public boolean isReturning;
    public boolean hasGrappling = false;
    public int turboLevel = 0;
    public boolean hasGentle = false;
    private double speed;
    private ItemStack rod = ItemStack.EMPTY;


    public EntityHook(EntityType<? extends EntityHook> type, Level world) {
        super(type, world);
    }

    public EntityHook(Level worldIn) {
        super(Entities.HOOK, worldIn);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.noCulling = true;
        this.isReturning = false;
    }

    public EntityHook(Level worldIn, Player hooker, ItemStack rod, float pullAmount) {
        this(worldIn);

        this.isReturning = false;
        this.angler = hooker;
        if(rod.getItem() == Items.ITEM_GRAB_HOOK){
            this.rod = rod;
            this.rod.getOrCreateTag().putBoolean("cast", true);
            hasGrappling = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_GRAPPLING, this.rod) > 0;
            hasGentle = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_GENTLE_GRAB, this.rod) > 0;
            turboLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_TURBO_GRAB, this.rod);
        }

        double speedMultiplier = (turboLevel * 0.25 + 1);
        this.speed = DEFAULT_SPEED * speedMultiplier * pullAmount;
        float pitch = this.angler.getXRot();
        float yaw = this.angler.getYRot();
        float f2 = Mth.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-pitch * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-pitch * ((float)Math.PI / 180F));
        double x = this.angler.getX();// - (double)f3 * 0.3D;
        double y = this.angler.getY() + (double)this.angler.getEyeHeight();
        double z = this.angler.getZ();// - (double)f2 * 0.3D;
        this.moveTo(x, y, z, yaw, pitch);
        Vec3 vec3d = new Vec3((double)(-f3), -(f5 / f4), (double)(-f2));
        double length = vec3d.length();
        vec3d = vec3d.scale(speed/length);
        this.setDeltaMovement(vec3d);
        this.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3d.y, Mth.sqrt((float)this.distanceToSqr(vec3d))) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public EntityHook(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(world);
    }

    public Player getAngler() {
        return angler;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (cau_ent.equals(key)) {
            int i = this.getEntityData().get(cau_ent);

            if (i > 0 && this.caughtEntity != null) {
                this.caughtEntity = null;
            }
        }

        super.onSyncedDataUpdated(key);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;

        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    private boolean checkCollision() {
        HitResult raytraceresult = ProjectileUtil.getHitResult(this, (entity) -> !entity.isSpectator() && (entity.isPickable() || entity instanceof ItemEntity) && (entity != this.angler || this.ticksInAir >= 5));
        if (raytraceresult.getType() != HitResult.Type.MISS) {
            if (raytraceresult.getType() == HitResult.Type.ENTITY) {
                Entity caught = ((EntityHitResult) raytraceresult).getEntity();
                if(!(caught instanceof LivingEntity) || caught.equals(this.angler)){
                    return false;
                }
                // Hit an entity
                this.caughtEntity = caught;
                this.getEntityData().set(cau_ent, this.caughtEntity.getId() + 1);
                this.caughtEntity = caught;

                if(!hasGentle){
                    level.playSound(null, getX(), getY(), getZ(), SoundEvents.HOOK_IMPACT, SoundSource.PLAYERS, 1.0f, level.random.nextFloat() * 0.2F + 0.9F);

                    caughtEntity.hurt(DamageSource.thrown(this, this.angler), 3);
                    if(!this.caughtEntity.isAlive()){
                        this.remove();
                        return true;
                    }
                }else{
                    level.playSound(null, getX(), getY(), getZ(), SoundEvents.HOOK_IMPACT, SoundSource.PLAYERS, 0.6f, level.random.nextFloat() * 0.2F + 1.5f);
                }

                this.caughtEntity.noPhysics = true;
                this.caughtEntity.stopRiding();
                return true;
            } else if(raytraceresult.getType() == HitResult.Type.BLOCK) {
                this.inGround = true;
                if(hasGrappling){
                    this.setDeltaMovement(0, 0, 0);
                    this.angler.noPhysics = true;
                    this.angler.stopRiding();
                }
                return true;
            }
        }
        return false;
    }

    private void pullEntity() {
        if (this.caughtEntity.isAlive()) {
            Vec3 v1 = this.angler.position();
            Vec3 v2 = this.caughtEntity.position();
            Vec3 v = v1.subtract(v2);
            if (v.length() > 2) {
                v = v.normalize().scale(speed);
                this.caughtEntity.setDeltaMovement(v);
                this.caughtEntity.hurtMarked = true;
                this.caughtEntity.hasImpulse = true;
            } else {
                this.remove();
                return;
            }

            double height = (double) this.caughtEntity.getBbHeight() + 0.5d;
            this.setPos(caughtEntity.getX(), caughtEntity.getBoundingBox().minY + height * 0.8D, caughtEntity.getZ());
            return;
        }
        this.caughtEntity = null;
    }

    private void pullUser() {
        Vec3 v1 = this.angler.position();
        Vec3 v2 = this.position();
        Vec3 v = v2.subtract(v1);
        if (v.length() > 2) {
            v = v.normalize().scale(speed);
            this.angler.setDeltaMovement(v);

            this.angler.noPhysics = true;
            this.angler.hasImpulse = true;
            this.angler.hurtMarked = true;
        } else {

            this.remove();
        }
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(cau_ent, 0);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.angler == null) {
                this.remove();
                return;
            }
            this.setSharedFlag(6, this.isCurrentlyGlowing());
        }
        this.baseTick();
        age++;

        if (this.level.isClientSide) {
            int i = this.getEntityData().get(cau_ent);

            if (i > 0 && this.caughtEntity == null) {
                this.caughtEntity = this.level.getEntity(i - 1);
                MinecraftForge.EVENT_BUS.post(new HookReturningEvent(this));
            }
        } else {
            ItemStack itemstack = this.angler.getMainHandItem();

            if (age > 80 || !this.angler.isAlive() || itemstack.getItem() != Items.ITEM_GRAB_HOOK || this.distanceToSqr(this.angler) > 4096.0D) {
                this.remove();
                this.angler.fishing = null;
                return;
            }
        }

        if (this.caughtEntity != null && this.angler != null) {
            pullEntity();
            return;
        }
        if (this.inGround) {
            if(hasGrappling && this.angler != null){
                pullUser();
            }else{
                this.remove();
            }
            return;
        }

        // In the air
        ++this.ticksInAir;
        if (this.ticksInAir == 20) {
            setReturning();
        }
        if (!this.level.isClientSide) {
            boolean caughtSomething = checkCollision();
            if(caughtSomething){
                return;
            }

            if (this.isReturning) {
                Vec3 target = this.angler.position().add(0, this.angler.getEyeHeight(), 0);
                Vec3 v = target.subtract(this.position());
                if (v.length() < 3D) {
                    this.remove();
                    return;
                }
                v = v.normalize().scale(speed).subtract(0, 0.1, 0);
                this.setDeltaMovement(v);
            }
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setPos(this.getX(), this.getY(), this.getZ());
    }

    private void setReturning() {
        this.isReturning = true;
        MinecraftForge.EVENT_BUS.post(new HookReturningEvent(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tagCompound) {
        tagCompound.putInt("xTile", this.xTile);
        tagCompound.putInt("yTile", this.yTile);
        tagCompound.putInt("zTile", this.zTile);
        tagCompound.putByte("inGround", (byte) (this.inGround ? 1 : 0));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tagCompund) {
        this.xTile = tagCompund.getInt("xTile");
        this.yTile = tagCompund.getInt("yTile");
        this.zTile = tagCompund.getInt("zTile");
        this.inGround = tagCompund.getByte("inGround") == 1;
    }

    /**
     * Will get destroyed next tick.
     */
    public void remove() {
        super.remove(RemovalReason.DISCARDED);
        if (this.caughtEntity != null) {
            this.caughtEntity.noPhysics = false;
        }
        if (this.angler != null) {
            this.angler.fishing = null;
            this.angler.noPhysics = false;
        }
        if(this.rod.getItem() == Items.ITEM_GRAB_HOOK){
            this.rod.getOrCreateTag().putBoolean("cast", false);
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(angler != null ? angler.getId() : -1);
        buffer.writeDouble(speed);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        int id = additionalData.readInt();
        Entity ent = level.getEntity(id);
        if (ent instanceof Player) {
            angler = (Player) ent;
        }
        this.speed = additionalData.readDouble();
    }
}