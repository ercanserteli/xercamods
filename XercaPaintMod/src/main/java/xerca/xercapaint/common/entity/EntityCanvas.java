package xerca.xercapaint.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.Items;

import javax.annotation.Nullable;


public class EntityCanvas extends HangingEntity implements IEntityAdditionalSpawnData {
    private CompoundNBT canvasNBT;
    private int tickCounter1 = 0;
    private CanvasType canvasType;

    public EntityCanvas(World world, CompoundNBT canvasNBT, BlockPos pos, Direction facing, CanvasType canvasType) {
        super(Entities.CANVAS, world, pos);
        this.canvasNBT = canvasNBT;
        this.canvasType = canvasType;

        this.updateFacingWithBoundingBox(facing);
    }

    public EntityCanvas(EntityType<EntityCanvas> entityCanvasEntityType, World world) {
        super(entityCanvasEntityType, world);
    }

    public EntityCanvas(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        super(Entities.CANVAS, world);
    }

    public CompoundNBT getCanvasNBT() {
        return canvasNBT;
    }

    @Override
    public int getWidthPixels() {
        return CanvasType.getWidth(canvasType);
    }

    @Override
    public int getHeightPixels() {
        return CanvasType.getHeight(canvasType);
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity) {
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity)brokenEntity;
                if (playerentity.abilities.isCreativeMode) {
                    return;
                }
            }
            ItemStack canvasItem;
            if(canvasType == CanvasType.SMALL){
                canvasItem = new ItemStack(Items.ITEM_CANVAS);
            }
            else if(canvasType == CanvasType.LARGE){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_LARGE);

            }
            else if(canvasType == CanvasType.LONG){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_LONG);

            }
            else if(canvasType == CanvasType.TALL){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_TALL);
            }else{
                XercaPaint.LOGGER.error("Invalid canvas type");
                return;
            }
            canvasItem.setTag(this.canvasNBT.copy());
            this.entityDropItem(canvasItem);
        }
    }

    public void tick() {
        this.prevPosX = this.getPosX();
        this.prevPosY = this.getPosY();
        this.prevPosZ = this.getPosZ();
        if (this.tickCounter1++ == 50 && !this.world.isRemote) {
            this.tickCounter1 = 0;
            if (this.isAlive() && !this.onValidSurface()) {
                this.remove();
                this.onBroken(null);
            }
        }

    }

    @Override
    public void playPlaceSound() {

    }


    private double offs(int l) {
        return l % 32 == 0 ? 0.5D : 0.0D;
    }

    @Override
    protected void updateBoundingBox(){
        if(canvasType != null){
//            super.updateBoundingBox();
            if (this.facingDirection != null) {
                double d0 = (double)this.hangingPosition.getX() + 0.5D;
                double d1 = (double)this.hangingPosition.getY() + 0.5D;
                double d2 = (double)this.hangingPosition.getZ() + 0.5D;
                double d4 = this.offs(this.getWidthPixels());
                double d5 = this.offs(this.getHeightPixels());
                d0 = d0 - (double)this.facingDirection.getXOffset() * 0.46875D;
                d2 = d2 - (double)this.facingDirection.getZOffset() * 0.46875D;
                d1 = d1 + d5;
                Direction direction = this.facingDirection.rotateYCCW();
                d0 = d0 + d4 * (double)direction.getXOffset();
                d2 = d2 + d4 * (double)direction.getZOffset();
                this.setRawPosition(d0, d1, d2);
                double d6 = this.getWidthPixels()-2;
                double d7 = this.getHeightPixels()-2;
                double d8 = this.getWidthPixels()-2;
                if (this.facingDirection.getAxis() == Direction.Axis.Z) {
                    d8 = 1.0D;
                } else {
                    d6 = 1.0D;
                }

                d6 = d6 / 32.0D;
                d7 = d7 / 32.0D;
                d8 = d8 / 32.0D;
                this.setBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
            }
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        this.canvasNBT = tagCompound.getCompound("canvas");
        this.canvasType = CanvasType.fromByte(tagCompound.getByte("ctype"));
        super.readAdditional(tagCompound);
    }

    @Override
    public void writeAdditional(CompoundNBT tagCompound) {
        tagCompound.put("canvas", canvasNBT);
        tagCompound.putByte("ctype", (byte)canvasType.ordinal());
        super.writeAdditional(tagCompound);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeCompoundTag(canvasNBT);
        buffer.writeInt(facingDirection.getIndex());
        buffer.writeByte(canvasType.ordinal());
        buffer.writeBlockPos(hangingPosition); // this has to be written, otherwise pos gets broken
//        XercaPaint.LOGGER.debug("writeSpawnData Pos: " + this.hangingPosition.toString() + " posY: " + this.posY);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer) {
        canvasNBT = buffer.readCompoundTag();
        facingDirection = Direction.byIndex(buffer.readInt());
        canvasType = CanvasType.fromByte(buffer.readByte());
        hangingPosition = buffer.readBlockPos();

        updateFacingWithBoundingBox(facingDirection);
//        XercaPaint.LOGGER.debug("readSpawnData Pos: " + this.hangingPosition.toString() + " posY: " + this.posY);
    }
}
