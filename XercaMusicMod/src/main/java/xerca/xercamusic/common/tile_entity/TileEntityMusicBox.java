package xerca.xercamusic.common.tile_entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.MusicBoxUpdatePacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.sendToClient;

public class TileEntityMusicBox extends BlockEntity {
    private boolean isPlaying = false;
    private boolean oldPoweredState = false;
    private boolean isPowering = false;
    private boolean firstBlockUpdate = true;

    private ItemStack sheetStack = ItemStack.EMPTY;
    private IItemInstrument instrument;
    private final ArrayList<NoteEvent> notes = new ArrayList<>();
    private byte bps;
    private float volume;
    private int poweringAge = 0;
    private int playingAge = 0;
    private int length = 0;
    private SoundController soundController = null;

    public TileEntityMusicBox(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.MUSIC_BOX, blockPos, blockState);
        if(blockState.getValue(BlockMusicBox.POWERED)){
            oldPoweredState = true;
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag parent, HolderLookup.@NotNull Provider levelRegistry) {
        super.saveAdditional(parent, levelRegistry);

        if (!this.sheetStack.isEmpty()) {
            CompoundTag sheetTag = new CompoundTag();
            parent.put("note", sheetStack.save(levelRegistry, sheetTag));
        }
        if (this.instrument != null) {
            ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey((Item) this.instrument);
            parent.putString("instrument_id", resourcelocation.toString());
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag parent, HolderLookup.@NotNull Provider levelRegistry) {
        super.loadAdditional(parent, levelRegistry);
        if (parent.contains("note", 10)) {
            CompoundTag sheetTag = parent.getCompound("note");
            ItemStack sheet = ItemStack.parse(levelRegistry, sheetTag).orElse(ItemStack.EMPTY);
            setSheetStack(sheet, false);
        }
        if (parent.contains("instrument_id", 8)) {
            this.setInstrument(BuiltInRegistries.ITEM.get(new ResourceLocation(parent.getString("instrument_id"))));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    private void stopPowering(){
        BlockState state = this.getBlockState();
        if(level != null) {
            level.setBlockAndUpdate(worldPosition, state.setValue(BlockMusicBox.POWERING, false));
        }
        isPowering = false;
        poweringAge = 0;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, TileEntityMusicBox t) {
        if(level != null && !t.sheetStack.isEmpty() && t.notes.isEmpty() && !ItemMusicSheet.isEmptySheet(t.sheetStack)) {
            UUID id = t.sheetStack.get(Items.SHEET_ID);
            int ver = t.sheetStack.getOrDefault(Items.SHEET_VERSION, -1);
            byte bps = t.sheetStack.getOrDefault(Items.SHEET_BPS, (byte)0);
            int length = t.sheetStack.getOrDefault(Items.SHEET_LENGTH, 0);
            if (id != null && ver >= 0 && bps > 0 && length > 0) {
                if (level.isClientSide) {
                    MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                        MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                        if (data != null) {
                            t.notes.clear();
                            t.notes.addAll(data.notes());
                        }
                    });
                } else {
                    MinecraftServer server = level.getServer();
                    if (server != null) {
                        MusicManager.MusicData data = MusicManager.getMusicData(id, ver, server);
                        if (data != null) {
                            t.notes.clear();
                            t.notes.addAll(data.notes());
                        }
                        else{
                            Mod.LOGGER.warn("Unknown music sheet (id: {})", id);
                        }
                    }
                }
            }
        }

        // Powering state timer should work in all cases
        if(t.isPowering){
            if(t.poweringAge >= 10){
                t.stopPowering();
                return;
            }
            else{
                t.poweringAge++;
            }
        }

        // Other things only work if a note stack and an instrument are present
        if(t.sheetStack.isEmpty() || t.instrument == null){
            if(t.soundController != null){
                t.soundController.setStop();
            }
            t.isPlaying = false;
            return;
        }

        if (state.getValue(BlockMusicBox.POWERED)) {
            if (!t.oldPoweredState) {
                // unpowered to powered
                t.isPlaying = !t.isPlaying;
                t.poweringAge = 0;
                t.oldPoweredState = true;
                t.playingAge = 0;

                if(t.isPlaying){
                    musicStart(t, blockPos);
                }
                else{
                    if(t.soundController != null){
                        t.soundController.setStop();
                    }
                }
            }
        }
        else {
            if (t.oldPoweredState) {
                // powered to unpowered
                t.oldPoweredState = false;
            }
        }

        if(t.isPlaying){
            t.playingAge ++;
            if(t.playingAge >= t.beatsToTicks(t.length)){
                musicOver(t, state);
            }
        }
    }

    private int beatsToTicks(int beats){
        return Math.round(((float)beats) * 20.0f / ((float) bps));
    }

    public static void musicOver(TileEntityMusicBox t, BlockState state) {
        t.poweringAge = 0;
        t.isPlaying = false;
        t.isPowering = true;

        if(t.level != null){
            Direction rightSide = state.getValue(BlockMusicBox.FACING).getClockWise();
            t.level.setBlockAndUpdate(t.worldPosition, state.setValue(BlockMusicBox.POWERING, true));

            BlockPos neighbor = t.worldPosition.relative(rightSide);
            t.level.neighborChanged(neighbor, t.getBlockState().getBlock(), t.worldPosition);
            t.level.updateNeighborsAtExceptFromFacing(neighbor, t.getBlockState().getBlock(), rightSide.getOpposite());
        }
    }

    public static void musicStart(TileEntityMusicBox t, BlockPos blockPos) {
        if(t.level != null){
            if(t.level.isClientSide){
                if(t.soundController != null){
                    t.soundController.setStop();
                }
                t.soundController = new SoundController(t.notes, blockPos.getX(), blockPos.getY(), blockPos.getZ(), t.instrument, t.bps, t.volume, t);
                t.soundController.start();
            }
        }
    }

    public ItemStack getSheetStack() {
        return sheetStack;
    }

    public void setSheetStack(ItemStack sheetStack, boolean updateClient) {
        if(sheetStack.getItem() instanceof ItemMusicSheet){
            if(updateClient && level != null && !level.isClientSide){
                updateClient(sheetStack, (Item) instrument);
            }

            this.sheetStack = sheetStack;
            if (!ItemMusicSheet.isEmptySheet(sheetStack)) {
                bps = sheetStack.getOrDefault(Items.SHEET_BPS, (byte)8);
                volume = sheetStack.getOrDefault(Items.SHEET_VOLUME, 1.f);
                length = sheetStack.getOrDefault(Items.SHEET_LENGTH, 0);
            }
            else {
                this.notes.clear();
            }
            setChanged();
        }
    }

    public void removeSheetStack() {
        if(!this.sheetStack.isEmpty()){
            if(level != null && !level.isClientSide){
                updateClient(ItemStack.EMPTY, (Item) instrument);
            }

            this.sheetStack = ItemStack.EMPTY;
            this.notes.clear();
            setChanged();
        }
    }

    public IItemInstrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Item instrument) {
        if(instrument instanceof IItemInstrument){
            if(level != null && !level.isClientSide){
                updateClient(null, instrument);
            }

            this.instrument = (IItemInstrument) instrument;
            setChanged();
        }
    }

    public void removeInstrument() {
        if(this.instrument != null){
            if(level != null && !level.isClientSide){
                updateClient(null, null);
            }

            this.instrument = null;
            setChanged();
        }
    }

    // Send update to clients
    private void updateClient(ItemStack sheetStack, Item itemInstrument){
        MusicBoxUpdatePacket packet = MusicBoxUpdatePacket.create(worldPosition, sheetStack, itemInstrument);
        for(ServerPlayer player : PlayerLookup.tracking(this)) {
            sendToClient(player, packet);
        }
    }

    //fix to sync client state after the block was moved
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        //send update only on first block update to not send more packets than needed as updateClient() already does most of the functionality
        if (firstBlockUpdate) {
            firstBlockUpdate = false;
            if(level != null && getBlockState().getValue(BlockMusicBox.POWERING)){
                stopPowering();
            }
            return ClientboundBlockEntityDataPacket.create(this);
        }
        else return null;
    }

    @Override
    public void setRemoved() {
        if(soundController != null){
            soundController.setStop();
        }
        super.setRemoved();
    }
}
