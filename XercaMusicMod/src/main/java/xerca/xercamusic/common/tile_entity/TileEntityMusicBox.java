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
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.MusicBoxUpdatePacket;

public class TileEntityMusicBox extends BlockEntity {
    private boolean isPlaying = false;
    private boolean oldPoweredState = false;
    private boolean isPowering = false;
    private boolean firstBlockUpdate = true;

    private ItemStack noteStack = ItemStack.EMPTY;
    private ItemInstrument instrument;
    private byte[] music;
    private byte mPause;
    private int poweringAge = 0;
    private int playingAge = 0;
    private int mLength = 0;
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
            setNoteStack(note);
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
            if(t.playingAge >= t.mLength*t.mPause){
                musicOver(t, state);
            }
        }
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
                t.soundController = new SoundController(t.music, blockPos.getX(), blockPos.getY(), blockPos.getZ(), t.instrument, t.mPause, t);
                t.soundController.start();
            }
        }
    }

    public ItemStack getNoteStack() {
        return noteStack;
    }

    public void setNoteStack(ItemStack noteStack) {
        if(noteStack.getItem() instanceof ItemMusicSheet){
            if(level != null && !level.isClientSide){
                updateClient(noteStack, instrument);
            }

            this.noteStack = noteStack;
            if (noteStack.hasTag() && noteStack.getTag().contains("music")) {
                CompoundTag comp = noteStack.getTag();
                music = comp.getByteArray("music");
                mPause = comp.getByte("pause");
                mLength = comp.getByte("length");
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
