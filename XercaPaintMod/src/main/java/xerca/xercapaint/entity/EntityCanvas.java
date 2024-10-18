package xerca.xercapaint.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.Mod;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.PictureRequestPacket;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class EntityCanvas extends HangingEntity {
    private String canvasTitle;
    private String canvasAuthor;
    private int canvasGeneration = 0;
    private boolean canvasSigned;
    private int tickCounter1 = 0;
    private static final EntityDataAccessor<String> CANVAS_ID = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> CANVAS_VERSION = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> CANVAS_TYPE_KEY = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> CANVAS_ROTATION = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.BYTE);
    public static final Map<String, Picture> PICTURES = Maps.newHashMap();
    public static final Set<String> PICTURE_REQUESTS = Sets.newHashSet();

    public EntityCanvas(Level world, ItemStack stack, BlockPos pos, Direction facing, CanvasType canvasType, int rotation) {
        super(Entities.CANVAS, world, pos);
        String id = stack.get(Items.CANVAS_ID);
        int version = stack.getOrDefault(Items.CANVAS_VERSION, 0);
        String title = stack.get(Items.CANVAS_TITLE);
        String author = stack.get(Items.CANVAS_AUTHOR);
        this.setCanvasName(id);
        this.setVersion(version);
        if(title != null && author != null){
            this.canvasSigned = true;
            this.canvasTitle = title;
            this.canvasAuthor = author;
            this.canvasGeneration = stack.getOrDefault(Items.CANVAS_GENERATION, 0);
        }else{
            this.canvasSigned = false;
        }
        this.setCanvasType(canvasType);
        this.setRotation(rotation);
        this.setDirection(facing);

        Picture picture = PICTURES.get(id);
        if(picture == null || picture.version < version){
            int[] pixels = null;
            List<Integer> pixelList = stack.get(Items.CANVAS_PIXELS);
            if (pixelList != null) {
                pixels = pixelList.stream().mapToInt(i->i).toArray();
            }
            PICTURES.put(id, new Picture(version, pixels));
        }
    }

    public EntityCanvas(EntityType<? extends HangingEntity> entityCanvasEntityType, Level world) {
        super(entityCanvasEntityType, world);

        Picture picture = PICTURES.get(getCanvasID());
        if(world.isClientSide && (picture == null || picture.version < getVersion())){
            requestPicture();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(CANVAS_ID, "");
        builder.define(CANVAS_VERSION, 0);
        builder.define(CANVAS_TYPE_KEY, (byte)0);
        builder.define(CANVAS_ROTATION, (byte)0);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        if (CANVAS_TYPE_KEY.equals(key)) {
            this.recalculateBoundingBox();
        }
    }

    public int getWidth() {
        return CanvasType.getWidth(getCanvasType());
    }

    public int getHeight() {
        return CanvasType.getHeight(getCanvasType());
    }

    @Override
    public void dropItem(@Nullable Entity brokenEntity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof Player playerentity) {
                if (playerentity.getAbilities().instabuild) {
                    return;
                }
            }
            ItemStack canvasItem;
            CanvasType canvasType = getCanvasType();
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
                Mod.LOGGER.error("Invalid canvas type");
                return;
            }

            canvasItem.set(Items.CANVAS_ID, getCanvasID());
            canvasItem.set(Items.CANVAS_VERSION, getVersion());
            if (canvasSigned) {
                canvasItem.set(Items.CANVAS_AUTHOR, canvasAuthor);
                canvasItem.set(Items.CANVAS_TITLE, canvasTitle);
                canvasItem.set(Items.CANVAS_GENERATION, canvasGeneration);
            }
            Picture picture = PICTURES.get(getCanvasID());
            if(picture != null){
                canvasItem.set(Items.CANVAS_PIXELS, Arrays.stream(picture.pixels).boxed().toList());
            }
            this.spawnAtLocation(canvasItem);
        }
    }

    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (this.tickCounter1++ == 50 && !this.level().isClientSide) {
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
    public void moveTo(double x, double y, double z, float yRot, float xRot) {
        this.setPos(x, y, z);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.setPos(x, y, z);
    }

    @Override
    protected @NotNull AABB calculateBoundingBox(@NotNull BlockPos pos, @NotNull Direction direction) {
        double d1 = (double)pos.getX() + 0.5D - (double)direction.getStepX() * 0.46875D;
        double d2 = (double)pos.getY() + 0.5D - (double)direction.getStepY() * 0.46875D;
        double d3 = (double)pos.getZ() + 0.5D - (double)direction.getStepZ() * 0.46875D;

        if(direction.getAxis().isHorizontal()){
            double d4 = this.offs(this.getWidth());
            double d5 = this.offs(this.getHeight());
            d2 = d2 + d5;
            Direction ccwDirection = direction.getCounterClockWise();
            d1 = d1 + d4 * (double)ccwDirection.getStepX();
            d3 = d3 + d4 * (double)ccwDirection.getStepZ();
        }

        double d6 = this.getWidth();
        double d7 = this.getHeight();
        double d8 = this.getWidth();
        Direction.Axis direction$axis = direction.getAxis();
        switch (direction$axis) {
            case X -> d6 = 1.0D;
            case Y -> d7 = 1.0D;
            case Z -> d8 = 1.0D;
        }

        d6 = d6 / 32.0D;
        d7 = d7 / 32.0D;
        d8 = d8 / 32.0D;
        return new AABB(d1 - d6, d2 - d7, d3 - d8, d1 + d6, d2 + d7, d3 + d8);
    }

    @Override
    public boolean survives() {
        if(direction.getAxis().isHorizontal()){
            return super.survives();
        }
        if (!this.level().noCollision(this)) {
            return false;
        } else {
            BlockState blockstate = this.level().getBlockState(this.pos.relative(this.direction.getOpposite()));
            return (blockstate.isSolid() ||
                    this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(blockstate)) && this.level().getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
        }
    }

    public int getRotation() {
        return this.getEntityData().get(CANVAS_ROTATION);
    }

    private void setRotation(int rotation) {
        this.getEntityData().set(CANVAS_ROTATION, (byte)(rotation % 4));
    }

    public String getCanvasID() {
        return this.getEntityData().get(CANVAS_ID);
    }

    private void setCanvasName(String canvasID) {
        this.getEntityData().set(CANVAS_ID, canvasID);
    }

    public int getVersion() {
        return this.getEntityData().get(CANVAS_VERSION);
    }

    private void setVersion(int version) {
        this.getEntityData().set(CANVAS_VERSION, version);
    }

    public CanvasType getCanvasType() {
        return CanvasType.fromByte(this.getEntityData().get(CANVAS_TYPE_KEY));
    }

    public byte getCanvasTypeKey() {
        return this.getEntityData().get(CANVAS_TYPE_KEY);
    }

    private void setCanvasType(CanvasType canvasType) {
        this.getEntityData().set(CANVAS_TYPE_KEY, (byte)canvasType.ordinal());
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(@NotNull ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tagCompound) {
        this.pos = new BlockPos(tagCompound.getInt("TileX"), tagCompound.getInt("TileY"), tagCompound.getInt("TileZ"));
        CompoundTag canvasNBT = tagCompound;
        if(tagCompound.contains("canvas")){
            canvasNBT = tagCompound.getCompound("canvas");
        }
        this.canvasSigned = canvasNBT.contains("author") && canvasNBT.contains("title");
        String canvasId = canvasNBT.getString("name");
        this.setCanvasName(canvasId);
        int version = canvasNBT.getInt("v");
        this.setVersion(version);
        if(canvasSigned)
        {
            this.canvasAuthor = canvasNBT.getString("author");
            this.canvasTitle = canvasNBT.getString("title");
            this.canvasGeneration = canvasNBT.getInt("generation");
        }

        Picture picture = PICTURES.get(canvasId);
        if(picture == null || picture.version < version){
            PICTURES.put(canvasId, new Picture(version, canvasNBT.getIntArray("pixels")));
        }

        CanvasType canvasType = CanvasType.fromByte(tagCompound.getByte("ctype"));
        if (canvasType == null) {
            Mod.LOGGER.error("EntityCanvas invalid ctype in readAdditionalSaveData");
            this.kill();
            return;
        }
        this.setCanvasType(canvasType);
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
        tagCompound.putString("name", getCanvasID());
        tagCompound.putInt("v", getVersion());
        if(canvasSigned){
            tagCompound.putString("author", canvasAuthor);
            tagCompound.putString("title", canvasTitle);
            tagCompound.putInt("generation", canvasGeneration);
        }
        tagCompound.putByte("ctype", getCanvasTypeKey());
        tagCompound.putByte("RealFace", (byte)this.direction.get3DDataValue());
        tagCompound.putByte("Rotation", (byte)this.getRotation());

        Picture picture = PICTURES.get(getCanvasID());
        if(picture != null){
            tagCompound.putIntArray("pixels", picture.pixels);
        }
    }

    private void requestPicture(){
        String canvasID = this.getCanvasID();
        if(!PICTURE_REQUESTS.contains(canvasID)){
            PICTURE_REQUESTS.add(canvasID);
            ClientPlayNetworking.send(new PictureRequestPacket(canvasID));
        }
    }

    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        CanvasType canvasType = this.getCanvasType();
        if(canvasType == CanvasType.SMALL || canvasType == CanvasType.LARGE){
            if (!this.level().isClientSide) {
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
