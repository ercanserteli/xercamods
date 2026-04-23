package xerca.xercafood.common.item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.KnifeCompat;
import xerca.xercafood.common.SoundEvents;

import java.util.ArrayList;
import java.util.Arrays;
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
    @SuppressFBWarnings(value = "SF", justification = "n is bounded by nextInt(5); all values are covered.")
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return stack;
        }

        if (!worldIn.isClientSide) {
            int n = worldIn.random.nextInt(5);
            switch (n) {
                case 0 -> applyLuckyStorm(worldIn, player);
                case 1 -> applyHolyBuffs(worldIn, player);
                case 2 -> applyYahooJump(worldIn, player);
                case 3 -> applySparkleRoulette(worldIn, player);
                case 4 -> applyScarySummon(worldIn, player);
                default -> {
                    // nextInt(5) constrains n to 0..4.
                }
            }
        }
        return super.finishUsingItem(stack, worldIn, player);
    }

    private void applyLuckyStorm(Level world, Player player) {
        player.heal(10);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 2));

        LightningBolt lightningBoltEntity = EntityType.LIGHTNING_BOLT.create(world);
        if (lightningBoltEntity != null) {
            lightningBoltEntity.teleportTo(player.getX(), player.getY(), player.getZ());
            lightningBoltEntity.setVisualOnly(true);
            world.addFreshEntity(lightningBoltEntity);
        }

        world.explode(null, player.getX(), player.getY(), player.getZ(), 1.1F, false, Level.ExplosionInteraction.TNT);
        spawnBonusCupcakes(world, player);
    }

    private void spawnBonusCupcakes(Level world, Player player) {
        float multiplier = 0.5f;
        float motionX = world.random.nextFloat() - 0.5f;
        float motionY = world.random.nextFloat() - 0.5f;
        float motionZ = world.random.nextFloat() - 0.5f;

        ItemEntity newCupcake = new ItemEntity(
                world,
                player.getX() + motionX,
                player.getY() + 1 + motionY,
                player.getZ() + motionZ,
                new ItemStack(Items.GOLDEN_CUPCAKE, 2)
        );
        newCupcake.setDeltaMovement(motionX * multiplier, motionY * multiplier, motionZ * multiplier);
        world.addFreshEntity(newCupcake);
    }

    private void applyHolyBuffs(Level world, Player player) {
        world.playSound(null, player.getX(), player.getY() + 1, player.getZ(), SoundEvents.HOLY, SoundSource.MASTER, 1.0f, world.random.nextFloat() * 0.2F + 0.9F);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 3));
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 500, 3));
        player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 500, 3));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 3));
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 300, 3));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 3));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 300, 3));
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 150, 3));
    }

    private void applyYahooJump(Level world, Player player) {
        List<Player> targets = player.level().getNearbyPlayers(yahooPredicate, player, player.getBoundingBox().inflate(16.0D, 8.0D, 16.0D));
        targets.add(player);
        for (Player target : targets) {
            world.playSound(null, target.getX(), target.getY() + 3, target.getZ(), SoundEvents.YAHOO, SoundSource.PLAYERS, 1.0f, world.random.nextFloat() * 0.2F + 0.9F);
            target.push(0, 2, 0);
            target.hurtMarked = true;
            int time = 1100 + world.random.nextInt(200);
            target.addEffect(new MobEffectInstance(MobEffects.JUMP, time, 6));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, time, 1));
        }
    }

    private void applySparkleRoulette(Level world, Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPARKLES, SoundSource.PLAYERS, 1.0f, world.random.nextFloat() * 0.4F + 0.8F);
        List<Holder<net.minecraft.world.effect.MobEffect>> effects = new ArrayList<>(Arrays.asList(
                MobEffects.HEALTH_BOOST, MobEffects.REGENERATION, MobEffects.SATURATION, MobEffects.SLOW_FALLING,
                MobEffects.CONFUSION, MobEffects.MOVEMENT_SPEED, MobEffects.HUNGER, MobEffects.WEAKNESS,
                MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SPEED, MobEffects.DAMAGE_BOOST
        ));
        Collections.shuffle(effects);
        for (int i = 0; i < 3; i++) {
            player.addEffect(new MobEffectInstance(effects.get(i), 200 + world.random.nextInt(400), 2 + world.random.nextInt(5)));
        }
    }

    private void applyScarySummon(Level world, Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SCARY, SoundSource.PLAYERS, 1.0f, world.random.nextFloat() * 0.2F + 0.9F);
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2));
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1));

        Item[] weapons = {
                Items.RAW_SAUSAGE, Items.COOKED_SAUSAGE, KnifeCompat.getKnifeItem(), Items.HOT_TEAPOT_1, Items.ROTTEN_BURGER
        };

        Skeleton skeleton = EntityType.SKELETON.create(world);
        if (skeleton != null) {
            equipMob(world, player, weapons, skeleton, new ItemStack(net.minecraft.world.item.Items.WITHER_SKELETON_SKULL));
            world.addFreshEntity(skeleton);
        }

        Zombie zombie = EntityType.ZOMBIE.create(world);
        if (zombie != null) {
            equipMob(world, player, weapons, zombie, new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD));
            world.addFreshEntity(zombie);
        }
    }

    private void equipMob(Level world, Player player, Item[] weapons, LivingEntity mob, ItemStack head) {
        mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapons[world.random.nextInt(weapons.length)]));
        mob.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(weapons[world.random.nextInt(weapons.length)]));
        mob.setItemSlot(EquipmentSlot.HEAD, head);
        mob.moveTo(
                player.getX() + world.random.nextInt(3),
                player.getY() + world.random.nextInt(5),
                player.getZ() + world.random.nextInt(3),
                world.random.nextFloat() * 360.0F,
                0.0F
        );
    }
}
