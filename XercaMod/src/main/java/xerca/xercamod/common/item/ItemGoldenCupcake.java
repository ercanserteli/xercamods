package xerca.xercamod.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.entity.EntityConfettiBall;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static xerca.xercamod.common.item.Foods.GOLDEN_CUPCAKE;

public class ItemGoldenCupcake extends Item {
    private final TargetingConditions yahooPredicate = TargetingConditions.forNonCombat().range(16.0D);
    public ItemGoldenCupcake() {
        super(new Item.Properties().food(GOLDEN_CUPCAKE));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entity) {
        if(!(entity instanceof Player player)){
            return stack;
        }

        if (!worldIn.isClientSide) {
            int n = worldIn.random.nextInt(5);
            switch (n) {
                case 0 -> {
                    player.heal(10);
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 2));
                    LightningBolt lightningBoltEntity = EntityType.LIGHTNING_BOLT.create(worldIn);
                    if (lightningBoltEntity != null) {
                        lightningBoltEntity.teleportTo(player.getX(), player.getY(), player.getZ());
                        lightningBoltEntity.setVisualOnly(true);
                        worldIn.addFreshEntity(lightningBoltEntity);
                    }
                    worldIn.explode(null, player.getX(), player.getY(), player.getZ(), 1.1F, false, Level.ExplosionInteraction.TNT);
                    EntityConfettiBall entityBall = new EntityConfettiBall(worldIn, player);
                    entityBall.shootFromRotation(player, 270, player.getYRot() + 90, 0.0F, 1.0F, 1.0F);
                    worldIn.addFreshEntity(entityBall);
                    for (int i = 0; i < 8; i++) {
                        entityBall = new EntityConfettiBall(worldIn, player);
                        entityBall.shootFromRotation(player, 300, 45 * i, 0.0F, 1.0F, 1.0F);
                        worldIn.addFreshEntity(entityBall);
                    }
                    float multiplier = 0.5f;
                    float motionX = worldIn.random.nextFloat() - 0.5f;
                    float motionY = worldIn.random.nextFloat() - 0.5f;
                    float motionZ = worldIn.random.nextFloat() - 0.5f;
                    ItemEntity newCupcake = new ItemEntity(worldIn,
                            player.getX() + motionX, player.getY() + 1 + motionY, player.getZ() + motionZ,
                            new ItemStack(Items.ITEM_GOLDEN_CUPCAKE.get(), 2));
                    newCupcake.setDeltaMovement(motionX * multiplier, motionY * multiplier, motionZ * multiplier);
                    worldIn.addFreshEntity(newCupcake);
                }
                case 1 -> {
                    worldIn.playSound(null, player.getX(), player.getY() + 1, player.getZ(), SoundEvents.HOLY.get(), SoundSource.MASTER, 1.0f, worldIn.random.nextFloat() * 0.2F + 0.9F);
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 500, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 500, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 300, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 300, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 150, 3));
                }
                case 2 -> {
                    List<Player> targets = player.level().getNearbyPlayers(yahooPredicate, player, player.getBoundingBox().inflate(16.0D, 8.0D, 16.0D));
                    targets.add(player);
                    for (Player p : targets) {
                        worldIn.playSound(null, p.getX(), p.getY() + 3, p.getZ(), SoundEvents.YAHOO.get(), SoundSource.PLAYERS, 1.0f, worldIn.random.nextFloat() * 0.2F + 0.9F);

                        p.push(0, 2, 0);
                        p.hurtMarked = true;
                        int time = 1100 + worldIn.random.nextInt(200);
                        p.addEffect(new MobEffectInstance(MobEffects.JUMP, time, 6));
                        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, time, 1));
                    }
                }
                case 3 -> {
                    worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPARKLES.get(), SoundSource.PLAYERS, 1.0f, worldIn.random.nextFloat() * 0.4F + 0.8F);
                    List<MobEffect> effects = Arrays.asList(MobEffects.HEALTH_BOOST, MobEffects.REGENERATION, MobEffects.SATURATION,
                            MobEffects.SLOW_FALLING, MobEffects.CONFUSION, MobEffects.MOVEMENT_SPEED, MobEffects.HUNGER, MobEffects.WEAKNESS,
                            MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SPEED, MobEffects.DAMAGE_BOOST);
                    Collections.shuffle(effects);
                    for (int i = 0; i < 3; i++) {
                        player.addEffect(new MobEffectInstance(effects.get(i), 200 + worldIn.random.nextInt(400), 2 + worldIn.random.nextInt(5)));
                    }
                }
                case 4 -> {
                    worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SCARY.get(), SoundSource.PLAYERS, 1.0f, worldIn.random.nextFloat() * 0.2F + 0.9F);
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1));
                    ItemStack herobrineHead = new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD, 1);
                    herobrineHead.getOrCreateTag().put("SkullOwner", ItemScythe.getSkullNBT(Arrays.asList(1002043797, -372031054, -1422417350, -1998966556), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM2NWVkMjgyOWM4M2UxMTlhODBkZmIyMjIxNjQ0M2U4NzhlZjEwNjQ5YzRhMzU0Zjc0YmY0NWFkMDZiYzFhNyJ9fX0="));
                    Item[] weapons = {Items.ITEM_GAVEL.get(), Items.ITEM_RAW_SAUSAGE.get(), Items.ITEM_STONE_WARHAMMER.get(), Items.STONE_SCYTHE.get(), Items.ITEM_PROSECUTOR_BADGE.get()};
                    Entity e1 = new Skeleton(EntityType.SKELETON, worldIn);
                    e1.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapons[worldIn.random.nextInt(weapons.length)]));
                    e1.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(weapons[worldIn.random.nextInt(weapons.length)]));
                    e1.setItemSlot(EquipmentSlot.HEAD, herobrineHead);
                    e1.moveTo(player.getX() + (double) worldIn.random.nextInt(3), player.getY() + (double) worldIn.random.nextInt(5), player.getZ() + (double) worldIn.random.nextInt(3), worldIn.random.nextFloat() * 360.0F, 0.0F);
                    ItemStack playerHead = new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD, 1);
                    playerHead.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), player.getGameProfile()));
                    Entity e2 = new Zombie(worldIn);
                    e2.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_KNIFE.get()));
                    e2.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.ITEM_KNIFE.get()));
                    e2.setItemSlot(EquipmentSlot.HEAD, playerHead);
                    e2.moveTo(player.getX() + (double) worldIn.random.nextInt(3), player.getY() + (double) worldIn.random.nextInt(5), player.getZ() + (double) worldIn.random.nextInt(3), worldIn.random.nextFloat() * 360.0F, 0.0F);
                    worldIn.addFreshEntity(e1);
                    worldIn.addFreshEntity(e2);
                }
            }
        }
        return super.finishUsingItem(stack, worldIn, player);
    }

}
