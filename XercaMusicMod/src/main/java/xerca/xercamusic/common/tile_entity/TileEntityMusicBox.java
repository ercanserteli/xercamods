package xerca.xercamusic.common.tile_entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;
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

public class TileEntityMusicBox extends TileEntity implements ITickableTileEntity  {
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

    public TileEntityMusicBox() {
        super(TileEntities.MUSIC_BOX);
    }

    @Override
    public CompoundNBT write(CompoundNBT parent) {
        super.write(parent);

        if (!this.noteStack.isEmpty()) {
            CompoundNBT noteTag = new CompoundNBT();
            noteStack.write(noteTag);
            parent.put("note", noteTag);
        }
        if (this.instrument != null) {
            ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.instrument);
            parent.putString("instrument_id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        }
        return parent;
    }

    @Override
    public void read(BlockState state, CompoundNBT parent) {
        super.read(state, parent); // The super call is required to save and load the tiles location
        if (parent.contains("note", 10)) {
            CompoundNBT noteTag = parent.getCompound("note");
            ItemStack note = ItemStack.read(noteTag);
            setNoteStack(note, false);
        }
        if (parent.contains("instrument_id", 8)) {
            this.setInstrument(ForgeRegistries.ITEMS.getValue(new ResourceLocation(parent.getString("instrument_id"))));
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        this.read(state, nbt);
    }

    private void stopPowering(){
        BlockState state = this.getBlockState();
        world.setBlockState(pos, state.with(BlockMusicBox.POWERING, false));
        isPowering = false;
        poweringAge = 0;
    }

    @Override
    public void tick() {
    	BlockState state = this.getBlockState();
    	TileEntity tileEntity = world.getTileEntity(pos);
    	if(!(tileEntity instanceof TileEntityMusicBox)) {
    		return;
    	}
    	TileEntityMusicBox te = (TileEntityMusicBox)tileEntity;
        if(!noteStack.isEmpty() && notes.isEmpty() && noteStack.hasTag()) {
            CompoundNBT comp = noteStack.getTag();
            if (comp != null && comp.contains("id") && comp.contains("ver") && comp.contains("bps") && comp.contains("l")) {
                UUID id = comp.getUniqueId("id");
                int ver = comp.getInt("ver");
                if (world.isRemote) {
                    MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                        MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                        if (data != null) {
                            notes.clear();
                            notes.addAll(data.notes);
                        }
                    });
                } else {
                    MusicManager.MusicData data = MusicManager.getMusicData(id, ver, world.getServer());
                    if (data != null) {
                        notes.clear();
                        notes.addAll(data.notes);
                    }
                }
            }
        }

        // Powering state timer should work in all cases
        if(isPowering){
            if(poweringAge >= 10){
                stopPowering();
                return;
            }
            else{
                poweringAge++;
            }
        }

        // Other things only work if a note stack and an instrument are present
        if(noteStack.isEmpty() || instrument == null){
            if(soundController != null){
                soundController.setStop();
            }
            isPlaying = false;
            return;
        }

        if (state.get(BlockMusicBox.POWERED)) {
            if (!oldPoweredState) {
                // unpowered to powered
                isPlaying = !isPlaying;
                poweringAge = 0;
                oldPoweredState = true;
                playingAge = 0;

                if(isPlaying){
                    musicStart(te, pos);
                }
                else{
                    if(soundController != null){
                        soundController.setStop();
                    }
                }
            }
        }
        else {
            if (oldPoweredState) {
                // powered to unpowered
                oldPoweredState = false;
            }
        }

        if(isPlaying){
            playingAge ++;
            if(playingAge >= beatsToTicks(mLengthBeats)){
                musicOver(te, state);
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

        if(t.world != null){
            Direction rightSide = state.get(BlockMusicBox.HORIZONTAL_FACING).rotateY();
            t.world.setBlockState(t.pos, state.with(BlockMusicBox.POWERING, true));

            BlockPos neighbor = t.pos.offset(rightSide);
            t.world.neighborChanged(neighbor, t.getBlockState().getBlock(), t.pos);
            t.world.notifyNeighborsOfStateExcept(neighbor, t.getBlockState().getBlock(), rightSide.getOpposite());
        }
    }

    public static void musicStart(TileEntityMusicBox t, BlockPos blockPos) {
        if(t.world != null){
            if(t.world.isRemote){
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
//        XercaMusic.LOGGER.debug("setNoteStack: " + noteStack.getTag());
        if(noteStack.getItem() instanceof ItemMusicSheet){
            if(updateClient && world != null && !world.isRemote){
                updateClient(noteStack, instrument);
            }

            this.noteStack = noteStack;
            if (noteStack.hasTag() && noteStack.getTag().contains("id") && noteStack.getTag().contains("ver") && noteStack.getTag().contains("l")) {
                CompoundNBT comp = noteStack.getTag();
                mBPS = comp.contains("bps") ? comp.getByte("bps") : 8;
                mVolume = comp.contains("vol") ? comp.getFloat("vol") : 1.f;
                mLengthBeats = comp.getInt("l");
            }
            else {
                this.notes.clear();
            }
            markDirty();
        }
    }

    public void removeNoteStack() {
        if(!this.noteStack.isEmpty()){
            if(world != null && !world.isRemote){
                updateClient(ItemStack.EMPTY, instrument);
            }

            this.noteStack = ItemStack.EMPTY;
            this.notes.clear();
            markDirty();
        }
    }

    public ItemInstrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Item instrument) {
        if(instrument instanceof ItemInstrument){
            if(world != null && !world.isRemote){
                updateClient(null, instrument);
            }

            this.instrument = (ItemInstrument) instrument;
            markDirty();
        }
    }

    public void removeInstrument() {
        if(this.instrument != null){
            if(world != null && !world.isRemote){
                updateClient(null, null);
            }

            this.instrument = null;
            markDirty();
        }
    }

    // Send update to clients
    private void updateClient(ItemStack noteStack, Item itemInstrument){
        MusicBoxUpdatePacket packet = new MusicBoxUpdatePacket(pos, noteStack, itemInstrument);
        XercaMusic.NETWORK_HANDLER.send(PacketDistributor.TRACKING_CHUNK.with(() -> (Chunk) world.getChunk(pos)), packet);
    }

    //fix to sync client state after the block was moved
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        //send update only on first block update to not send more packets than needed as updateClient() already does most of the functionality
        if (firstBlockUpdate) {
            firstBlockUpdate = false;
            CompoundNBT nbt = new CompoundNBT();
            this.write(nbt);
            if(world != null && getBlockState().get(BlockMusicBox.POWERING)){
                stopPowering();
            }
            return new SUpdateTileEntityPacket(this.getPos(), 0, nbt);
        }
        else return null;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.write(pkt.getNbtCompound());
        if(world != null && getBlockState().get(BlockMusicBox.POWERING)){
            stopPowering();
        }
    }
}
