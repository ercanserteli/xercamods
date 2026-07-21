package xerca.xercamusic.common.tile_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.MusicBoxUpdatePacket;

import java.util.ArrayList;
import java.util.UUID;

public class TileEntityMusicBox extends BlockEntity {
    private final ArrayList<NoteEvent> notes = new ArrayList<>();
    private final ArrayList<VolumeMarker> volumeMarkers = new ArrayList<>();
    private @Nullable UUID warnedMissingSheetId;
    private int warnedMissingSheetVersion = -1;
    private boolean isPlaying;
    private boolean oldPoweredState;
    private boolean isPowering;
    private boolean firstBlockUpdate = true;
    private ItemStack sheetStack = ItemStack.EMPTY;
    private @Nullable IItemInstrument instrument;
    private byte bps;
    private float volume;
    private int poweringAge;
    private int playingAge;
    private int length;
    private @Nullable SoundController soundController;
    private static final String KEY_NOTE = "note";
    private static final String KEY_INS_ID = "instrument_id";

    private static byte sanitizeBps(int bps) {
        return (byte) Math.clamp(bps, 1, 50);
    }

    private static float sanitizeVolume(float volume) {
        return Math.clamp(volume, 0.0f, 1.0f);
    }

    public TileEntityMusicBox(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.MUSIC_BOX, blockPos, blockState);
        boolean powered = blockState.getValue(BlockMusicBox.POWERED);
        if (powered) {
            oldPoweredState = true;
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, TileEntityMusicBox t) {
        t.loadMusicData(level);
        if (t.finishPoweringPulseIfExpired()) {
            return;
        }
        if (!t.hasPlaybackItems()) {
            t.stopPlayback();
            return;
        }
        t.handlePowerChange(state, blockPos);
        t.advancePlayback(state);
    }

    private void loadMusicData(Level level) {
        if (sheetStack.isEmpty() || !notes.isEmpty() || ItemMusicSheet.isEmptySheet(sheetStack)) {
            return;
        }

        UUID id = sheetStack.get(Items.SHEET_ID);
        int version = sheetStack.getOrDefault(Items.SHEET_VERSION, -1);
        byte sheetBps = sheetStack.getOrDefault(Items.SHEET_BPS, (byte) 0);
        int sheetLength = sheetStack.getOrDefault(Items.SHEET_LENGTH, 0);
        if (id == null || version < 0 || sheetBps <= 0 || sheetLength <= 0) {
            return;
        }

        if (level.isClientSide()) {
            MusicManagerClient.checkMusicDataAndRun(id, version, () -> {
                MusicManager.MusicData data = MusicManagerClient.getMusicData(id, version);
                if (data != null) {
                    applyMusicData(data);
                }
            });
            return;
        }

        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, server);
        if (data == null) {
            warnMissingSheetOnce(id, version);
            return;
        }
        applyMusicData(data);
        clearWarnedMissingSheet();
    }

    private void applyMusicData(MusicManager.MusicData data) {
        notes.clear();
        notes.addAll(data.notes());
        volumeMarkers.clear();
        if (data.volumeMarkers() != null) {
            volumeMarkers.addAll(data.volumeMarkers());
        }
    }

    private boolean finishPoweringPulseIfExpired() {
        if (!isPowering) {
            return false;
        }
        if (poweringAge < 10) {
            poweringAge++;
            return false;
        }
        stopPowering();
        return true;
    }

    private boolean hasPlaybackItems() {
        return !sheetStack.isEmpty() && instrument != null;
    }

    private void stopPlayback() {
        stopSoundController();
        isPlaying = false;
    }

    private void handlePowerChange(BlockState state, BlockPos blockPos) {
        boolean powered = state.getValue(BlockMusicBox.POWERED);
        if (powered == oldPoweredState) {
            return;
        }
        oldPoweredState = powered;
        if (!powered) {
            return;
        }

        isPlaying = !isPlaying;
        poweringAge = 0;
        playingAge = 0;
        if (isPlaying) {
            musicStart(this, blockPos);
        } else {
            stopSoundController();
        }
    }

    private void advancePlayback(BlockState state) {
        if (!isPlaying) {
            return;
        }
        playingAge++;
        if (playingAge >= beatsToTicks(length)) {
            musicOver(this, state);
        }
    }

    private void stopSoundController() {
        if (soundController != null) {
            soundController.setStop();
        }
    }

    public static void musicOver(TileEntityMusicBox t, BlockState state) {
        t.poweringAge = 0;
        t.isPlaying = false;
        t.isPowering = true;

        Level level = t.level;
        if (level != null) {
            Direction rightSide = state.getValue(HorizontalDirectionalBlock.FACING).getClockWise();
            level.setBlockAndUpdate(t.worldPosition, state.setValue(BlockMusicBox.POWERING, true));

            BlockPos neighbor = t.worldPosition.relative(rightSide);
            level.neighborChanged(neighbor, t.getBlockState().getBlock(), null);
            level.updateNeighborsAtExceptFromFacing(neighbor, t.getBlockState().getBlock(), rightSide.getOpposite(), null);
        }
    }

    public static void musicStart(TileEntityMusicBox t, BlockPos blockPos) {
        IItemInstrument instrument = t.instrument;
        if (t.level != null && t.level.isClientSide() && instrument != null) {
            if (t.soundController != null) {
                t.soundController.setStop();
            }
            t.soundController = new SoundController(t.notes, t.volumeMarkers, blockPos.getX(), blockPos.getY(), blockPos.getZ(), instrument, t.bps, t.volume, t);
            t.soundController.start();
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (!this.sheetStack.isEmpty()) {
            output.store(KEY_NOTE, ItemStack.CODEC, sheetStack);
        }
        if (this.instrument != null) {
            Identifier resourcelocation = BuiltInRegistries.ITEM.getKey((Item) this.instrument);
            output.putString(KEY_INS_ID, resourcelocation.toString());
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        Level level = this.level;
        if (level != null && oldState.getBlock() instanceof BlockMusicBox musicBox) {
            musicBox.ejectContentsOnRemoval(level, pos, oldState, this);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read(KEY_NOTE, ItemStack.CODEC).ifPresent(sheet -> setSheetStack(sheet, false));
        input.getString(KEY_INS_ID).ifPresent(insId ->
                this.setInstrument(BuiltInRegistries.ITEM.getValue(Identifier.parse(insId))));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    private void stopPowering() {
        BlockState state = this.getBlockState();
        if (level != null) {
            level.setBlockAndUpdate(worldPosition, state.setValue(BlockMusicBox.POWERING, false));
        }
        isPowering = false;
        poweringAge = 0;
    }

    private int beatsToTicks(int beats) {
        return Math.max(1, Math.round(beats * 20.0f / Math.max(1, bps)));
    }

    public ItemStack getSheetStack() {
        return sheetStack;
    }

    private void warnMissingSheetOnce(UUID id, int version) {
        if (!id.equals(warnedMissingSheetId) || warnedMissingSheetVersion != version) {
            Mod.LOGGER.warn("Unknown music sheet (id: {}, version: {})", id, version);
            warnedMissingSheetId = id;
            warnedMissingSheetVersion = version;
        }
    }

    private void clearWarnedMissingSheet() {
        warnedMissingSheetId = null;
        warnedMissingSheetVersion = -1;
    }

    public void setSheetStack(ItemStack sheetStack, boolean updateClient) {
        if (sheetStack.getItem() instanceof ItemMusicSheet) {
            if (updateClient && level != null && !level.isClientSide()) {
                updateClient(sheetStack, (Item) instrument);
            }

            this.sheetStack = sheetStack;
            clearWarnedMissingSheet();
            this.notes.clear();
            this.volumeMarkers.clear();
            if (!ItemMusicSheet.isEmptySheet(sheetStack)) {
                bps = sanitizeBps(sheetStack.getOrDefault(Items.SHEET_BPS, (byte) 8));
                volume = sanitizeVolume(sheetStack.getOrDefault(Items.SHEET_VOLUME, 1.f));
                length = sheetStack.getOrDefault(Items.SHEET_LENGTH, 0);
            }
            setChanged();
        }
    }

    public void removeSheetStack() {
        if (!this.sheetStack.isEmpty()) {
            if (level != null && !level.isClientSide()) {
                updateClient(ItemStack.EMPTY, (Item) instrument);
            }

            this.sheetStack = ItemStack.EMPTY;
            clearWarnedMissingSheet();
            this.notes.clear();
            this.volumeMarkers.clear();
            setChanged();
        }
    }

    @Nullable
    public IItemInstrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Item instrument) {
        if (instrument instanceof IItemInstrument itemInstrument) {
            if (level != null && !level.isClientSide()) {
                updateClient(null, instrument);
            }

            this.instrument = itemInstrument;
            setChanged();
        }
    }

    public void removeInstrument() {
        if (this.instrument != null) {
            if (level != null && !level.isClientSide()) {
                updateClient(null, null);
            }

            this.instrument = null;
            setChanged();
        }
    }

    // Send update to clients
    private void updateClient(@Nullable ItemStack sheetStack, @Nullable Item itemInstrument) {
        MusicBoxUpdatePacket packet = MusicBoxUpdatePacket.create(worldPosition, sheetStack, itemInstrument);
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new net.minecraft.world.level.ChunkPos(worldPosition.getX() >> 4, worldPosition.getZ() >> 4), packet);
        }
    }

    // fix to sync client state after the block was moved
    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        // send update only on the first block update to not send more packets than needed as updateClient() already does most of the functionality
        if (firstBlockUpdate) {
            firstBlockUpdate = false;
            if (level != null && getBlockState().getValue(BlockMusicBox.POWERING)) {
                stopPowering();
            }
            return ClientboundBlockEntityDataPacket.create(this);
        } else return null;
    }

    @Override
    public void setRemoved() {
        if (soundController != null) {
            soundController.setStop();
        }
        super.setRemoved();
    }
}
