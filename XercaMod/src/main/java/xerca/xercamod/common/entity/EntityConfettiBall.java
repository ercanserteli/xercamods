package xerca.xercamod.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.item.Items;


public class EntityConfettiBall extends ProjectileItemEntity {
    public EntityConfettiBall(EntityType<? extends EntityConfettiBall> type, World world) {
        super(type, world);
    }

    public EntityConfettiBall(World worldIn, LivingEntity throwerIn) {
        super(Entities.CONFETTI_BALL, throwerIn, worldIn);
    }

    public EntityConfettiBall(World worldIn, double x, double y, double z) {
        super(Entities.CONFETTI_BALL, x, y, z, worldIn);
    }

    public EntityConfettiBall(World worldIn) {
        super(Entities.CONFETTI_BALL, worldIn);
    }

    public EntityConfettiBall(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        super(Entities.CONFETTI_BALL, world);
    }

    private void spawnConfetti(double x, double y, double z) {
        for (int j = 0; j < 12; ++j) {
            this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.ITEM_CONFETTI)), x, y, z, ((double) this.rand.nextFloat() - 0.5D) * 0.3D, ((double) this.rand.nextFloat()) * 0.5D, ((double) this.rand.nextFloat() - 0.5D) * 0.3D);
        }
    }

    private void spawnConfetti(Vec3d vec) {
        spawnConfetti(vec.x, vec.y, vec.z);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        spawnConfetti(result.getHitVec());
        if (!this.world.isRemote) {
            this.world.playSound(null, this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), SoundEvents.CRACK, SoundCategory.PLAYERS, 2.0f, this.rand.nextFloat() * 0.4F + 0.8F);
            this.remove();
        }
    }

    @Override
    protected void registerData() {

    }

    @Override
    public void tick() {
        super.tick();
        if(this.ticksExisted % 4 == 0){
            if(!this.world.isRemote){
                this.world.playSound(null, this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), SoundEvents.CRACK, SoundCategory.PLAYERS, 2.0f, this.rand.nextFloat() * 0.4F + 0.8F);
            }
            else{
                spawnConfetti(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ITEM_CONFETTI_BALL;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.ITEM_CONFETTI_BALL);
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}