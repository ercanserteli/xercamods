package xerca.xercamusic.common.tile_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.MusicBoxUpdatePacket;

import java.util.ArrayList;
import java.util.UUID;

public class TileEntityMusicBox extends BlockEntity {
    private boolean isPlaying = false;
    private boolean oldPoweredState = false;
    private boolean isPowering = false;
    private boolean firstBlockUpdate = true;

    private ItemStack noteStack = ItemStack.EMPTY;
    private ItemInstrument instrument;
    private ArrayList<NoteEvent> notes = new ArrayList<>();
    private byte mBPS;
    private float mVolume;
    private int poweringAge = 0;
    private int playingAge = 0;
    private int mLengthBeats = 0;
    private SoundController soundController = null;

    public TileEntityMusicBox(BlockPos blockPos, BlockState blockState) {
        super(TileEntities.MUSIC_BOX, blockPos, blockState);
        if(blockState.getValue(BlockMusicBox.POWERED)){
            oldPoweredState = true;
        }
    }

    @Override
    public CompoundTag save(CompoundTag parent) {
        super.save(parent); // The super call is required to save and load the tileEntity's location

        if (!this.noteStack.isEmpty()) {
            CompoundTag noteTag = new CompoundTag();
            noteStack.save(noteTag);
            parent.put("note", noteTag);
        }
        if (this.instrument != null) {
            ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.instrument);
            parent.putString("instrument_id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        }

        return parent;
    }

    @Override
    public void load(CompoundTag parent) {
        super.load(parent); // The super call is required to save and load the tiles location
        if (parent.contains("note", 10)) {
            CompoundTag noteTag = parent.getCompound("note");
            ItemStack note = ItemStack.of(noteTag);
            setNoteStack(note, false);
        }
        if (parent.contains("instrument_id", 8)) {
            this.setInstrument(ForgeRegistries.ITEMS.getValue(new ResourceLocation(parent.getString("instrument_id"))));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        this.load(nbt);
    }

    private void stopPowering(){
        BlockState state = this.getBlockState();
        level.setBlockAndUpdate(worldPosition, state.setValue(BlockMusicBox.POWERING, false));
        isPowering = false;
        poweringAge = 0;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, TileEntityMusicBox t) {
        if(level != null && !t.noteStack.isEmpty() && t.notes.isEmpty() && t.noteStack.hasTag()) {
            CompoundTag comp = t.noteStack.getTag();
            if (comp != null && comp.contains("id") && comp.contains("ver") && comp.contains("bps") && comp.contains("l")) {
                UUID id = comp.getUUID("id");
                int ver = comp.getInt("ver");
                if (level.isClientSide) {
                    MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                        MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                        if (data != null) {
                            t.notes.clear();
                            t.notes.addAll(data.notes);
                        }
                    });
                } else {
                    MusicManager.MusicData data = MusicManager.getMusicData(id, ver, level.getServer());
                    if (data != null) {
                        t.notes.clear();
                        t.notes.addAll(data.notes);
                    }
                }
            }
        }

        // Powering state timer should work in all cases
        if(t.isPowering){
            if(t.poweringAge >= 20){
                t.stopPowering();
                return;
            }
            else{
                t.poweringAge++;
            }
        }

        // Other things only work if a note stack and an instrument are present
        if(t.noteStack.isEmpty() || t.instrument == null){
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
            if(t.playingAge >= t.beatsToTicks(t.mLengthBeats)){
                musicOver(t, state);
            }
        }
    }

    private int beatsToTicks(int beats){
        return Math.round(((float)beats) * 20.0f / ((float)mBPS));
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
                t.soundController = new SoundController(t.notes, blockPos.getX(), blockPos.getY(), blockPos.getZ(), t.instrument, t.mBPS, t.mVolume, t);
                t.soundController.start();
            }
        }
    }

    public ItemStack getNoteStack() {
        return noteStack;
    }

    public void setNoteStack(ItemStack noteStack, boolean updateClient) {
        XercaMusic.LOGGER.warn("setNoteStack: " + noteStack.getTag());
        if(noteStack.getItem() instanceof ItemMusicSheet){
            if(updateClient && level != null && !level.isClientSide){
                updateClient(noteStack, instrument);
            }

            this.noteStack = noteStack;
            if (noteStack.hasTag() && noteStack.getTag().contains("id") && noteStack.getTag().contains("ver") && noteStack.getTag().contains("l")) {
                CompoundTag comp = noteStack.getTag();
                mBPS = comp.contains("bps") ? comp.getByte("bps") : 8;
                mVolume = comp.contains("vol") ? comp.getFloat("vol") : 1.f;
                mLengthBeats = comp.getInt("l");
            }
            else {
                this.notes.clear();
            }
            setChanged();
        }
    }

    public void removeNoteStack() {
        if(!this.noteStack.isEmpty()){
            if(level != null && !level.isClientSide){
                updateClient(ItemStack.EMPTY, instrument);
            }

            this.noteStack = ItemStack.EMPTY;
            this.notes.clear();
            setChanged();
        }
    }

    public ItemInstrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Item instrument) {
        if(instrument instanceof ItemInstrument){
            if(level != null && !level.isClientSide){
                updateClient(null, instrument);
            }

            this.instrument = (ItemInstrument) instrument;
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
    private void updateClient(ItemStack noteStack, Item itemInstrument){
        MusicBoxUpdatePacket packet = new MusicBoxUpdatePacket(worldPosition, noteStack, itemInstrument);
        XercaMusic.NETWORK_HANDLER.send(PacketDistributor.TRACKING_CHUNK.with(() -> (LevelChunk) level.getChunk(worldPosition)), packet);
    }

    //fix to sync client state after the block was moved
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        //send update only on first block update to not send more packets than needed as updateClient() already does most of the functionality
        if (firstBlockUpdate) {
            firstBlockUpdate = false;
            CompoundTag nbt = new CompoundTag();
            this.save(nbt);
            if(level != null && getBlockState().getValue(BlockMusicBox.POWERING)){
                stopPowering();
            }
            return new ClientboundBlockEntityDataPacket(this.getBlockPos(), 0, nbt);
        }
        else return null;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
        if(level != null && getBlockState().getValue(BlockMusicBox.POWERING)){
            stopPowering();
        }
    }

    @Override
    public void setRemoved() {
        if(soundController != null){
            soundController.setStop();
        }
        super.setRemoved();
    }
}
