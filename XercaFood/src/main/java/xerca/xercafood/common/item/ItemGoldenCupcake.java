package xerca.xercafood.common.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.SoundEvents;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static xerca.xercafood.common.item.Foods.GOLDEN_CUPCAKE;

public class ItemGoldenCupcake extends Item {
    private final TargetingConditions yahooPredicate = TargetingConditions.forNonCombat().range(16.0D);

    public ItemGoldenCupcake() {
        super(new Item.Properties().food(GOLDEN_CUPCAKE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return stack;
        }
        Player player = (Player) entity;

        if (!worldIn.isClientSide) {
            int n = worldIn.random.nextInt(5);
            switch (n) {
                case 0:
                    player.heal(10);
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 2));

                    LightningBolt lightningBoltEntity = EntityType.LIGHTNING_BOLT.create(worldIn);
                    lightningBoltEntity.teleportTo(player.getX(), player.getY(), player.getZ());
                    lightningBoltEntity.setVisualOnly(true);
                    worldIn.addFreshEntity(lightningBoltEntity);

                    worldIn.explode(null, player.getX(), player.getY(), player.getZ(), 1.1F, false, Level.ExplosionInteraction.TNT);

                    float multiplier = 0.5f;
                    float motionX = worldIn.random.nextFloat() - 0.5f;
                    float motionY = worldIn.random.nextFloat() - 0.5f;
                    float motionZ = worldIn.random.nextFloat() - 0.5f;

                    ItemEntity newCupcake = new ItemEntity(worldIn,
                            player.getX() + motionX, player.getY() + 1 + motionY, player.getZ() + motionZ,
                            new ItemStack(Items.ITEM_GOLDEN_CUPCAKE, 2));

                    newCupcake.setDeltaMovement(motionX * multiplier, motionY * multiplier, motionZ * multiplier);

                    worldIn.addFreshEntity(newCupcake);
                    break;
                case 1:
                    worldIn.playSound(null, player.getX(), player.getY() + 1, player.getZ(), SoundEvents.HOLY, SoundSource.MASTER, 1.0f, worldIn.random.nextFloat() * 0.2F + 0.9F);

                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 500, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 500, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 300, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 300, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 150, 3));
                    break;
                case 2:
                    List<Player> targets = player.level().getNearbyPlayers(yahooPredicate, player, player.getBoundingBox().inflate(16.0D, 8.0D, 16.0D));
                    targets.add(player);
                    for (Player p : targets) {
                        worldIn.playSound(null, p.getX(), p.getY() + 3, p.getZ(), SoundEvents.YAHOO, SoundSource.PLAYERS, 1.0f, worldIn.random.nextFloat() * 0.2F + 0.9F);

                        p.push(0, 2, 0);
                        p.hurtMarked = true;
                        int time = 1100 + worldIn.random.nextInt(200);
                        p.addEffect(new MobEffectInstance(MobEffects.JUMP, time, 6));
                        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, time, 1));
                    }
                    break;
                case 3:
                    worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPARKLES, SoundSource.PLAYERS, 1.0f, worldIn.random.nextFloat() * 0.4F + 0.8F);

                    List<Holder<net.minecraft.world.effect.MobEffect>> effects = new ArrayList<>(Arrays.asList(MobEffects.HEALTH_BOOST, MobEffects.REGENERATION, MobEffects.SATURATION,
                            MobEffects.SLOW_FALLING, MobEffects.CONFUSION, MobEffects.MOVEMENT_SPEED, MobEffects.HUNGER, MobEffects.WEAKNESS,
                            MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SPEED, MobEffects.DAMAGE_BOOST));
                    Collections.shuffle(effects);
                    for (int i = 0; i < 3; i++) {
                        player.addEffect(new MobEffectInstance(effects.get(i), 200 + worldIn.random.nextInt(400), 2 + worldIn.random.nextInt(5)));
                    }
                    break;
                case 4:
                    worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SCARY, SoundSource.PLAYERS, 1.0f, worldIn.random.nextFloat() * 0.2F + 0.9F);

                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1));

                    Item[] weapons = {Items.ITEM_RAW_SAUSAGE, Items.ITEM_KNIFE, Items.ITEM_HOT_TEAPOT_1, Items.ITEM_ROTTEN_BURGER};

                    Skeleton skeleton = EntityType.SKELETON.create(worldIn);
                    Zombie zombie = EntityType.ZOMBIE.create(worldIn);
                    if (skeleton != null) {
                        skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapons[worldIn.random.nextInt(weapons.length)]));
                        skeleton.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(weapons[worldIn.random.nextInt(weapons.length)]));
                        skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(net.minecraft.world.item.Items.WITHER_SKELETON_SKULL));
                        skeleton.moveTo(player.getX() + (double) worldIn.random.nextInt(3), player.getY() + (double) worldIn.random.nextInt(5), player.getZ() + (double) worldIn.random.nextInt(3), worldIn.random.nextFloat() * 360.0F, 0.0F);
                        worldIn.addFreshEntity(skeleton);
                    }
                    if (zombie != null) {
                        zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_KNIFE));
                        zombie.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.ITEM_KNIFE));
                        zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD));
                        zombie.moveTo(player.getX() + (double) worldIn.random.nextInt(3), player.getY() + (double) worldIn.random.nextInt(5), player.getZ() + (double) worldIn.random.nextInt(3), worldIn.random.nextFloat() * 360.0F, 0.0F);
                        worldIn.addFreshEntity(zombie);
                    }
                    break;
            }
        }
        return super.finishUsingItem(stack, worldIn, player);
    }

}
