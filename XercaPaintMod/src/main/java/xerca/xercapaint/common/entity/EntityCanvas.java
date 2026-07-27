package xerca.xercapaint.common.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.packets.PictureRequestPacket;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EntityCanvas extends HangingEntity implements IEntityAdditionalSpawnData {
    private static final int[] NO_PIXELS = new int[0];

    private String canvasName = "";
    @Nullable
    private String canvasTitle;
    @Nullable
    private String canvasAuthor;
    private int canvasVersion;
    private int canvasGeneration;
    private boolean canvasSigned;
    private int tickCounter1;
    private CanvasType canvasType = CanvasType.SMALL;
    private boolean glass;
    private static final EntityDataAccessor<Integer> ROTATION = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.INT);
    public static final Map<String, Picture> PICTURES = Maps.newHashMap();
    private static final Set<String> PICTURE_REQUESTS = Sets.newHashSet();

    public EntityCanvas(Level world, CompoundTag canvasNBT, BlockPos pos, Direction facing, CanvasType canvasType, boolean glass, int rotation) {
        super(Entities.CANVAS.get(), world, pos);
        this.canvasName = canvasNBT.getString(ItemCanvas.TAG_CANVAS_ID);
        this.canvasVersion = canvasNBT.getInt(ItemCanvas.TAG_VERSION);
        if (canvasNBT.contains(ItemCanvas.TAG_TITLE) && canvasNBT.contains(ItemCanvas.TAG_AUTHOR)) {
            this.canvasSigned = true;
            this.canvasTitle = canvasNBT.getString(ItemCanvas.TAG_TITLE);
            this.canvasAuthor = canvasNBT.getString(ItemCanvas.TAG_AUTHOR);
            this.canvasGeneration = canvasNBT.getInt(ItemCanvas.TAG_GENERATION);
        } else {
            this.canvasSigned = false;
        }
        this.canvasType = canvasType;
        this.glass = glass;
        this.setRotation(rotation);

        this.setDirection(facing);

        Picture picture = PICTURES.get(canvasName);
        if (picture == null || picture.version < canvasVersion) {
            PICTURES.put(canvasName, new Picture(canvasVersion, canvasNBT.getIntArray(ItemCanvas.TAG_PIXELS),
                    canvasNBT.getBoolean(ItemCanvas.TAG_SIDES_ACTIVE), canvasNBT.getIntArray(ItemCanvas.TAG_SIDE_PIXELS)));
        }
    }

    public EntityCanvas(EntityType<? extends HangingEntity> entityCanvasEntityType, Level world) {
        super(entityCanvasEntityType, world);
    }

    public EntityCanvas(PlayMessages.SpawnEntity ignoredSpawnEntity, Level world) {
        super(Entities.CANVAS.get(), world);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(ROTATION, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntityCanvas other)) {
            return false;
        }
        return Objects.equals(this.getUUID(), other.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(EntityCanvas.class, this.getUUID());
    }

    public static boolean isPictureRequested(String canvasId) {
        return PICTURE_REQUESTS.contains(canvasId);
    }

    public static void markPictureRequested(String canvasId) {
        PICTURE_REQUESTS.add(canvasId);
    }

    public static void clearPictureRequest(String canvasId) {
        PICTURE_REQUESTS.remove(canvasId);
    }

    public CanvasType getCanvasType() {
        return canvasType;
    }

    public boolean isGlass() {
        return glass;
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
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(glass ? SoundEvents.GLASS_BREAK : SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof Player player && player.getAbilities().instabuild) {
                return;
            }

            ItemStack canvasItem = new ItemStack(ItemCanvas.canvasItemFor(canvasType, glass));
            CompoundTag nbt = new CompoundTag();
            nbt.putString(ItemCanvas.TAG_CANVAS_ID, canvasName);
            nbt.putInt(ItemCanvas.TAG_VERSION, canvasVersion);
            nbt.putInt(ItemCanvas.TAG_GENERATION, 0);
            if (canvasSigned && canvasAuthor != null && canvasTitle != null) {
                nbt.putString(ItemCanvas.TAG_AUTHOR, canvasAuthor);
                nbt.putString(ItemCanvas.TAG_TITLE, canvasTitle);
                nbt.putInt(ItemCanvas.TAG_GENERATION, canvasGeneration);
            }
            Picture picture = PICTURES.get(canvasName);
            if (picture != null) {
                nbt.putIntArray(ItemCanvas.TAG_PIXELS, picture.pixels());
                if (picture.sidePixels().length > 0) {
                    nbt.putBoolean(ItemCanvas.TAG_SIDES_ACTIVE, picture.sidesActive());
                    nbt.putIntArray(ItemCanvas.TAG_SIDE_PIXELS, picture.sidePixels());
                }
            }

            canvasItem.setTag(nbt);
            this.spawnAtLocation(canvasItem);
        }
    }

    @Override
    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        boolean shouldCheckSurvival = this.tickCounter1 == 50;
        this.tickCounter1++;
        if (shouldCheckSurvival && !this.level().isClientSide) {
            this.tickCounter1 = 0;
            if (this.isAlive() && !this.survives()) {
                this.remove(RemovalReason.DISCARDED);
                this.dropItem(null);
            }
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(glass ? SoundEvents.GLASS_PLACE : SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void setDirection(@NotNull Direction facingDirectionIn) {
        this.direction = facingDirectionIn;
        if (facingDirectionIn.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((this.direction.get2DDataValue() * 90));
        } else {
            this.setXRot((-90 * facingDirectionIn.getAxisDirection().getStep()));
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
    protected void recalculateBoundingBox() {
        if (this.direction == null) {
            return;
        }
        AABB boundingBox = this.calculateBoundingBox(this.pos, this.direction);
        Vec3 center = boundingBox.getCenter();
        this.setPosRaw(center.x, center.y, center.z);
        this.setBoundingBox(boundingBox);
    }

    protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
        double d1 = pos.getX() + 0.5D - direction.getStepX() * 0.46875D;
        double d2 = pos.getY() + 0.5D - direction.getStepY() * 0.46875D;
        double d3 = pos.getZ() + 0.5D - direction.getStepZ() * 0.46875D;

        if (direction.getAxis().isHorizontal()) {
            double d4 = this.offs(this.getWidth());
            double d5 = this.offs(this.getHeight());
            d2 = d2 + d5;
            Direction ccwDirection = direction.getCounterClockWise();
            d1 = d1 + d4 * ccwDirection.getStepX();
            d3 = d3 + d4 * ccwDirection.getStepZ();
        }

        double d6 = this.getWidth();
        double d7 = this.getHeight();
        double d8 = this.getWidth();
        Direction.Axis axis = direction.getAxis();
        switch (axis) {
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
        if (direction.getAxis().isHorizontal()) {
            return super.survives();
        }
        Level level = this.level();
        if (!level.noCollision(this)) {
            return false;
        } else {
            BlockPos supportPos = this.pos.relative(this.direction.getOpposite());
            BlockState state = level.getBlockState(supportPos);
            return (state.isFaceSturdy(level, supportPos, this.direction) ||
                    (this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(state)))
                    && level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
        }
    }

    @Override
    public @NotNull Vec3 getLightProbePosition(float partialTick) {
        return Vec3.atCenterOf(this.pos);
    }

    public int getRotation() {
        return this.getEntityData().get(ROTATION);
    }

    private void setRotation(int rotation) {
        this.getEntityData().set(ROTATION, rotation % 4);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tagCompound) {
        this.pos = new BlockPos(tagCompound.getInt("TileX"), tagCompound.getInt("TileY"), tagCompound.getInt("TileZ"));
        CompoundTag canvasNBT = tagCompound;
        if (tagCompound.contains("canvas")) {
            canvasNBT = tagCompound.getCompound("canvas");
        }
        this.canvasSigned = canvasNBT.contains(ItemCanvas.TAG_AUTHOR) && canvasNBT.contains(ItemCanvas.TAG_TITLE);
        this.canvasName = canvasNBT.getString(ItemCanvas.TAG_CANVAS_ID);
        this.canvasVersion = canvasNBT.getInt(ItemCanvas.TAG_VERSION);
        if (canvasSigned) {
            this.canvasAuthor = canvasNBT.getString(ItemCanvas.TAG_AUTHOR);
            this.canvasTitle = canvasNBT.getString(ItemCanvas.TAG_TITLE);
            this.canvasGeneration = canvasNBT.getInt(ItemCanvas.TAG_GENERATION);
        }

        Picture picture = PICTURES.get(canvasName);
        if (picture == null || picture.version < canvasVersion) {
            PICTURES.put(canvasName, new Picture(canvasVersion, canvasNBT.getIntArray(ItemCanvas.TAG_PIXELS),
                    canvasNBT.getBoolean(ItemCanvas.TAG_SIDES_ACTIVE), canvasNBT.getIntArray(ItemCanvas.TAG_SIDE_PIXELS)));
        }

        this.canvasType = CanvasType.fromByte(tagCompound.getByte("ctype"));
        this.glass = tagCompound.getBoolean("glass");
        if (tagCompound.contains("Facing") && !tagCompound.contains("RealFace")) {
            int facing = tagCompound.getByte("Facing");
            Direction horizontal = Direction.from2DDataValue(facing);
            this.setDirection(horizontal);
        } else {
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
        tagCompound.putString(ItemCanvas.TAG_CANVAS_ID, canvasName);
        tagCompound.putInt(ItemCanvas.TAG_VERSION, canvasVersion);
        if (canvasSigned && canvasAuthor != null && canvasTitle != null) {
            tagCompound.putString(ItemCanvas.TAG_AUTHOR, canvasAuthor);
            tagCompound.putString(ItemCanvas.TAG_TITLE, canvasTitle);
            tagCompound.putInt(ItemCanvas.TAG_GENERATION, canvasGeneration);
        }
        tagCompound.putByte("ctype", canvasType.toByte());
        tagCompound.putBoolean("glass", glass);
        tagCompound.putByte("RealFace", (byte) this.direction.get3DDataValue());
        tagCompound.putByte("Rotation", (byte) this.getRotation());

        Picture picture = PICTURES.get(canvasName);
        if (picture != null) {
            tagCompound.putIntArray(ItemCanvas.TAG_PIXELS, picture.pixels());
            if (picture.sidePixels().length > 0) {
                tagCompound.putBoolean(ItemCanvas.TAG_SIDES_ACTIVE, picture.sidesActive());
                tagCompound.putIntArray(ItemCanvas.TAG_SIDE_PIXELS, picture.sidePixels());
            }
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeUtf(canvasName);
        buffer.writeInt(canvasVersion);
        buffer.writeInt(direction.get3DDataValue());
        buffer.writeByte(canvasType.toByte());
        buffer.writeBoolean(glass);
        buffer.writeBlockPos(pos); // this has to be written, otherwise pos gets broken
        buffer.writeByte((byte) this.getRotation());
    }

    private void requestPicture() {
        if (!isPictureRequested(canvasName)) {
            markPictureRequested(canvasName);
            PictureRequestPacket pack = new PictureRequestPacket(canvasName);
            XercaPaint.NETWORK_HANDLER.sendToServer(pack);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        canvasName = buffer.readUtf();
        canvasVersion = buffer.readInt();

        Picture picture = PICTURES.get(canvasName);
        if (picture == null || picture.version < canvasVersion) {
            requestPicture();
        }
        direction = Direction.from3DDataValue(buffer.readInt());
        canvasType = CanvasType.fromByte(buffer.readByte());
        glass = buffer.readBoolean();
        pos = buffer.readBlockPos();
        setRotation(buffer.readByte());

        setDirection(direction);
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        if (canvasType == CanvasType.SMALL || canvasType == CanvasType.LARGE) {
            if (!this.level().isClientSide) {
                setRotation(getRotation() + 1);
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public record Picture(int version, int[] pixels, boolean sidesActive, int[] sidePixels) {
        public Picture {
            pixels = pixels == null ? NO_PIXELS : pixels.clone();
            sidePixels = sidePixels == null ? NO_PIXELS : sidePixels.clone();
        }

        @Override
        public int[] pixels() {
            return pixels.clone();
        }

        @Override
        public int[] sidePixels() {
            return sidePixels.clone();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Picture other)) {
                return false;
            }
            return version == other.version && sidesActive == other.sidesActive
                    && Arrays.equals(pixels, other.pixels)
                    && Arrays.equals(sidePixels, other.sidePixels);
        }

        @Override
        public int hashCode() {
            int result = Integer.hashCode(version);
            result = 31 * result + Arrays.hashCode(pixels);
            result = 31 * result + Boolean.hashCode(sidesActive);
            result = 31 * result + Arrays.hashCode(sidePixels);
            return result;
        }

        @Override
        public @NotNull String toString() {
            return "Picture{" +
                    "version=" + version +
                    ", pixels=" + Arrays.toString(pixels) +
                    ", sidesActive=" + sidesActive +
                    ", sidePixels=" + Arrays.toString(sidePixels) +
                    '}';
        }
    }
}
