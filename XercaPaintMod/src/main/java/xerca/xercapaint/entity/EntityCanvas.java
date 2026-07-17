package xerca.xercapaint.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.PictureRequestPacket;

import java.util.*;


public class EntityCanvas extends HangingEntity {
    @Nullable
    private String canvasTitle;
    @Nullable
    private String canvasAuthor;
    private int canvasGeneration;
    private boolean canvasSigned;
    private int tickCounter1;
    private static final EntityDataAccessor<String> CANVAS_ID = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> CANVAS_VERSION = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> CANVAS_TYPE_KEY = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> CANVAS_ROTATION = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> CANVAS_GLASS = SynchedEntityData.defineId(EntityCanvas.class, EntityDataSerializers.BOOLEAN);
    public static final Map<String, Picture> PICTURES = Maps.newHashMap();
    private static final Set<String> PICTURE_REQUESTS = Sets.newHashSet();

    public EntityCanvas(Level world, ItemStack stack, BlockPos pos, Direction facing, CanvasType canvasType, int rotation) {
        super(Entities.CANVAS, world, pos);
        String id = Objects.requireNonNullElse(stack.get(Items.CANVAS_ID), "");
        int version = stack.getOrDefault(Items.CANVAS_VERSION, 0);
        String title = stack.get(Items.CANVAS_TITLE);
        String author = stack.get(Items.CANVAS_AUTHOR);
        this.setCanvasID(id);
        this.setVersion(version);
        if (title != null && author != null) {
            this.canvasSigned = true;
            this.canvasTitle = title;
            this.canvasAuthor = author;
            this.canvasGeneration = stack.getOrDefault(Items.CANVAS_GENERATION, 0);
        } else {
            this.canvasSigned = false;
        }
        this.setCanvasType(canvasType);
        this.setGlass(stack.getItem() instanceof ItemCanvas itemCanvas && itemCanvas.isGlass());
        this.setRotation(rotation);
        this.setDirection(facing);

        Picture picture = PICTURES.get(id);
        if (picture == null || picture.version < version) {
            int[] pixels = new int[0];
            List<Integer> pixelList = stack.get(Items.CANVAS_PIXELS);
            if (pixelList != null) {
                pixels = pixelList.stream().mapToInt(i -> i).toArray();
            }
            boolean sidesActive = stack.getOrDefault(Items.CANVAS_SIDES_ACTIVE, false);
            int[] sidePixels = new int[0];
            List<Integer> sideList = stack.get(Items.CANVAS_SIDE_PIXELS);
            if (sideList != null) {
                sidePixels = sideList.stream().mapToInt(i -> i).toArray();
            }
            PICTURES.put(id, new Picture(version, pixels, sidesActive, sidePixels));
        }
    }

    public EntityCanvas(EntityType<EntityCanvas> entityCanvasEntityType, Level level) {
        super(entityCanvasEntityType, level);
        clientPictureInit(level);
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

    private void clientPictureInit(Level level) {
        if (!level.isClientSide()) {
            return;
        }

        String canvasID = getCanvasID();
        int version = getVersion();
        if (!canvasID.isEmpty() && version > 0) {
            Picture picture = PICTURES.get(getCanvasID());
            if ((picture == null || picture.version < getVersion()) && !isPictureRequested(canvasID)) {
                markPictureRequested(canvasID);
                ClientPlayNetworking.send(new PictureRequestPacket(canvasID));
            }
        }
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

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CANVAS_ID, "");
        builder.define(CANVAS_VERSION, 0);
        builder.define(CANVAS_TYPE_KEY, (byte) 0);
        builder.define(CANVAS_ROTATION, (byte) 0);
        builder.define(CANVAS_GLASS, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (CANVAS_TYPE_KEY.equals(key)) {
            this.recalculateBoundingBox();
        } else if (CANVAS_ID.equals(key) || CANVAS_VERSION.equals(key)) {
            clientPictureInit(this.level());
        }
    }

    public int getWidth() {
        return CanvasType.getWidth(requireCanvasType());
    }

    public int getHeight() {
        return CanvasType.getHeight(requireCanvasType());
    }

    @Override
    public void dropItem(ServerLevel serverLevel, @Nullable Entity brokenEntity) {
        if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(isGlass() ? SoundEvents.GLASS_BREAK : SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof Player playerEntity && playerEntity.getAbilities().instabuild) {
                return;
            }
            CanvasType canvasType = requireCanvasType();
            ItemStack canvasItem = new ItemStack(ItemCanvas.canvasItemFor(canvasType, isGlass()));

            canvasItem.set(Items.CANVAS_ID, getCanvasID());
            canvasItem.set(Items.CANVAS_VERSION, getVersion());
            if (canvasSigned) {
                canvasItem.set(Items.CANVAS_AUTHOR, canvasAuthor);
                canvasItem.set(Items.CANVAS_TITLE, canvasTitle);
                canvasItem.set(Items.CANVAS_GENERATION, canvasGeneration);
            }
            ItemCanvas.updateStackSize(canvasItem);
            Picture picture = PICTURES.get(getCanvasID());
            if (picture != null) {
                canvasItem.set(Items.CANVAS_PIXELS, Arrays.stream(picture.pixels).boxed().toList());
                if (picture.sidePixels().length > 0) {
                    canvasItem.set(Items.CANVAS_SIDES_ACTIVE, picture.sidesActive());
                    canvasItem.set(Items.CANVAS_SIDE_PIXELS, Arrays.stream(picture.sidePixels()).boxed().toList());
                }
            }
            this.spawnAtLocation(serverLevel, canvasItem);
        }
    }

    @Override
    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        boolean shouldCheckSurvival = this.tickCounter1 == 50;
        this.tickCounter1++;
        if (shouldCheckSurvival && this.level() instanceof ServerLevel serverLevel) {
            this.tickCounter1 = 0;
            if (this.isAlive() && !this.survives()) {
                this.remove(RemovalReason.DISCARDED);
                this.dropItem(serverLevel, null);
            }
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(isGlass() ? SoundEvents.GLASS_PLACE : SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void setDirection(Direction facingDirectionIn) {
        this.setDirectionRaw(facingDirectionIn);
        if (facingDirectionIn.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((facingDirectionIn.get2DDataValue() * 90));
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
    public void snapTo(double x, double y, double z, float yRot, float xRot) {
        this.setPos(x, y, z);
    }

    @Override
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
        Direction direction = this.getDirection();
        if (direction.getAxis().isHorizontal()) {
            return super.survives();
        }
        Level level = this.level();
        if (!level.noCollision(this)) {
            return false;
        } else {
            BlockPos supportPos = this.pos.relative(direction.getOpposite());
            BlockState state = level.getBlockState(supportPos);
            return state.isFaceSturdy(level, supportPos, direction)
                    && level.getEntities(this, this.getBoundingBox(), HangingEntity.class::isInstance).isEmpty();
        }
    }

    @Override
    public Vec3 getLightProbePosition(float partialTick) {
        return Vec3.atCenterOf(this.pos);
    }

    public int getRotation() {
        return this.getEntityData().get(CANVAS_ROTATION);
    }

    private void setRotation(int rotation) {
        this.getEntityData().set(CANVAS_ROTATION, (byte) (rotation % 4));
    }

    public String getCanvasID() {
        return this.getEntityData().get(CANVAS_ID);
    }

    private void setCanvasID(String canvasID) {
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
        this.getEntityData().set(CANVAS_TYPE_KEY, canvasType.toByte());
    }

    public boolean isGlass() {
        return this.getEntityData().get(CANVAS_GLASS);
    }

    private void setGlass(boolean glass) {
        this.getEntityData().set(CANVAS_GLASS, glass);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, this.getDirection().get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        this.pos = new BlockPos(input.getIntOr("TileX", 0), input.getIntOr("TileY", 0), input.getIntOr("TileZ", 0));
        ValueInput canvasInput = input.child("canvas").orElse(input);
        this.canvasSigned = canvasInput.getString("author").isPresent() && canvasInput.getString("title").isPresent();
        String canvasId = canvasInput.getStringOr("name", "");
        this.setCanvasID(canvasId);
        int version = canvasInput.getIntOr("v", 0);
        this.setVersion(version);
        if (canvasSigned) {
            this.canvasAuthor = canvasInput.getStringOr("author", "");
            this.canvasTitle = canvasInput.getStringOr("title", "");
            this.canvasGeneration = canvasInput.getIntOr("generation", 0);
        }

        Picture picture = PICTURES.get(canvasId);
        if (picture == null || picture.version < version) {
            boolean sidesActive = canvasInput.getBooleanOr("sidesActive", false);
            int[] sidePixels = canvasInput.getIntArray("sidePixels").orElse(new int[0]);
            PICTURES.put(canvasId, new Picture(version, canvasInput.getIntArray("pixels").orElse(new int[0]), sidesActive, sidePixels));
        }

        this.setCanvasType(CanvasType.fromByte(input.getByteOr("ctype", (byte) 0)));
        this.setGlass(input.getBooleanOr("glass", false));
        byte realFace = input.getByteOr("RealFace", (byte) -1);
        byte facing = input.getByteOr("Facing", (byte) -1);
        if (facing != -1 && realFace == -1) {
            Direction horizontal = Direction.from2DDataValue(facing);
            this.setDirection(horizontal);
        } else {
            this.setDirection(Direction.from3DDataValue(realFace == -1 ? 0 : realFace));
        }
        this.setRotation(input.getByteOr("Rotation", (byte) 0));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        BlockPos blockpos = this.getPos();
        output.putInt("TileX", blockpos.getX());
        output.putInt("TileY", blockpos.getY());
        output.putInt("TileZ", blockpos.getZ());
        output.putString("name", getCanvasID());
        output.putInt("v", getVersion());
        if (canvasSigned && canvasAuthor != null && canvasTitle != null) {
            output.putString("author", canvasAuthor);
            output.putString("title", canvasTitle);
            output.putInt("generation", canvasGeneration);
        }
        output.putByte("ctype", getCanvasTypeKey());
        output.putBoolean("glass", isGlass());
        output.putByte("RealFace", (byte) this.getDirection().get3DDataValue());
        output.putByte("Rotation", (byte) this.getRotation());

        Picture picture = PICTURES.get(getCanvasID());
        if (picture != null) {
            output.putIntArray("pixels", picture.pixels);
            if (picture.sidePixels().length > 0) {
                output.putBoolean("sidesActive", picture.sidesActive());
                output.putIntArray("sidePixels", picture.sidePixels());
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        CanvasType canvasType = this.requireCanvasType();
        if (canvasType == CanvasType.SMALL || canvasType == CanvasType.LARGE) {
            if (!this.level().isClientSide()) {
                setRotation(getRotation() + 1);
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public record Picture(int version, int[] pixels, boolean sidesActive, int[] sidePixels) {
        public Picture {
            pixels = pixels.clone();
            sidePixels = sidePixels.clone();
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
            if (this == o) return true;
            if (!(o instanceof Picture(
                    int otherVersion, int[] otherPixels, boolean otherSidesActive, int[] otherSidePixels
            )))
                return false;
            return version == otherVersion && sidesActive == otherSidesActive
                    && java.util.Arrays.equals(pixels, otherPixels)
                    && java.util.Arrays.equals(sidePixels, otherSidePixels);
        }

        @Override
        public int hashCode() {
            int result = Integer.hashCode(version);
            result = 31 * result + java.util.Arrays.hashCode(pixels);
            result = 31 * result + Boolean.hashCode(sidesActive);
            result = 31 * result + java.util.Arrays.hashCode(sidePixels);
            return result;
        }

        @Override
        public String toString() {
            return "Picture{" +
                    "version=" + version +
                    ", pixels=" + java.util.Arrays.toString(pixels) +
                    ", sidesActive=" + sidesActive +
                    ", sidePixels=" + java.util.Arrays.toString(sidePixels) +
                    '}';
        }
    }

    private CanvasType requireCanvasType() {
        return java.util.Objects.requireNonNull(this.getCanvasType(), "Canvas type");
    }
}
