package xerca.xercamod.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.item.Items;

public class EntityTomato extends ProjectileItemEntity {

    public EntityTomato(EntityType<? extends EntityTomato> type, World world) {
        super(type, world);
    }

    public EntityTomato(World worldIn, LivingEntity throwerIn) {
        super(Entities.TOMATO, throwerIn, worldIn);
    }

    public EntityTomato(World worldIn, double x, double y, double z) {
        super(Entities.TOMATO, x, y, z, worldIn);
    }

    public EntityTomato(World worldIn) {
        super(Entities.TOMATO, worldIn);
    }

    public EntityTomato(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(world);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) result;
            entityRayTraceResult.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 1f);
        }

        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte)3);
            world.playSound(null, result.getHitVec().x, result.getHitVec().y, result.getHitVec().z, SoundEvents.TOMATO_SPLASH, SoundCategory.PLAYERS, 1.0f, this.rand.nextFloat() * 0.2F + 0.9F);
            this.remove();
        }
    }

    @Override
    protected void registerData() {

    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 3)
        {
            for (int j = 0; j < 8; ++j) {
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.ITEM_TOMATO)), this.getPosX(), this.getPosY(), this.getPosZ(), ((double) this.rand.nextFloat() - 0.5D) * 0.28D, ((double) this.rand.nextFloat() - 0.3D) * 0.28D, ((double) this.rand.nextFloat() - 0.5D) * 0.28D);
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ITEM_TOMATO;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.ITEM_TOMATO);
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
