package xerca.xercapaint.common.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.packets.CloseGuiPacket;
import xerca.xercapaint.common.packets.OpenGuiPacket;

import javax.annotation.Nullable;


public class EntityEasel extends Entity {
    private static final DataParameter<ItemStack> DATA_CANVAS;
    private PlayerEntity painter = null;
    private Runnable dropDeferred = null;
    private int dropWaitTicks = 0;

    static {
        DATA_CANVAS = EntityDataManager.defineId(EntityEasel.class, DataSerializers.ITEM_STACK);
    }

    public EntityEasel(World world) {
        super(Entities.EASEL, world);
    }

    public EntityEasel(EntityType<EntityEasel> entityCanvasEntityType, World world) {
        super(entityCanvasEntityType, world);
    }

    public EntityEasel(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        super(Entities.EASEL, world);
    }

    public void setPainter(PlayerEntity painter){
        this.painter = painter;
    }

    public PlayerEntity getPainter(){
        return this.painter;
    }

    public boolean isPushable() {
    return false;
}

    public boolean hurt(DamageSource damageSource, float p_31580_) {
        if (!this.level.isClientSide && this.isAlive()) {
            if(!getItem().isEmpty() && !damageSource.isExplosion()){
                this.dropItem(damageSource.getEntity(), false);
            }
            else{
                this.dropItem(damageSource.getEntity());
                kill();
            }
        }
        return false;
    }

    private void showBreakingParticles() {
        if (this.level instanceof ServerWorld) {
            ((ServerWorld)this.level).sendParticles(new BlockParticleData(ParticleTypes.BLOCK, Blocks.BIRCH_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
        }
    }

    public void kill() {
        showBreakingParticles();
        this.remove();
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_CANVAS, ItemStack.EMPTY);
    }

    public void dropItem(@Nullable Entity entity) {
        this.dropItem(entity, true);
    }

    private void dropItem(@Nullable Entity entity, boolean dropSelf) {
        if(painter != null){
            if(!level.isClientSide){
                if(dropDeferred == null){
                    CloseGuiPacket pack = new CloseGuiPacket();
                    XercaPaint.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) painter), pack);
                    dropDeferred = () -> doDrop(entity, dropSelf);
                }
            }
        }
        else{
            doDrop(entity, dropSelf);
        }
    }

    public void doDrop(@Nullable Entity entity, boolean dropSelf){
        ItemStack canvasStack = this.getItem();
        this.setItem(ItemStack.EMPTY);

        if (!canvasStack.isEmpty()) {
            canvasStack = canvasStack.copy();
            this.spawnAtLocation(canvasStack);
        }

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            if (player.isCreative()) {
                return;
            }
        }

        if (dropSelf && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(this.getEaselItemStack());
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

//    public SlotAccess getSlot(int i) {
//        return i == 0 ? new SlotAccess() {
//            public ItemStack get() {
//                return EntityEasel.this.getItem();
//            }
//
//            public boolean set(ItemStack itemStack) {
//                EntityEasel.this.setItem(itemStack);
//                return true;
//            }
//        } : super.getSlot(i);
//    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void onSyncedDataUpdated(DataParameter<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor.equals(DATA_CANVAS)) {
            ItemStack itemStack = this.getItem();
            if (!itemStack.isEmpty() && itemStack.getEntityRepresentation() != this) {
                itemStack.setEntityRepresentation(this);
            }
        }
    }

    public void addAdditionalSaveData(CompoundNBT tag) {
        if (!this.getItem().isEmpty()) {
            tag.put("Item", this.getItem().save(new CompoundNBT()));
        }
    }

    public void readAdditionalSaveData(CompoundNBT tag) {
        CompoundNBT itemTag = tag.getCompound("Item");
        if (itemTag != null && !itemTag.isEmpty()) {
            ItemStack var3 = ItemStack.of(itemTag);
            if (var3.isEmpty()) {
                LOGGER.warn("Unable to load item from: {}", itemTag);
            }
            this.setItem(var3, false);
        }
    }

    public ActionResultType interact(PlayerEntity player, Hand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        boolean isEaselFilled = !this.getItem().isEmpty();
        boolean handHoldsCanvas = itemInHand.getItem() instanceof ItemCanvas;
        boolean handHoldsPalette = itemInHand.getItem() instanceof ItemPalette;
        if(this.level.isClientSide){
            return !isEaselFilled && !handHoldsCanvas ? ActionResultType.PASS : ActionResultType.SUCCESS;
        }
        else {
            if (!isEaselFilled) {
                if (handHoldsCanvas && this.isAlive()) {
                    this.setItem(itemInHand);
                    itemInHand.shrink(1);
                }
            }else{
                boolean unused = this.painter == null;
                boolean toEdit = handHoldsPalette && !(getItem().hasTag() && getItem().getTag().getInt("generation") > 0);
                boolean allowed = unused || !toEdit;
                OpenGuiPacket pack = new OpenGuiPacket(this.getId(), allowed, toEdit, hand);
                XercaPaint.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), pack);
                if(toEdit && allowed){
                    this.painter = player;
                }
            }

            return ActionResultType.CONSUME;
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
        move(MoverType.SELF, new Vector3d(0, -0.25, 0));
        reapplyPosition();
        if(!level.isClientSide){
            if(dropDeferred != null && painter == null){
                dropDeferred.run();
                dropDeferred = null;
            }
        }
        if(painter != null){
            if(!painter.isAlive()){
                painter = null;
            }
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void setItemSlot(EquipmentSlotType equipmentSlot, ItemStack itemStack) {

    }

}
