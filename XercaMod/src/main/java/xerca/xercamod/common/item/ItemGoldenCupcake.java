package xerca.xercamod.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.entity.EntityConfettiBall;

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
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity player) {
        player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 300, 1));
        player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 200, 0));

        if (!worldIn.isRemote) {
            int n = worldIn.rand.nextInt(5);
            switch (n){
                case 0:
                    ((ServerWorld)worldIn).addLightningBolt(new LightningBoltEntity(worldIn, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), true));
                    worldIn.createExplosion(null, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), 1.1F, false, Explosion.Mode.BREAK);
                    EntityConfettiBall entityball = new EntityConfettiBall(worldIn, player);
                    entityball.shoot(player, 270, player.rotationYaw + 90, 0.0F, 1.0F, 1.0F);
                    worldIn.addEntity(entityball);
                    for(int i=0; i<8; i++){
                        entityball = new EntityConfettiBall(worldIn, player);
                        entityball.shoot(player, 300, 45*i, 0.0F, 1.0F, 1.0F);
                        worldIn.addEntity(entityball);
                    }

                    float multiplier = 0.5f;
                    float motionX = worldIn.rand.nextFloat() - 0.5f;
                    float motionY = worldIn.rand.nextFloat() - 0.5f;
                    float motionZ = worldIn.rand.nextFloat() - 0.5f;

                    ItemEntity newCupcake = new ItemEntity(worldIn,
                            player.getPosition().getX() + motionX, player.getPosition().getY()+1+motionY, player.getPosition().getZ()+motionZ,
                            new ItemStack(Items.ITEM_GOLDEN_CUPCAKE, 2));

                    newCupcake.setMotion(motionX * multiplier, motionY * multiplier, motionZ * multiplier);

                    worldIn.addEntity(newCupcake);
                    break;
                case 1:
                case 2:
                    worldIn.playSound(null, player.getPosition().getX(), player.getPosition().getY() + 3, player.getPosition().getZ(), SoundEvents.YAHOO, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.9F);

                    player.addVelocity(0, 2, 0);
                    player.velocityChanged = true;
                    player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 1200, 6));
                    break;
                case 3:
                case 4:
                    worldIn.playSound(null, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), SoundEvents.SCARY, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.8F);

                    player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 200, 1));

                    ItemStack head = new ItemStack(net.minecraft.item.Items.PLAYER_HEAD, 1);
                    head.getOrCreateTag().put("SkullOwner", new StringNBT("MHF_Herobrine"));

                    Item[] instruments = {Items.ITEM_GAVEL, Items.ITEM_RAW_SAUSAGE, Items.ITEM_STONE_WARHAMMER, Items.ITEM_PROSECUTOR_BADGE};
                    int i = worldIn.rand.nextInt(4);

                    Entity e1 = new SkeletonEntity(EntityType.SKELETON, worldIn);
                    e1.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(instruments[i]));
                    e1.setItemStackToSlot(EquipmentSlotType.HEAD, head);
                    e1.setLocationAndAngles(player.getPosition().getX() + (double) worldIn.rand.nextInt(3), player.getPosition().getY() + (double) worldIn.rand.nextInt(5), player.getPosition().getZ() + (double) worldIn.rand.nextInt(3), worldIn.rand.nextFloat() * 360.0F, 0.0F);

                    ItemStack playerHead = new ItemStack(net.minecraft.item.Items.PLAYER_HEAD, 1);
                    playerHead.getOrCreateTag().put("SkullOwner", new StringNBT(player.getName().getString()));

                    Entity e2 = new ZombieEntity(worldIn);
                    e2.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.ITEM_KNIFE));
                    e2.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.ITEM_KNIFE));
                    e2.setItemStackToSlot(EquipmentSlotType.HEAD, playerHead);
                    e2.setLocationAndAngles(player.getPosition().getX() + (double) worldIn.rand.nextInt(3), player.getPosition().getY() + (double) worldIn.rand.nextInt(5), player.getPosition().getZ() + (double) worldIn.rand.nextInt(3), worldIn.rand.nextFloat() * 360.0F, 0.0F);

                    worldIn.addEntity(e1);
                    worldIn.addEntity(e2);
                    break;
            }
        }
        return super.onItemUseFinish(stack, worldIn, player);
    }

}
