package xerca.xercapaint.common.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.commons.lang3.Validate;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.packets.PictureRequestPacket;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;


public class EntityCanvas extends HangingEntity implements IEntityAdditionalSpawnData {
    private String canvasName;
    private String canvasTitle;
    private String canvasAuthor;
    private int canvasVersion;
    private int canvasGeneration = 0;
    private boolean canvasSigned;
    private int tickCounter1 = 0;
    private CanvasType canvasType;
    private static final DataParameter<Integer> ROTATION = EntityDataManager.createKey(EntityCanvas.class, DataSerializers.VARINT);
    public static final Map<String, Picture> PICTURES = Maps.newHashMap();
    public static final Set<String> PICTURE_REQUESTS = Sets.newHashSet();

    public EntityCanvas(World world, CompoundNBT canvasNBT, BlockPos pos, Direction facing, CanvasType canvasType, int rotation) {
        super(Entities.CANVAS, world, pos);
        this.canvasName = canvasNBT.getString("name");
        this.canvasVersion = canvasNBT.getInt("v");
        if(canvasNBT.contains("title") && canvasNBT.contains("author")){
            this.canvasSigned = true;
            this.canvasTitle = canvasNBT.getString("title");
            this.canvasAuthor = canvasNBT.getString("author");
            this.canvasGeneration = canvasNBT.getInt("generation");
        }else{
            this.canvasSigned = false;
        }
        this.canvasType = canvasType;
        this.setRotation(rotation);

        this.updateFacingWithBoundingBox(facing);

        Picture picture = PICTURES.get(canvasName);
        if(picture == null || picture.version < canvasVersion){
            PICTURES.put(canvasName, new Picture(canvasVersion, canvasNBT.getIntArray("pixels")));
        }
    }

    public EntityCanvas(EntityType<EntityCanvas> entityCanvasEntityType, World world) {
        super(entityCanvasEntityType, world);
    }

    public EntityCanvas(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        super(Entities.CANVAS, world);
    }

//    public CompoundNBT getCanvasNBT() {
//        return canvasNBT;
//    }

    protected void registerData() {
        this.getDataManager().register(ROTATION, 0);
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
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.0F;
    }

    public String getCanvasName() {
        return canvasName;
    }

    public int getCanvasVersion() {
        return canvasVersion;
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
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("name", canvasName);
            nbt.putInt("v", canvasVersion);
            nbt.putInt("generation", 0);
            if (canvasSigned) {
                nbt.putString("author", canvasAuthor);
                nbt.putString("title", canvasTitle);
                nbt.putInt("generation", canvasGeneration);
            }
            Picture picture = PICTURES.get(canvasName);
            if(picture != null){
                nbt.putIntArray("pixels", picture.pixels);
            }

            canvasItem.setTag(nbt);
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
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void updateFacingWithBoundingBox(Direction facingDirectionIn) {
        Validate.notNull(facingDirectionIn);
        this.facingDirection = facingDirectionIn;
        if (facingDirectionIn.getAxis().isHorizontal()) {
            this.rotationPitch = 0.0F;
            this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
        } else {
            this.rotationPitch = (float)(-90 * facingDirectionIn.getAxisDirection().getOffset());
            this.rotationYaw = 0.0F;
        }

        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        this.updateBoundingBox();
    }

    private double offs(int l) {
        return l % 32 == 0 ? 0.5D : 0.0D;
    }

    @Override
    protected void updateBoundingBox(){
        if(canvasType != null){
            if (this.facingDirection != null) {
                double d1 = (double)this.hangingPosition.getX() + 0.5D - (double)this.facingDirection.getXOffset() * 0.46875D;
                double d2 = (double)this.hangingPosition.getY() + 0.5D - (double)this.facingDirection.getYOffset() * 0.46875D;
                double d3 = (double)this.hangingPosition.getZ() + 0.5D - (double)this.facingDirection.getZOffset() * 0.46875D;

                if(this.facingDirection.getAxis().isHorizontal()){
                    double d4 = this.offs(this.getWidthPixels());
                    double d5 = this.offs(this.getHeightPixels());
                    d2 = d2 + d5;
                    Direction direction = this.facingDirection.rotateYCCW();
                    d1 = d1 + d4 * (double)direction.getXOffset();
                    d3 = d3 + d4 * (double)direction.getZOffset();
                }else{

                }

                this.setRawPosition(d1, d2, d3);
                double d6 = this.getWidthPixels()-2;
                double d7 = this.getHeightPixels()-2;
                double d8 = this.getWidthPixels()-2;
                Direction.Axis direction$axis = this.facingDirection.getAxis();
                switch(direction$axis) {
                    case X:
                        d6 = 1.0D;
                        break;
                    case Y:
                        d7 = 1.0D;
                        break;
                    case Z:
                        d8 = 1.0D;
                }

                d6 = d6 / 32.0D;
                d7 = d7 / 32.0D;
                d8 = d8 / 32.0D;
                this.setBoundingBox(new AxisAlignedBB(d1 - d6, d2 - d7, d3 - d8, d1 + d6, d2 + d7, d3 + d8));
            }
        }
    }

    @Override
    public boolean onValidSurface() {
        if(facingDirection.getAxis().isHorizontal()){
            return super.onValidSurface();
        }
        if (!this.world.hasNoCollisions(this)) {
            return false;
        } else {
            BlockState blockstate = this.world.getBlockState(this.hangingPosition.offset(this.facingDirection.getOpposite()));
            return blockstate.getMaterial().isSolid() ||
                    this.facingDirection.getAxis().isHorizontal() && RedstoneDiodeBlock.isDiode(blockstate) ? this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_HANGING_ENTITY).isEmpty() : false;
        }
    }

    public int getRotation() {
        return this.getDataManager().get(ROTATION);
    }

    private void setRotation(int rotation) {
        this.getDataManager().set(ROTATION, rotation % 4);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        this.hangingPosition = new BlockPos(tagCompound.getInt("TileX"), tagCompound.getInt("TileY"), tagCompound.getInt("TileZ"));
        CompoundNBT canvasNBT = tagCompound;
        if(tagCompound.contains("canvas")){
            canvasNBT = tagCompound.getCompound("canvas");
        }
        this.canvasSigned = canvasNBT.contains("author") && canvasNBT.contains("title");
        this.canvasName = canvasNBT.getString("name");
        this.canvasVersion = canvasNBT.getInt("v");
        if(canvasSigned)
        {
            this.canvasAuthor = canvasNBT.getString("author");
            this.canvasTitle = canvasNBT.getString("title");
            this.canvasGeneration = canvasNBT.getInt("generation");
        }

        Picture picture = PICTURES.get(canvasName);
        if(picture == null || picture.version < canvasVersion){
            PICTURES.put(canvasName, new Picture(canvasVersion, canvasNBT.getIntArray("pixels")));
        }

        this.canvasType = CanvasType.fromByte(tagCompound.getByte("ctype"));
        if(tagCompound.contains("Facing") && !tagCompound.contains("RealFace")){
            Direction horizontal = Direction.byHorizontalIndex(tagCompound.getByte("Facing"));
            this.updateFacingWithBoundingBox(horizontal);
        }
        else{
            this.updateFacingWithBoundingBox(Direction.byIndex(tagCompound.getByte("RealFace")));
        }
        this.setRotation(tagCompound.getByte("Rotation"));
    }

    @Override
    public void writeAdditional(CompoundNBT tagCompound) {
        BlockPos blockpos = this.getHangingPosition();
        tagCompound.putInt("TileX", blockpos.getX());
        tagCompound.putInt("TileY", blockpos.getY());
        tagCompound.putInt("TileZ", blockpos.getZ());
        tagCompound.putString("name", canvasName);
        tagCompound.putInt("v", canvasVersion);
        if(canvasSigned){
            tagCompound.putString("author", canvasAuthor);
            tagCompound.putString("title", canvasTitle);
            tagCompound.putInt("generation", canvasGeneration);
        }
        tagCompound.putByte("ctype", (byte)canvasType.ordinal());
        tagCompound.putByte("RealFace", (byte)this.facingDirection.getIndex());
        tagCompound.putByte("Rotation", (byte)this.getRotation());

        Picture picture = PICTURES.get(canvasName);
        if(picture != null){
            tagCompound.putIntArray("pixels", picture.pixels);
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeString(canvasName);
        buffer.writeInt(canvasVersion);
        buffer.writeInt(facingDirection.getIndex());
        buffer.writeByte(canvasType.ordinal());
        buffer.writeBlockPos(hangingPosition); // this has to be written, otherwise pos gets broken
        buffer.writeByte((byte)this.getRotation());
//        XercaPaint.LOGGER.debug("writeSpawnData Pos: " + this.hangingPosition.toString() + " posY: " + this.posY);
    }

    private void requestPicture(){
        if(!PICTURE_REQUESTS.contains(canvasName)){
            PICTURE_REQUESTS.add(canvasName);
            PictureRequestPacket pack = new PictureRequestPacket(canvasName);
            XercaPaint.NETWORK_HANDLER.sendToServer(pack);
        }
    }

    @Override
    public void readSpawnData(PacketBuffer buffer) {
        canvasName = buffer.readString();
        canvasVersion = buffer.readInt();

        Picture picture = PICTURES.get(canvasName);
        if(picture == null || picture.version < canvasVersion){
            requestPicture();
        }
        facingDirection = Direction.byIndex(buffer.readInt());
        canvasType = CanvasType.fromByte(buffer.readByte());
        hangingPosition = buffer.readBlockPos();
        setRotation(buffer.readByte());

        updateFacingWithBoundingBox(facingDirection);
//        XercaPaint.LOGGER.debug("readSpawnData Pos: " + this.hangingPosition.toString() + " posY: " + this.posY);
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if(canvasType == CanvasType.SMALL || canvasType == CanvasType.LARGE){
            if (!this.world.isRemote) {
                setRotation(getRotation() + 1);
            }
            return ActionResultType.SUCCESS;
        }
        else{
            return ActionResultType.PASS;
        }
    }

    public static class Picture{
        public int version;
        public int[] pixels;

        public Picture(int version, int[] pixels){
            this.version = version;
            this.pixels = pixels;
        }
    }
}
