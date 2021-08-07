package xerca.xercamod.common.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.packets.ScytheAttackPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

import static xerca.xercamod.common.item.Items.ENCHANTMENT_GUILLOTINE;

@ParametersAreNonnullByDefault
public class ItemScythe extends DiggerItem {
    private static final net.minecraft.tags.Tag<Block> EFFECTIVE_ON = net.minecraft.tags.Tag.fromSet(ImmutableSet.of());

    public ItemScythe(Tier tier, int attackDamageIn, float attackSpeedIn, Item.Properties builder) {
        super(attackDamageIn, attackSpeedIn, tier, EFFECTIVE_ON, builder.addToolType(net.minecraftforge.common.ToolType.PICKAXE, tier.getLevel()));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        if(blockState.getBlock() instanceof CropBlock){
            if(((CropBlock)blockState.getBlock()).isMaxAge(blockState)){
                return 1.0f;
            }
            else {
                return 0.0f;
            }
        }
        return 1.0f;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player)
    {
        int sweeping = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, itemstack);
        if(sweeping > 0){
            Level world = player.level;
            BlockState southBS = world.getBlockState(pos.south());
            BlockState northBS = world.getBlockState(pos.north());
            BlockState westBS = world.getBlockState(pos.west());
            BlockState eastBS = world.getBlockState(pos.east());

            if(isMaxCrop(southBS)){
                breakBlock(pos.south(), world, player);
            }
            if(isMaxCrop(northBS)){
                breakBlock(pos.north(), world, player);
            }
            if(isMaxCrop(westBS)){
                breakBlock(pos.west(), world, player);
            }
            if(isMaxCrop(eastBS)){
                breakBlock(pos.east(), world, player);
            }

            if(sweeping > 1){
                BlockState seBS = world.getBlockState(pos.south().east());
                BlockState swBS = world.getBlockState(pos.south().west());
                BlockState neBS = world.getBlockState(pos.north().west());
                BlockState nwBS = world.getBlockState(pos.north().east());

                if(isMaxCrop(seBS)){
                    breakBlock(pos.south().east(), world, player);
                }
                if(isMaxCrop(swBS)){
                    breakBlock(pos.south().west(), world, player);
                }
                if(isMaxCrop(neBS)){
                    breakBlock(pos.north().west(), world, player);
                }
                if(isMaxCrop(nwBS)){
                    breakBlock(pos.north().east(), world, player);
                }
            }
        }
        return false;
    }

    private void breakBlock(BlockPos pos, Level world, Player player){
        world.destroyBlock(pos, true, player);
    }

    private boolean isMaxCrop(BlockState bs){
        return (bs.getBlock() instanceof TallGrassBlock) || (bs.getBlock() instanceof CropBlock && ((CropBlock)bs.getBlock()).isMaxAge(bs));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench)
    {
        return ench.category == EnchantmentCategory.BREAKABLE || ench == Enchantments.BLOCK_FORTUNE || ench == ENCHANTMENT_GUILLOTINE
                || ench == Enchantments.SHARPNESS || ench == Enchantments.SMITE || ench == Enchantments.SWEEPING_EDGE
                || ench == Enchantments.BANE_OF_ARTHROPODS || ench == Enchantments.MOB_LOOTING;
    }

    public static CompoundTag getSkullNBT(List<Integer> id, String texture) {
        CompoundTag skullNBT = new CompoundTag();
        CompoundTag propertiesNBT = new CompoundTag();
        ListTag texturesNBT = new ListTag();
        CompoundTag tNBT = new CompoundTag();
        tNBT.putString("Value", texture);
        texturesNBT.add(tNBT);
        propertiesNBT.put("textures", texturesNBT);
        skullNBT.put("Properties", propertiesNBT);
        skullNBT.putIntArray("Id", id);
        return skullNBT;
    }

    public static void spawnHead(LivingEntity target){
        if(target instanceof Player){
            spawnHead((Player) target, target.level, target.getX(), target.getY(), target.getZ());
        }
        else if(target.getType() == EntityType.COW){
            spawnHead(Arrays.asList(-2094654955,1289635317,-2072061254,-1389687975), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBlNGU2ZmJmNWYzZGNmOTQ0MjJhMWYzMTk0NDhmMTUyMzY5ZDE3OWRiZmJjZGYwMGU1YmZlODQ5NWZhOTc3In19fQ==", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.cow");
        }
        else if(target.getType() == EntityType.IRON_GOLEM){
            spawnHead(Arrays.asList(-417497690,-2115092234,-1652637095,-1368159706), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM2Y2Q3MjAyYzM0ZTc4ZjMwNzMwOTAzNDlmN2Q5NzNiMjg4YWY1ZTViNzMzNGRkMjQ5MDEwYjNmMjcwNzhmOSJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.iron_golem");
        }
        else if(target.getType() == EntityType.OCELOT){
            spawnHead(Arrays.asList(26786972,-1196471604,-1857097459,-1977271489), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE4YjZiNzk3ODMzNjhkZmUwMDQyOTg1MTEwZGEzNjZmOWM3ODhiNDUwOTdhM2VhNmQwZDlhNzUzZTlmNDJjNiJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.ocelot");
        }
        else if(target.getType() == EntityType.BLAZE){
            spawnHead(Arrays.asList(-1988261685,-188068212,-1178146933,-1658240924), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA2ZTM0MmY5MGVjNTM4YWFhMTU1MmIyMjRmMjY0YTA0MDg0MDkwMmUxMjZkOTFlY2U2MTM5YWE1YjNjN2NjMyJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.blaze");
        }
        else if(target.getType() == EntityType.PIG){
            spawnHead(Arrays.asList(1003849902,-32486937,-1481465560,1732265), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTU2MmEzN2I4NzFmOTY0YmZjM2UxMzExZWE2NzJhYWEwMzk4NGE1ZGM0NzIxNTRhMzRkYzI1YWYxNTdlMzgyYiJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.pig");
        }
        else if(target.getType() == EntityType.SLIME){
            spawnHead(Arrays.asList(-1039916148,-57717813,-1391423156,-248573967), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZjMjdiMDEzZjFiZjMzNDQ4NjllODFlNWM2MTAwMjdiYzQ1ZWM1Yjc5NTE0ZmRjOTZlMDFkZjFiN2UzYTM4NyJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.slime");
        }
        else if(target.getType() == EntityType.VILLAGER){
            spawnHead(Arrays.asList(-579522334,-80917881,-1713988716,921668381), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRiZDgzMjgxM2FjMzhlNjg2NDg5MzhkN2EzMmY2YmEyOTgwMWFhZjMxNzQwNDM2N2YyMTRiNzhiNGQ0NzU0YyJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.villager");
        }
        else if(target.getType() == EntityType.CAVE_SPIDER){
            spawnHead(Arrays.asList(202094529,1049906219,-1603801553,1961515467), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdiMDcwNjNhNjg3NGZhM2UyMjU0OGUwMjA2MmJkNzMzYzI1ODg1OTI5ODA5NjI0MTgwYWViYjg1MTU1N2Y2YSJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.cave_spider");
        }
        else if(target.getType() == EntityType.ENDERMAN){
            spawnHead(Arrays.asList(-620677106,-2030549868,-1207093980,1829338684), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIwOWEzNzUyNTEwZTkxNGIwYmRjOTA5NmIzOTJiYjM1OWY3YThlOGE5NTY2YTAyZTdmNjZmYWZmOGQ2Zjg5ZSJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.enderman");
        }
        else if(target.getType() == EntityType.MAGMA_CUBE){
            spawnHead(Arrays.asList(-1846771288,385960253,-1088350611,-1439946228), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDkwZDYxZThjZTk1MTFhMGEyYjVlYTI3NDJjYjFlZjM2MTMxMzgwZWQ0MTI5ZTFiMTYzY2U4ZmYwMDBkZThlYSJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.magma_cube");
        }
        else if(target.getType() == EntityType.ZOMBIFIED_PIGLIN){
            spawnHead(Arrays.asList(-174221110,851921639,-1251029810,-1727624143), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE2ZDE2N2M1NzQ0ZWQxNGViYzAyZjQ0N2YzMjYxNDA1OTM2MmI3ZDJlY2I4MDhmZjA2MTY1ZDJjMzQzYmVmMiJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.zombie_pigman");
        }
        else if(target.getType() == EntityType.SPIDER){
            spawnHead(Arrays.asList(848845569,-89043442,-1563667252,1307407919), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYxYTQ5NTQxYTgzNmFhOGY0Zjc2ZTBkNGNiMmZmMDQ4ODhjNjJmOTQxMWVhMTBjYmFjZjFmMmE1NDQyNDI0MCJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.spider");
        }
        else if(target.getType() == EntityType.CHICKEN){
            spawnHead(Arrays.asList(1799972324,1156335733,-1788030142,1902426427), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE2YjhlOTgzODljNTQxYmIzNjQ1Mzg1MGJjYmQxZjdiYzVhNTdkYTYyZGNjNTA1MDYwNDA5NzM3ZWM1YjcyYSJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.chicken");
        }
        else if(target.getType() == EntityType.GHAST){
            spawnHead(Arrays.asList(1340996983,-1860222137,-1353501742,-1474776410), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE0ZTQyZWIxNWEwODgxM2E2YTZmNjFmMTBhYTI4ODAxOWZhMGZhZTEwNmEyOTUzZGRiNDZmNzdlZTJkNzdmIn19fQ==", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.ghast");
        }
        else if(target.getType() == EntityType.MOOSHROOM){
            spawnHead(Arrays.asList(-1612461734,989744374,-1157404361,1657691129), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTIzY2ZjNTU4MjQ1NGZjZjk5MDZmODQxZmRhMmNjNmFlODk2Y2Y0NTU4MjFjNGFkYTE5OThkZTcwODc3Y2M4NiJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.mooshroom");
        }
        else if(target.getType() == EntityType.SHEEP){
            spawnHead(Arrays.asList(262421674,-1762178383,-1870145564,1689656607), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NhMzhjY2Y0MTdlOTljYTlkNDdlZWIxNWE4YTMwZWRiMTUwN2FhNTJiNjc4YzIyMGM3MTdjNDc0YWE2ZmUzZSJ9fX0=", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.sheep");
        }
        else if(target.getType() == EntityType.SQUID){
            spawnHead(Arrays.asList(-1940995388,882658030,-1188418964,246238058), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU4OTEwMWQ1Y2M3NGFhNDU4MDIxYTA2MGY2Mjg5YTUxYTM1YTdkMzRkOGNhZGRmYzNjZGYzYjJjOWEwNzFhIn19fQ==", target.level, target.getX(), target.getY(), target.getZ(), "xercamod.head.squid");
        }
    }

    private static void spawnHead(Tag skullOwner, Level world, double x, double y, double z, @Nullable String nameTransKey){
        ItemStack playerHead = new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD, 1);
        CompoundTag headNBT = playerHead.getOrCreateTag();
        headNBT.put("SkullOwner", skullOwner);
        if(nameTransKey != null){
            playerHead.setHoverName(new TranslatableComponent(nameTransKey));
        }
        Containers.dropItemStack(world, x, y, z, playerHead);
    }

    private static void spawnHead(List<Integer> id, String texture, Level world, double x, double y, double z, @Nullable String nameTransKey){
        spawnHead(getSkullNBT(id, texture), world, x, y, z, nameTransKey);
    }

    private static void spawnHead(Player player, Level world, double x, double y, double z){
        spawnHead(NbtUtils.writeGameProfile(new CompoundTag(), player.getGameProfile()), world, x, y, z, null);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isScytheEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
    {
        if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, stack) > 0){
            float damage = (float)player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            float bonusDamage;
            if (entity instanceof LivingEntity) {
                bonusDamage = EnchantmentHelper.getDamageBonus(stack, ((LivingEntity)entity).getMobType());
            } else {
                bonusDamage = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
            }

            float cooldownStrength = player.getAttackStrengthScale(0.5f);
            damage *= (0.2F + cooldownStrength * cooldownStrength * 0.8F);
            bonusDamage *= cooldownStrength;
            damage += bonusDamage;

            boolean cooledAttack = cooldownStrength > 0.9F;
            boolean cooledSprintAttack = player.isSprinting() && cooledAttack;
            boolean critical = cooledAttack && player.fallDistance > 0.0F && !player.isOnGround() && !player.onClimbable() &&
                    !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() &&
                    entity instanceof LivingEntity  && !player.isSprinting();
//        net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, entity, critical, critical ? 1.5F : 1.0F);
//        critical = hitResult != null;

            double d0 = player.walkDist - player.walkDistO;
            boolean sweep = cooledAttack && !critical && !cooledSprintAttack && player.isOnGround() &&
                    d0 < player.getSpeed();

            if(sweep){
                float sweepDamage = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * damage;

                for(LivingEntity livingentity : player.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
                    if (livingentity != player && livingentity != entity && !player.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && player.distanceToSqr(livingentity) < 9.0D) {
//                        livingentity.knockback(player, 0.4F, MathHelper.sin(player.rotationYaw * ((float)Math.PI / 180F)), (-MathHelper.cos(player.rotationYaw * ((float)Math.PI / 180F))));
                        livingentity.knockback(0.4F, (double)Mth.sin(player.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(player.getYRot() * ((float)Math.PI / 180F))));
                        livingentity.hurt(DamageSource.playerAttack(player), sweepDamage);
                    }
                }

                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
                player.sweepAttack();
            }
        }

        return false;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if(EnchantmentHelper.getItemEnchantmentLevel(ENCHANTMENT_GUILLOTINE, heldItem) > 0) {
            playerIn.startUsingItem(hand);
            return InteractionResultHolder.success(heldItem);
        }
        return InteractionResultHolder.pass(heldItem);
    }

    @Nonnull
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if(EnchantmentHelper.getItemEnchantmentLevel(ENCHANTMENT_GUILLOTINE, stack) > 0){
            return UseAnim.BOW;
        }
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if(EnchantmentHelper.getItemEnchantmentLevel(ENCHANTMENT_GUILLOTINE, stack) > 0) {
            return 72000;
        }
        return 0;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player)) return;
        if(EnchantmentHelper.getItemEnchantmentLevel(ENCHANTMENT_GUILLOTINE, stack) <= 0) return;

        Player player = (Player) entityLiving;

        // Number of seconds that the item has been used for
        float f = (this.getUseDuration(stack) - timeLeft) / 20.0f;
        if(f > 1.f) f = 1.f;

        if ((double) f >= 0.9D) {
            player.swing(InteractionHand.MAIN_HAND);
            if (worldIn.isClientSide) {
                Minecraft mine = Minecraft.getInstance();
                if (mine.hitResult != null){
                    if(mine.hitResult.getType() == HitResult.Type.ENTITY) {
                        Entity target = ((EntityHitResult) mine.hitResult).getEntity();
                        ScytheAttackPacket pack = new ScytheAttackPacket(f, target.getId());
                        XercaMod.NETWORK_HANDLER.sendToServer(pack);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        TranslatableComponent text = new TranslatableComponent("xercamod.scythe_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
        if(EnchantmentHelper.getItemEnchantmentLevel(ENCHANTMENT_GUILLOTINE, stack) > 0){
            TranslatableComponent textGuillotine = new TranslatableComponent("xercamod.guillotine_tooltip");
            tooltip.add(textGuillotine.withStyle(ChatFormatting.YELLOW));
        }
    }
}
