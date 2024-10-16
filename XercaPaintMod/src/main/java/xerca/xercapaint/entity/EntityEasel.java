package xerca.xercapaint.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.CloseGuiPacket;
import xerca.xercapaint.packets.OpenGuiPacket;

import javax.annotation.Nullable;


public class EntityEasel extends Entity {
    private static final EntityDataAccessor<ItemStack> DATA_CANVAS;
    private Player painter = null;
    private Runnable dropDeferred = null;
    private int dropWaitTicks = 0;

    static {
        DATA_CANVAS = SynchedEntityData.defineId(EntityEasel.class, EntityDataSerializers.ITEM_STACK);
    }

    public EntityEasel(Level world) {
        super(Entities.EASEL, world);
    }

    public EntityEasel(EntityType<EntityEasel> entityCanvasEntityType, Level world) {
        super(entityCanvasEntityType, world);
    }

    public void setPainter(Player painter){
        this.painter = painter;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float p_31580_) {
        if (!this.level().isClientSide && !this.isRemoved()) {
            if(!getItem().isEmpty() && !damageSource.is(DamageTypeTags.IS_EXPLOSION)){
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
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.BIRCH_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, this.getBbWidth() / 4.0F, this.getBbHeight() / 4.0F, this.getBbWidth() / 4.0F, 0.05D);
        }
    }

    @Override
    public void kill() {
        showBreakingParticles();
        this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(DATA_CANVAS, ItemStack.EMPTY);
    }

    public void dropItem(@Nullable Entity entity) {
        this.dropItem(entity, true);
    }

    private void dropItem(@Nullable Entity entity, boolean dropSelf) {
        if(painter != null){
            if(!this.level().isClientSide){
                if(dropDeferred == null){
                    CloseGuiPacket pack = new CloseGuiPacket();
                    ServerPlayNetworking.send((ServerPlayer) painter, pack);
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

        if (entity instanceof Player player) {
            if (player.getAbilities().instabuild) {
                return;
            }
        }

        if (dropSelf && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
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

    @Override
    public @NotNull SlotAccess getSlot(int i) {
        return i == 0 ? new SlotAccess() {
            @Override
            public @NotNull ItemStack get() {
                return EntityEasel.this.getItem();
            }

            @Override
            public boolean set(@NotNull ItemStack itemStack) {
                EntityEasel.this.setItem(itemStack);
                return true;
            }
        } : super.getSlot(i);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor.equals(DATA_CANVAS)) {
            ItemStack itemStack = this.getItem();
            if (!itemStack.isEmpty() && itemStack.getEntityRepresentation() != this) {
                itemStack.setEntityRepresentation(this);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        if (!this.getItem().isEmpty()) {
            tag.put("Item", this.getItem().save(this.registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        CompoundTag itemTag = tag.getCompound("Item");
        if (!itemTag.isEmpty()) {
            ItemStack itemStack = ItemStack.parseOptional(this.registryAccess(), itemTag);
            if (itemStack.isEmpty()) {
                Mod.LOGGER.warn("Unable to load item from: {}", itemTag);
            }
            this.setItem(itemStack, false);
        }
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        boolean isEaselFilled = !this.getItem().isEmpty();
        boolean handHoldsCanvas = itemInHand.getItem() instanceof ItemCanvas;
        boolean handHoldsPalette = itemInHand.getItem() instanceof ItemPalette;
        if(this.level().isClientSide){
            return !isEaselFilled && !handHoldsCanvas ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }
        else {
            if (!isEaselFilled) {
                if (handHoldsCanvas && !this.isRemoved()) {
                    this.setItem(itemInHand);
                    itemInHand.shrink(1);
                }
            }else{
                boolean unused = this.painter == null;
                boolean toEdit = handHoldsPalette && !(getItem().getOrDefault(Items.CANVAS_GENERATION, 0) > 0);
                boolean allowed = unused || !toEdit;
                OpenGuiPacket pack = new OpenGuiPacket(this.getId(), allowed, toEdit, hand);
                ServerPlayNetworking.send((ServerPlayer) player, pack);
                if(toEdit && allowed){
                    this.painter = player;
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    protected ItemStack getEaselItemStack() {
        return new ItemStack(Items.ITEM_EASEL);
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack canvas = this.getItem();
        return canvas.isEmpty() ? this.getEaselItemStack() : canvas.copy();
    }

    @Override
    public void tick() {
        super.tick();
        move(MoverType.SELF, new Vec3(0, -0.25, 0));
        reapplyPosition();
        if(!this.level().isClientSide){
            if(dropDeferred != null){
                dropWaitTicks ++;
                if(painter == null || dropWaitTicks > 80){
                    dropDeferred.run();
                    dropDeferred = null;
                    dropWaitTicks = 0;
                }
            }
        }
        if(painter != null){
            if(painter.isRemoved() || !painter.isAlive()){
                painter = null;
            }
            else if(painter.distanceToSqr(this) > 64){
                painter = null;
            }
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

}
