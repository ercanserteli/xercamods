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
    private static final DataParameter<Integer> ROTATION = EntityDataManager.defineId(EntityCanvas.class, DataSerializers.INT);
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

        this.setDirection(facing);

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

    protected void defineSynchedData() {
        this.getEntityData().define(ROTATION, 0);
    }

    @Override
    public int getWidth() {
        return CanvasType.getWidth(canvasType);
    }

    @Override
    public int getHeight() {
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
    public void dropItem(@Nullable Entity brokenEntity) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity)brokenEntity;
                if (playerentity.abilities.instabuild) {
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
            this.spawnAtLocation(canvasItem);
        }
    }

    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.tickCounter1++ == 50 && !this.level.isClientSide) {
            this.tickCounter1 = 0;
            if (this.isAlive() && !this.survives()) {
                this.remove();
                this.dropItem(null);
            }
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void setDirection(Direction facingDirectionIn) {
        Validate.notNull(facingDirectionIn);
        this.direction = facingDirectionIn;
        if (facingDirectionIn.getAxis().isHorizontal()) {
            this.xRot = 0.0F;
            this.yRot = (float)(this.direction.get2DDataValue() * 90);
        } else {
            this.xRot = (float)(-90 * facingDirectionIn.getAxisDirection().getStep());
            this.yRot = 0.0F;
        }

        this.xRotO = this.xRot;
        this.yRotO = this.yRot;
        this.recalculateBoundingBox();
    }

    private double offs(int l) {
        return l % 32 == 0 ? 0.5D : 0.0D;
    }

    @Override
    protected void recalculateBoundingBox(){
        if(canvasType != null){
            if (this.direction != null) {
                double d1 = (double)this.pos.getX() + 0.5D - (double)this.direction.getStepX() * 0.46875D;
                double d2 = (double)this.pos.getY() + 0.5D - (double)this.direction.getStepY() * 0.46875D;
                double d3 = (double)this.pos.getZ() + 0.5D - (double)this.direction.getStepZ() * 0.46875D;

                if(this.direction.getAxis().isHorizontal()){
                    double d4 = this.offs(this.getWidth());
                    double d5 = this.offs(this.getHeight());
                    d2 = d2 + d5;
                    Direction direction = this.direction.getCounterClockWise();
                    d1 = d1 + d4 * (double)direction.getStepX();
                    d3 = d3 + d4 * (double)direction.getStepZ();
                }else{

                }

                this.setPosRaw(d1, d2, d3);
                double d6 = this.getWidth()-2;
                double d7 = this.getHeight()-2;
                double d8 = this.getWidth()-2;
                Direction.Axis direction$axis = this.direction.getAxis();
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
    public boolean survives() {
        if(direction.getAxis().isHorizontal()){
            return super.survives();
        }
        if (!this.level.noCollision(this)) {
            return false;
        } else {
            BlockState blockstate = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
            return blockstate.getMaterial().isSolid() ||
                    this.direction.getAxis().isHorizontal() && RedstoneDiodeBlock.isDiode(blockstate) ? this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty() : false;
        }
    }

    public int getRotation() {
        return this.getEntityData().get(ROTATION);
    }

    private void setRotation(int rotation) {
        this.getEntityData().set(ROTATION, rotation % 4);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tagCompound) {
        this.pos = new BlockPos(tagCompound.getInt("TileX"), tagCompound.getInt("TileY"), tagCompound.getInt("TileZ"));
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
            int facing = tagCompound.getByte("Facing");
            Direction horizontal = Direction.from2DDataValue(facing);
            this.setDirection(horizontal);
        }
        else{
            this.setDirection(Direction.from3DDataValue(tagCompound.getByte("RealFace")));
        }
        this.setRotation(tagCompound.getByte("Rotation"));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tagCompound) {
        BlockPos blockpos = this.getPos();
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
        tagCompound.putByte("RealFace", (byte)this.direction.get3DDataValue());
        tagCompound.putByte("Rotation", (byte)this.getRotation());

        Picture picture = PICTURES.get(canvasName);
        if(picture != null){
            tagCompound.putIntArray("pixels", picture.pixels);
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeUtf(canvasName);
        buffer.writeInt(canvasVersion);
        buffer.writeInt(direction.get3DDataValue());
        buffer.writeByte(canvasType.ordinal());
        buffer.writeBlockPos(pos); // this has to be written, otherwise pos gets broken
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
        canvasName = buffer.readUtf();
        canvasVersion = buffer.readInt();

        Picture picture = PICTURES.get(canvasName);
        if(picture == null || picture.version < canvasVersion){
            requestPicture();
        }
        direction = Direction.from3DDataValue(buffer.readInt());
        canvasType = CanvasType.fromByte(buffer.readByte());
        pos = buffer.readBlockPos();
        setRotation(buffer.readByte());

        setDirection(direction);
//        XercaPaint.LOGGER.debug("readSpawnData Pos: " + this.hangingPosition.toString() + " posY: " + this.posY);
    }

    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if(canvasType == CanvasType.SMALL || canvasType == CanvasType.LARGE){
            if (!this.level.isClientSide) {
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
