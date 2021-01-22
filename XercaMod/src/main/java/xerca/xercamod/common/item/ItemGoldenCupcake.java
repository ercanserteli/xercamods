package xerca.xercamod.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.entity.EntityConfettiBall;

import java.util.Arrays;

import static xerca.xercamod.common.item.Foods.GOLDEN_CUPCAKE;

public class ItemGoldenCupcake extends Item {
    public ItemGoldenCupcake() {
        super(new Item.Properties().food(GOLDEN_CUPCAKE));
        setRegistryName("item_golden_cupcake");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entity) {
        if(!(entity instanceof PlayerEntity)){
            return stack;
        }
        PlayerEntity player = (PlayerEntity)entity;
        player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 300, 1));
        player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 200, 0));

        if (!worldIn.isRemote) {
            int n = worldIn.rand.nextInt(5);
            switch (n){
                case 0:
                    LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(worldIn);
                    lightningboltentity.setPositionAndUpdate(player.getPosX(), player.getPosY(), player.getPosZ());
                    worldIn.addEntity(lightningboltentity);

                    worldIn.createExplosion(null, player.getPosX(), player.getPosY(), player.getPosZ(), 1.1F, false, Explosion.Mode.BREAK);
                    EntityConfettiBall entityball = new EntityConfettiBall(worldIn, player);
                    entityball.func_234612_a_(player, 270, player.rotationYaw + 90, 0.0F, 1.0F, 1.0F);
                    worldIn.addEntity(entityball);
                    for(int i=0; i<8; i++){
                        entityball = new EntityConfettiBall(worldIn, player);
                        entityball.func_234612_a_(player, 300, 45*i, 0.0F, 1.0F, 1.0F);
                        worldIn.addEntity(entityball);
                    }

                    float multiplier = 0.5f;
                    float motionX = worldIn.rand.nextFloat() - 0.5f;
                    float motionY = worldIn.rand.nextFloat() - 0.5f;
                    float motionZ = worldIn.rand.nextFloat() - 0.5f;

                    ItemEntity newCupcake = new ItemEntity(worldIn,
                            player.getPosX() + motionX, player.getPosY()+1+motionY, player.getPosZ()+motionZ,
                            new ItemStack(Items.ITEM_GOLDEN_CUPCAKE, 2));

                    newCupcake.setMotion(motionX * multiplier, motionY * multiplier, motionZ * multiplier);

                    worldIn.addEntity(newCupcake);
                    break;
                case 1:
                case 2:
                    worldIn.playSound(null, player.getPosX(), player.getPosY() + 3, player.getPosZ(), SoundEvents.YAHOO, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.9F);

                    player.addVelocity(0, 2, 0);
                    player.velocityChanged = true;
                    player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 1200, 6));
                    break;
                case 3:
                case 4:
                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.SCARY, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.8F);

                    player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 200, 1));

                    ItemStack herobrineHead = new ItemStack(net.minecraft.item.Items.PLAYER_HEAD, 1);
                    herobrineHead.getOrCreateTag().put("SkullOwner", ItemScythe.getSkullNBT(Arrays.asList(1002043797,-372031054,-1422417350,-1998966556), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM2NWVkMjgyOWM4M2UxMTlhODBkZmIyMjIxNjQ0M2U4NzhlZjEwNjQ5YzRhMzU0Zjc0YmY0NWFkMDZiYzFhNyJ9fX0="));

                    Item[] instruments = {Items.ITEM_GAVEL, Items.ITEM_RAW_SAUSAGE, Items.ITEM_STONE_WARHAMMER, Items.ITEM_PROSECUTOR_BADGE};

                    Entity e1 = new SkeletonEntity(EntityType.SKELETON, worldIn);
                    e1.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(instruments[worldIn.rand.nextInt(4)]));
                    e1.setItemStackToSlot(EquipmentSlotType.HEAD, herobrineHead);
                    e1.setLocationAndAngles(player.getPosX() + (double) worldIn.rand.nextInt(3), player.getPosY() + (double) worldIn.rand.nextInt(5), player.getPosZ() + (double) worldIn.rand.nextInt(3), worldIn.rand.nextFloat() * 360.0F, 0.0F);

                    ItemStack playerHead = new ItemStack(net.minecraft.item.Items.PLAYER_HEAD, 1);
                    playerHead.getOrCreateTag().put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), player.getGameProfile()));

                    Entity e2 = new ZombieEntity(worldIn);
                    e2.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.ITEM_KNIFE));
                    e2.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.ITEM_KNIFE));
                    e2.setItemStackToSlot(EquipmentSlotType.HEAD, playerHead);
                    e2.setLocationAndAngles(player.getPosX() + (double) worldIn.rand.nextInt(3), player.getPosY() + (double) worldIn.rand.nextInt(5), player.getPosZ() + (double) worldIn.rand.nextInt(3), worldIn.rand.nextFloat() * 360.0F, 0.0F);

                    worldIn.addEntity(e1);
                    worldIn.addEntity(e2);
                    break;
            }
        }
        return super.onItemUseFinish(stack, worldIn, player);
    }

}
