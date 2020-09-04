package xerca.xercamod.common.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.TallGrassBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.packets.ScytheAttackPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

import static xerca.xercamod.common.item.Items.ENCHANTMENT_GUILLOTINE;

@ParametersAreNonnullByDefault
public class ItemScythe extends ToolItem {
    private static final Set<Block> EFFECTIVE_ON = ImmutableSet.of();

    public ItemScythe(IItemTier tier, int attackDamageIn, float attackSpeedIn, Item.Properties builder) {
        super(attackDamageIn, attackSpeedIn, tier, EFFECTIVE_ON, builder.addToolType(net.minecraftforge.common.ToolType.PICKAXE, tier.getHarvestLevel()));

        this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            @Override
            public float call(@Nonnull ItemStack stack, World worldIn, LivingEntity entityIn) {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    ItemStack itemstack = entityIn.getActiveItemStack();
                    return ((itemstack.getItem() instanceof ItemScythe)) ? (stack.getUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
                }
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            @Override
            public float call(@Nonnull ItemStack stack, World worldIn, LivingEntity entityIn) {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState blockState) {
        if(blockState.getBlock() instanceof CropsBlock){
            if(((CropsBlock)blockState.getBlock()).isMaxAge(blockState)){
                return 1.0f;
            }
            else {
                return 0.0f;
            }
        }
        return 1.0f;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player)
    {
        int sweeping = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, itemstack);
        if(sweeping > 0){
            World world = player.world;
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

    private void breakBlock( BlockPos pos, World world, PlayerEntity player){
        world.func_225521_a_(pos, true, player);
    }

    private boolean isMaxCrop(BlockState bs){
        return (bs.getBlock() instanceof TallGrassBlock) || (bs.getBlock() instanceof CropsBlock && ((CropsBlock)bs.getBlock()).isMaxAge(bs));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench)
    {
        return ench.type == EnchantmentType.BREAKABLE || ench == Enchantments.FORTUNE || ench == ENCHANTMENT_GUILLOTINE
                || ench == Enchantments.SHARPNESS || ench == Enchantments.SMITE || ench == Enchantments.SWEEPING
                || ench == Enchantments.BANE_OF_ARTHROPODS || ench == Enchantments.LOOTING;
    }

    public static void spawnHead(LivingEntity target){
        if(target instanceof PlayerEntity){
            spawnHead(target.getName().getString(), target.world, target.getPosX(), target.getPosY(), target.getPosZ());
        }
        else if(target.getType() == EntityType.COW){
            spawnHead("MHF_Cow", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.cow");
        }
        else if(target.getType() == EntityType.IRON_GOLEM){
            spawnHead("MHF_Golem", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.iron_golem");
        }
        else if(target.getType() == EntityType.OCELOT){
            spawnHead("MHF_Ocelot", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.ocelot");
        }
        else if(target.getType() == EntityType.BLAZE){
            spawnHead("MHF_Blaze", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.blaze");
        }
        else if(target.getType() == EntityType.PIG){
            spawnHead("MHF_Pig", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.pig");
        }
        else if(target.getType() == EntityType.SLIME){
            spawnHead("MHF_Slime", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.slime");
        }
        else if(target.getType() == EntityType.VILLAGER){
            spawnHead("MHF_Villager", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.villager");
        }
        else if(target.getType() == EntityType.CAVE_SPIDER){
            spawnHead("MHF_CaveSpider", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.cave_spider");
        }
        else if(target.getType() == EntityType.ENDERMAN){
            spawnHead("MHF_Enderman", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.enderman");
        }
        else if(target.getType() == EntityType.MAGMA_CUBE){
            spawnHead("MHF_LavaSlime", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.magma_cube");
        }
        else if(target.getType() == EntityType.ZOMBIE_PIGMAN){
            spawnHead("MHF_PigZombie", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.zombie_pigman");
        }
        else if(target.getType() == EntityType.SPIDER){
            spawnHead("MHF_Spider", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.spider");
        }
        else if(target.getType() == EntityType.CHICKEN){
            spawnHead("MHF_Chicken", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.chicken");
        }
        else if(target.getType() == EntityType.GHAST){
            spawnHead("MHF_Ghast", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.ghast");
        }
        else if(target.getType() == EntityType.MOOSHROOM){
            spawnHead("MHF_MushroomCow", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.mooshroom");
        }
        else if(target.getType() == EntityType.SHEEP){
            spawnHead("MHF_Sheep", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.sheep");
        }
        else if(target.getType() == EntityType.SQUID){
            spawnHead("MHF_Squid", target.world, target.getPosX(), target.getPosY(), target.getPosZ(), "xercamod.head.squid");
        }
    }

    private static void spawnHead(String playerName, World world, double x, double y, double z){
        spawnHead(playerName, world, x, y, z, null);
    }

    private static void spawnHead(String playerName, World world, double x, double y, double z, @Nullable String nameTransKey){
        ItemStack playerHead = new ItemStack(net.minecraft.item.Items.PLAYER_HEAD, 1);
        playerHead.getOrCreateTag().put("SkullOwner", StringNBT.valueOf(playerName));
        if(nameTransKey != null){
            playerHead.setDisplayName(new TranslationTextComponent(nameTransKey));
        }
        InventoryHelper.spawnItemStack(world, x, y, z, playerHead);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isScytheEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity)
    {
        if(EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, stack) > 0){
            float damage = (float)player.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
            float bonusDamage;
            if (entity instanceof LivingEntity) {
                bonusDamage = EnchantmentHelper.getModifierForCreature(stack, ((LivingEntity)entity).getCreatureAttribute());
            } else {
                bonusDamage = EnchantmentHelper.getModifierForCreature(stack, CreatureAttribute.UNDEFINED);
            }

            float cooldownStrength = player.getCooledAttackStrength(0.5f);
            damage *= (0.2F + cooldownStrength * cooldownStrength * 0.8F);
            bonusDamage *= cooldownStrength;
            damage += bonusDamage;

            boolean cooledAttack = cooldownStrength > 0.9F;
            boolean cooledSprintAttack = player.isSprinting() && cooledAttack;
            boolean critical = cooledAttack && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() &&
                    !player.isInWater() && !player.isPotionActive(Effects.BLINDNESS) && !player.isPassenger() &&
                    entity instanceof LivingEntity  && !player.isSprinting();
//        net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, entity, critical, critical ? 1.5F : 1.0F);
//        critical = hitResult != null;

            double d0 = player.distanceWalkedModified - player.prevDistanceWalkedModified;
            boolean sweep = cooledAttack && !critical && !cooledSprintAttack && player.onGround &&
                    d0 < player.getAIMoveSpeed();

            if(sweep){
                float sweepDamage = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * damage;

                for(LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, entity.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
                    if (livingentity != player && livingentity != entity && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity)livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 9.0D) {
                        livingentity.knockBack(player, 0.4F, MathHelper.sin(player.rotationYaw * ((float)Math.PI / 180F)), (-MathHelper.cos(player.rotationYaw * ((float)Math.PI / 180F))));
                        livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), sweepDamage);
                    }
                }

                player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                player.spawnSweepParticles();
            }
        }

        return false;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        if(EnchantmentHelper.getEnchantmentLevel(ENCHANTMENT_GUILLOTINE, heldItem) > 0) {
            playerIn.setActiveHand(hand);
            return ActionResult.resultSuccess(heldItem);
        }
        return ActionResult.resultPass(heldItem);
    }

    @Nonnull
    @Override
    public UseAction getUseAction(ItemStack stack) {
        if(EnchantmentHelper.getEnchantmentLevel(ENCHANTMENT_GUILLOTINE, stack) > 0){
            return UseAction.BOW;
        }
        return UseAction.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if(EnchantmentHelper.getEnchantmentLevel(ENCHANTMENT_GUILLOTINE, stack) > 0) {
            return 72000;
        }
        return 0;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof PlayerEntity)) return;
        if(EnchantmentHelper.getEnchantmentLevel(ENCHANTMENT_GUILLOTINE, stack) <= 0) return;

        PlayerEntity player = (PlayerEntity) entityLiving;

        // Number of seconds that the item has been used for
        float f = (this.getUseDuration(stack) - timeLeft) / 20.0f;
        if(f > 1.f) f = 1.f;

        if ((double) f >= 0.9D) {
            player.swingArm(Hand.MAIN_HAND);
            if (worldIn.isRemote) {
                Minecraft mine = Minecraft.getInstance();
                if (mine.objectMouseOver != null){
                    if(mine.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                        Entity target = ((EntityRayTraceResult) mine.objectMouseOver).getEntity();
                        ScytheAttackPacket pack = new ScytheAttackPacket(f, target.getEntityId());
                        XercaMod.NETWORK_HANDLER.sendToServer(pack);
                    }
                }

            }
        }
    }
}
