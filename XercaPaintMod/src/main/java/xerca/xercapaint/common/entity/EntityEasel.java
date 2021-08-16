package xerca.xercapaint.common.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import xerca.xercapaint.client.ClientProxy;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;


public class EntityEasel extends LivingEntity {
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(0, ItemStack.EMPTY);
    private static final EntityDataAccessor<ItemStack> DATA_CANVAS;
    private static final Predicate<Entity> RIDABLE_MINECARTS = (p_31582_) ->
            p_31582_ instanceof AbstractMinecart && ((AbstractMinecart)p_31582_).canBeRidden();


    static {
        DATA_CANVAS = SynchedEntityData.defineId(EntityEasel.class, EntityDataSerializers.ITEM_STACK);
    }

    public EntityEasel(Level world) {
        super(Entities.EASEL, world);
        yRotO = 0;
        setYRot(0);
        setYBodyRot(0);
        setYHeadRot(0);
    }

    public EntityEasel(EntityType<EntityEasel> entityCanvasEntityType, Level world) {
        super(entityCanvasEntityType, world);
        yRotO = 0;
        setYRot(0);
        setYBodyRot(0);
        setYHeadRot(0);
    }

    public EntityEasel(FMLPlayMessages.SpawnEntity spawnEntity, Level world) {
        super(Entities.EASEL, world);
        yRotO = 0;
        setYRot(0);
        setYBodyRot(0);
        setYHeadRot(0);
    }

    public boolean isPushable() {
    return false;
}

    protected void doPush(Entity p_31564_) {
    }

    protected void pushEntities() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS);

        for(int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (this.distanceToSqr(entity) <= 0.2D) {
                entity.push(this);
            }
        }
    }

    public boolean hurt(DamageSource damageSource, float p_31580_) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            if(!getItem().isEmpty()){
                this.dropItem(damageSource.getEntity(), false);
            }
            else{
                this.dropItem(damageSource.getEntity());
                kill();
            }
        }
        return false;
    }
//    public void handleEntityEvent(byte p_31568_) {
//        if (p_31568_ == 32) {
//            if (this.level.isClientSide) {
//                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
////                this.lastHit = this.level.getGameTime();
//            }
//        } else {
//            super.handleEntityEvent(p_31568_);
//        }
//    }

    private void showBreakingParticles() {
        if (this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.BIRCH_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
        }
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource p_31636_) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    public void kill() {
        showBreakingParticles();
        this.remove(Entity.RemovalReason.KILLED);
    }

    public boolean attackable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_CANVAS, ItemStack.EMPTY);
    }

    public void dropItem(@Nullable Entity entity) {
        this.dropItem(entity, true);
    }

    private void dropItem(@Nullable Entity entity, boolean p_31804_) {
        ItemStack canvasStack = this.getItem();
        this.setItem(ItemStack.EMPTY);
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {

        } else {
            if (!canvasStack.isEmpty()) {
                canvasStack = canvasStack.copy();
                this.spawnAtLocation(canvasStack);
            }

            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (player.getAbilities().instabuild) {
                    return;
                }
            }

            if (p_31804_ ) {
                this.spawnAtLocation(this.getEaselItemStack());
            }
        }
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_CANVAS);
    }

    public void setItem(ItemStack itemStack) {
        this.setItem(itemStack, true);
    }

    public void setItem(ItemStack itemStack, boolean makeSound) {
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(1);
            itemStack.setEntityRepresentation(this);
        }

        this.getEntityData().set(DATA_CANVAS, itemStack);
        if(makeSound){
            if (!itemStack.isEmpty()) {
                this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
            }else{
                this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            }
        }
    }

    public SlotAccess getSlot(int i) {
        return i == 0 ? new SlotAccess() {
            public ItemStack get() {
                return EntityEasel.this.getItem();
            }

            public boolean set(ItemStack itemStack) {
                EntityEasel.this.setItem(itemStack);
                return true;
            }
        } : super.getSlot(i);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor.equals(DATA_CANVAS)) {
            ItemStack itemStack = this.getItem();
            if (!itemStack.isEmpty() && itemStack.getEntityRepresentation() != this) {
                itemStack.setEntityRepresentation(this);
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!this.getItem().isEmpty()) {
            tag.put("Item", this.getItem().save(new CompoundTag()));
        }
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        CompoundTag itemTag = tag.getCompound("Item");
        if (itemTag != null && !itemTag.isEmpty()) {
            ItemStack var3 = ItemStack.of(itemTag);
            if (var3.isEmpty()) {
                LOGGER.warn("Unable to load item from: {}", itemTag);
            }
            this.setItem(var3, false);
        }
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        boolean isEaselFilled = !this.getItem().isEmpty();
        boolean handHoldsCanvas = itemInHand.getItem() instanceof ItemCanvas;
        boolean handHoldsPalette = itemInHand.getItem() instanceof ItemPalette;
        if(this.level.isClientSide){
            if(isEaselFilled){
                if(handHoldsPalette){
                    // Edit painting
                    ClientProxy.showCanvasGui(this, itemInHand);
                }
                else{
                    // Show painting
                    ClientProxy.showCanvasGui(this, ItemStack.EMPTY);
                }
            }
            return !isEaselFilled && !handHoldsCanvas ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }
        else {
            if (!isEaselFilled) {
                if (handHoldsCanvas && !this.isRemoved()) {
                    this.setItem(itemInHand);
                    itemInHand.shrink(1);
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    protected ItemStack getEaselItemStack() {
        return new ItemStack(Items.ITEM_EASEL);
    }

    public ItemStack getPickResult() {
        ItemStack canvas = this.getItem();
        return canvas.isEmpty() ? this.getEaselItemStack() : canvas.copy();
    }

    public void tick() {
        super.tick();
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }


    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

}
