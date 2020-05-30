package xerca.xercamod.common.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import xerca.xercamod.common.HookReturningEvent;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.item.Items;

public class EntityHook extends Entity implements IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> cau_ent = EntityDataManager.<Integer>createKey(EntityHook.class, DataSerializers.VARINT);
    private static final double DEFAULT_SPEED = 1.5D;
    private int xTile;
    private int yTile;
    private int zTile;
    private int age = 0;
    private boolean inGround;
    private PlayerEntity angler;
    private int ticksInAir;
    public Entity caughtEntity;
    public boolean isReturning;
    public boolean hasGrappling = false;
    public int turboLevel = 0;
    public boolean hasGentle = false;
    private double speed;
    private ItemStack rod = ItemStack.EMPTY;


    public EntityHook(EntityType<? extends EntityHook> type, World world) {
        super(type, world);
    }

    public EntityHook(World worldIn) {
        super(Entities.HOOK, worldIn);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.ignoreFrustumCheck = true;
        this.isReturning = false;
    }

    public EntityHook(World worldIn, PlayerEntity hooker, ItemStack rod, float pullAmount) {
        this(worldIn);

        this.isReturning = false;
        this.angler = hooker;
        if(rod.getItem() == Items.ITEM_GRAB_HOOK){
            this.rod = rod;
            this.rod.getOrCreateTag().putBoolean("cast", true);
            hasGrappling = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_GRAPPLING, this.rod) > 0;
            hasGentle = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_GENTLE_GRAB, this.rod) > 0;
            turboLevel = EnchantmentHelper.getEnchantmentLevel(Items.ENCHANTMENT_TURBO_GRAB, this.rod);
        }

        double speedMultiplier = (turboLevel * 0.25 + 1);
        this.speed = DEFAULT_SPEED * speedMultiplier * pullAmount;
        float pitch = this.angler.rotationPitch;
        float yaw = this.angler.rotationYaw;
        float f2 = MathHelper.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-pitch * ((float)Math.PI / 180F));
        double x = this.angler.posX;// - (double)f3 * 0.3D;
        double y = this.angler.posY + (double)this.angler.getEyeHeight();
        double z = this.angler.posZ;// - (double)f2 * 0.3D;
        this.setLocationAndAngles(x, y, z, yaw, pitch);
        Vec3d vec3d = new Vec3d((double)(-f3), -(f5 / f4), (double)(-f2));
        double length = vec3d.length();
        vec3d = vec3d.scale(speed/length);
        this.setMotion(vec3d);
        this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)MathHelper.sqrt(this.getDistanceSq(vec3d))) * (double)(180F / (float)Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    public EntityHook(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(world);
    }

    public PlayerEntity getAngler() {
        return angler;
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        if (cau_ent.equals(key)) {
            int i = this.getDataManager().get(cau_ent);

            if (i > 0 && this.caughtEntity != null) {
                this.caughtEntity = null;
            }
        }

        super.notifyDataManagerChange(key);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    private boolean checkCollision() {
        RayTraceResult raytraceresult = ProjectileHelper.rayTrace(this, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (p_213856_1_) -> !p_213856_1_.isSpectator() && (p_213856_1_.canBeCollidedWith() || p_213856_1_ instanceof ItemEntity) && (p_213856_1_ != this.angler || this.ticksInAir >= 5), RayTraceContext.BlockMode.COLLIDER, true);
        if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            if (raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
                Entity caught = ((EntityRayTraceResult) raytraceresult).getEntity();
                if(!(caught instanceof LivingEntity) || caught.equals(this.angler)){
                    return false;
                }
                // Hit an entity
                this.caughtEntity = caught;
                this.getDataManager().set(cau_ent, this.caughtEntity.getEntityId() + 1);
                this.caughtEntity = caught;

                if(!hasGentle){
                    world.playSound(null, this.getPosition(), SoundEvents.HOOK_IMPACT, SoundCategory.PLAYERS, 1.0f, world.rand.nextFloat() * 0.2F + 0.9F);

                    caughtEntity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.angler), 3);
                    if(!this.caughtEntity.isAlive()){
                        this.remove();
                        return true;
                    }
                }else{
                    world.playSound(null, this.getPosition(), SoundEvents.HOOK_IMPACT, SoundCategory.PLAYERS, 0.6f, world.rand.nextFloat() * 0.2F + 1.5f);
                }

                this.caughtEntity.noClip = true;
                this.caughtEntity.stopRiding();
                return true;
            } else if(raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
                this.inGround = true;
                if(hasGrappling){
                    this.setMotion(0, 0, 0);
                    this.angler.noClip = true;
                    this.angler.stopRiding();
                }
                return true;
            }
        }
        return false;
    }

    private void pullEntity() {
        if (this.caughtEntity.isAlive()) {
            Vec3d v1 = this.angler.getPositionVector();
            Vec3d v2 = this.caughtEntity.getPositionVector();
            Vec3d v = v1.subtract(v2);
            if (v.length() > 2) {
                v = v.normalize().scale(speed);
                this.caughtEntity.setMotion(v);
                this.caughtEntity.velocityChanged = true;
                this.caughtEntity.isAirBorne = true;
            } else {
                this.remove();
                return;
            }

            double height = (double) this.caughtEntity.getHeight() + 0.5d;
            this.posX = this.caughtEntity.posX;
            this.posY = this.caughtEntity.getBoundingBox().minY + height * 0.8D;
            this.posZ = this.caughtEntity.posZ;
            return;
        }
        this.caughtEntity = null;
    }

    private void pullUser() {
        Vec3d v1 = this.angler.getPositionVector();
        Vec3d v2 = this.getPositionVector();
        Vec3d v = v2.subtract(v1);
        if (v.length() > 2) {
            v = v.normalize().scale(speed);
            this.angler.setMotion(v);

            this.angler.noClip = true;
            this.angler.isAirBorne = true;
            this.angler.velocityChanged = true;
        } else {

            this.remove();
        }
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(cau_ent, 0);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.angler == null) {
                this.remove();
                return;
            }
            this.setFlag(6, this.isGlowing());
        }
        this.baseTick();
        age++;

        if (this.world.isRemote) {
            int i = this.getDataManager().get(cau_ent);

            if (i > 0 && this.caughtEntity == null) {
                this.caughtEntity = this.world.getEntityByID(i - 1);
                MinecraftForge.EVENT_BUS.post(new HookReturningEvent(this));
            }
        } else {
            ItemStack itemstack = this.angler.getHeldItemMainhand();

            if (age > 80 || !this.angler.isAlive() || itemstack.getItem() != Items.ITEM_GRAB_HOOK || this.getDistanceSq(this.angler) > 4096.0D) {
                this.remove();
                this.angler.fishingBobber = null;
                return;
            }
        }

        if (this.caughtEntity != null) {
            pullEntity();
            return;
        }
        if (this.inGround) {
            if(hasGrappling){
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
        if (!this.world.isRemote) {
            boolean caughtSomething = checkCollision();
            if(caughtSomething){
                return;
            }

            if (this.isReturning) {
                Vec3d target = this.angler.getPositionVector().add(0, this.angler.getEyeHeight(), 0);
                Vec3d v = target.subtract(this.getPositionVector());
                if (v.length() < 3D) {
                    this.remove();
                    return;
                }
                v = v.normalize().scale(speed).subtract(0, 0.1, 0);
                this.setMotion(v);
            }
        }

        this.move(MoverType.SELF, this.getMotion());
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    private void setReturning() {
        this.isReturning = true;
        MinecraftForge.EVENT_BUS.post(new HookReturningEvent(this));
    }

    @Override
    public void writeAdditional(CompoundNBT tagCompound) {
        tagCompound.putInt("xTile", this.xTile);
        tagCompound.putInt("yTile", this.yTile);
        tagCompound.putInt("zTile", this.zTile);
        tagCompound.putByte("inGround", (byte) (this.inGround ? 1 : 0));
    }

    @Override
    public void readAdditional(CompoundNBT tagCompund) {
        this.xTile = tagCompund.getInt("xTile");
        this.yTile = tagCompund.getInt("yTile");
        this.zTile = tagCompund.getInt("zTile");
        this.inGround = tagCompund.getByte("inGround") == 1;
    }

    /**
     * Will get destroyed next tick.
     */
    public void remove() {
        super.remove();
        if (this.caughtEntity != null) {
            this.caughtEntity.noClip = false;
        }
        if (this.angler != null) {
            this.angler.fishingBobber = null;
            this.angler.noClip = false;
        }
        if(this.rod.getItem() == Items.ITEM_GRAB_HOOK){
            this.rod.getOrCreateTag().putBoolean("cast", false);
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(angler != null ? angler.getEntityId() : -1);
        buffer.writeDouble(speed);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        int id = additionalData.readInt();
        Entity ent = world.getEntityByID(id);
        if (ent instanceof PlayerEntity) {
            angler = (PlayerEntity) ent;
        }
        this.speed = additionalData.readDouble();
    }
}