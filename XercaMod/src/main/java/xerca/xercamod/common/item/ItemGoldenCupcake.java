package xerca.xercamod.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
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
import net.minecraft.potion.Effect;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static xerca.xercamod.common.item.Foods.GOLDEN_CUPCAKE;

public class ItemGoldenCupcake extends Item {
    private EntityPredicate yahooPredicate = (new EntityPredicate()).setDistance(16.0D);
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

        if (!worldIn.isRemote) {
            int n = worldIn.rand.nextInt(5);
            switch (n){
                case 0:
                    player.heal(10);
                    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 300, 2));

                    LightningBoltEntity lightningBoltEntity = EntityType.LIGHTNING_BOLT.create(worldIn);
                    lightningBoltEntity.setPositionAndUpdate(player.getPosX(), player.getPosY(), player.getPosZ());
                    lightningBoltEntity.setEffectOnly(true);
                    worldIn.addEntity(lightningBoltEntity);

                    worldIn.createExplosion(null, player.getPosX(), player.getPosY(), player.getPosZ(), 1.1F, false, Explosion.Mode.BREAK);
                    EntityConfettiBall entityBall = new EntityConfettiBall(worldIn, player);
                    entityBall.func_234612_a_(player, 270, player.rotationYaw + 90, 0.0F, 1.0F, 1.0F);
                    worldIn.addEntity(entityBall);
                    for(int i=0; i<8; i++){
                        entityBall = new EntityConfettiBall(worldIn, player);
                        entityBall.func_234612_a_(player, 300, 45*i, 0.0F, 1.0F, 1.0F);
                        worldIn.addEntity(entityBall);
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
                    worldIn.playSound(null, player.getPosX(), player.getPosY() + 1, player.getPosZ(), SoundEvents.HOLY, SoundCategory.MASTER, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.9F);

                    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 500, 3));
                    player.addPotionEffect(new EffectInstance(Effects.GLOWING, 500, 3));
                    player.addPotionEffect(new EffectInstance(Effects.HEALTH_BOOST, 500, 3));
                    player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 300, 3));
                    player.addPotionEffect(new EffectInstance(Effects.SATURATION, 300, 3));
                    player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 300, 3));
                    player.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 300, 3));
                    player.addPotionEffect(new EffectInstance(Effects.LEVITATION, 150, 3));
                    break;
                case 2:
                    List<PlayerEntity> targets = player.world.getTargettablePlayersWithinAABB(yahooPredicate, player, player.getBoundingBox().grow(16.0D, 8.0D, 16.0D));
                    targets.add(player);
                    for(PlayerEntity p : targets){
                        worldIn.playSound(null, p.getPosX(), p.getPosY() + 3, p.getPosZ(), SoundEvents.YAHOO, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.9F);

                        p.addVelocity(0, 2, 0);
                        p.velocityChanged = true;
                        int time = 1100 + random.nextInt(200);
                        p.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, time, 6));
                        p.addPotionEffect(new EffectInstance(Effects.SPEED, time, 1));
                    }
                    break;
                case 3:
                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.SPARKLES, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.4F + 0.8F);

                    List<Effect> effects = Arrays.asList(Effects.HEALTH_BOOST, Effects.REGENERATION, Effects.SATURATION,
                            Effects.SLOW_FALLING, Effects.NAUSEA, Effects.SPEED, Effects.HUNGER, Effects.WEAKNESS,
                            Effects.SLOWNESS, Effects.HASTE, Effects.STRENGTH);
                    Collections.shuffle(effects);
                    for(int i=0; i<3; i++){
                        player.addPotionEffect(new EffectInstance(effects.get(i), 200 + random.nextInt(400), 2 + random.nextInt(5)));
                    }
                    break;
                case 4:
                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.SCARY, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.9F);

                    player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 200, 0));
                    player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 200, 2));
                    player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 200, 1));

                    ItemStack herobrineHead = new ItemStack(net.minecraft.item.Items.PLAYER_HEAD, 1);
                    herobrineHead.getOrCreateTag().put("SkullOwner", ItemScythe.getSkullNBT(Arrays.asList(1002043797,-372031054,-1422417350,-1998966556), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM2NWVkMjgyOWM4M2UxMTlhODBkZmIyMjIxNjQ0M2U4NzhlZjEwNjQ5YzRhMzU0Zjc0YmY0NWFkMDZiYzFhNyJ9fX0="));

                    Item[] weapons = {Items.ITEM_GAVEL, Items.ITEM_RAW_SAUSAGE, Items.ITEM_STONE_WARHAMMER, Items.STONE_SCYTHE, Items.ITEM_PROSECUTOR_BADGE};

                    Entity e1 = new SkeletonEntity(EntityType.SKELETON, worldIn);
                    e1.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(weapons[worldIn.rand.nextInt(weapons.length)]));
                    e1.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(weapons[worldIn.rand.nextInt(weapons.length)]));
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
