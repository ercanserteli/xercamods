package xerca.xercapaint.common.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.packets.PictureRequestPacket;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.entity.Entity.RemovalReason;

public class EntityCanvas extends HangingEntity implements IEntityAdditionalSpawnData {
    private String canvasName;
    private String canvasTitle;
    private String canvasAuthor;
    private int canvasVersion;
    private int canvasGeneration = 0;
    private boolean canvasSigned;
    private int tickCounter1 = 0;
    private CanvasType canvasType;
    private static final EntityDataAccessor<Integer> ROTATION = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.INT);
    public static final Map<String, Picture> PICTURES = Maps.newHashMap();
    public static final Set<String> PICTURE_REQUESTS = Sets.newHashSet();

    public EntityCanvas(Level world, CompoundTag canvasNBT, BlockPos pos, Direction facing, CanvasType canvasType, int rotation) {
        super(Entities.CANVAS.get(), world, pos);
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

    public EntityCanvas(EntityType<EntityCanvas> entityCanvasEntityType, Level world) {
        super(entityCanvasEntityType, world);
    }

    public EntityCanvas(PlayMessages.SpawnEntity ignoredSpawnEntity, Level world) {
        super(Entities.CANVAS.get(), world);
    }

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
    protected float getEyeHeight(@NotNull Pose poseIn, @NotNull EntityDimensions sizeIn) {
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
            if (brokenEntity instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    return;
                }
            }
            ItemStack canvasItem;
            if(canvasType == CanvasType.SMALL){
                canvasItem = new ItemStack(Items.ITEM_CANVAS.get());
            }
            else if(canvasType == CanvasType.LARGE){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_LARGE.get());
            }
            else if(canvasType == CanvasType.LONG){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_LONG.get());
            }
            else if(canvasType == CanvasType.TALL){
                canvasItem = new ItemStack(Items.ITEM_CANVAS_TALL.get());
            }else{
                XercaPaint.LOGGER.error("Invalid canvas type");
                return;
            }
            CompoundTag nbt = new CompoundTag();
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
                this.remove(RemovalReason.DISCARDED);
                this.dropItem(null);
            }
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void setDirection(@NotNull Direction facingDirectionIn) {
        Validate.notNull(facingDirectionIn);
        this.direction = facingDirectionIn;
        if (facingDirectionIn.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((float)(this.direction.get2DDataValue() * 90));
        } else {
            this.setXRot((float)(-90 * facingDirectionIn.getAxisDirection().getStep()));
            this.setYRot(0.0F);
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }

    private double offs(int l) {
        return l % 32 == 0 ? 0.5D : 0.0D;
    }

    @Override
    protected void recalculateBoundingBox(){
        if(canvasType != null){
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
            }

            this.setPosRaw(d1, d2, d3);
            double d6 = this.getWidth()-2;
            double d7 = this.getHeight()-2;
            double d8 = this.getWidth()-2;
            Direction.Axis direction$axis = this.direction.getAxis();
            switch (direction$axis) {
                case X -> d6 = 1.0D;
                case Y -> d7 = 1.0D;
                case Z -> d8 = 1.0D;
            }

            d6 = d6 / 32.0D;
            d7 = d7 / 32.0D;
            d8 = d8 / 32.0D;
            this.setBoundingBox(new AABB(d1 - d6, d2 - d7, d3 - d8, d1 + d6, d2 + d7, d3 + d8));
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
            return (blockstate.getMaterial().isSolid() ||
                    this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(blockstate)) && this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
        }
    }

    public int getRotation() {
        return this.getEntityData().get(ROTATION);
    }

    private void setRotation(int rotation) {
        this.getEntityData().set(ROTATION, rotation % 4);
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tagCompound) {
        this.pos = new BlockPos(tagCompound.getInt("TileX"), tagCompound.getInt("TileY"), tagCompound.getInt("TileZ"));
        CompoundTag canvasNBT = tagCompound;
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
    public void addAdditionalSaveData(CompoundTag tagCompound) {
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
    public void writeSpawnData(FriendlyByteBuf buffer) {
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
    public void readSpawnData(FriendlyByteBuf buffer) {
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

    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        if(canvasType == CanvasType.SMALL || canvasType == CanvasType.LARGE){
            if (!this.level.isClientSide) {
                setRotation(getRotation() + 1);
            }
            return InteractionResult.SUCCESS;
        }
        else{
            return InteractionResult.PASS;
        }
    }

    public static class Picture{
        public final int version;
        public final int[] pixels;

        public Picture(int version, int[] pixels){
            this.version = version;
            this.pixels = pixels;
        }
    }
}
