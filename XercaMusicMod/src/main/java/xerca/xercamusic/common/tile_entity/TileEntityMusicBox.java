package xerca.xercamusic.common.tile_entity;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.MusicBoxUpdatePacket;

public class TileEntityMusicBox extends TileEntity implements ITickableTileEntity {
    private boolean isPlaying = false;
    private boolean oldPoweredState = false;
    private boolean isPowering = false;
    private boolean firstBlockUpdate = true;

    private ItemStack noteStack = ItemStack.EMPTY;
    private ItemInstrument instrument;
    private byte[] music;
    private int mLength;
    private int mTime = 0;
    private byte mPause;
    private int age = 0;
    private int poweringAge = 0;
    private NoteSound lastPlayed = null;

    public TileEntityMusicBox() {
        super(TileEntities.MUSIC_BOX);
    }

    @Override
    public void setPos(BlockPos pos)
    {
        super.setPos(pos);
    }

    @Override
    public CompoundNBT write(CompoundNBT parent) {
        super.write(parent); // The super call is required to save and load the tileEntity's location

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
            setNoteStack(note);
        }
        if (parent.contains("instrument_id", 8)) {
            this.setInstrument(ForgeRegistries.ITEMS.getValue(new ResourceLocation(parent.getString("instrument_id"))));
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
//        XercaMusic.LOGGER.debug("TileEntityMusicBox getUpdateTag called");
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
//        XercaMusic.LOGGER.debug("TileEntityMusicBox handleUpdateTag called");
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
        // Powering state timer should work in all cases
        if(isPowering){
            if(poweringAge >= 20){
                stopPowering();
                return;
            }
            else{
                poweringAge++;
            }
        }

        // Other things only work if a note stack and an instrument are present
        if(noteStack.isEmpty() || instrument == null){
            return;
        }
        BlockState state = this.getBlockState();

//        XercaMusic.LOGGER.debug("oldPoweredState: " + oldPoweredState);

        if (state.get(BlockMusicBox.POWERED)) {
            if (!oldPoweredState) {
//                XercaMusic.LOGGER.debug(this.pos + " Unpowered to powered");
                // unpowered to powered
                isPlaying = !isPlaying;
                age = 0;
                mTime = 0;
                poweringAge = 0;
                oldPoweredState = true;
            }
        }else{
            if (oldPoweredState) {
//                XercaMusic.LOGGER.debug(this.pos + " Powered to unpowered");
                oldPoweredState = false;
            }
        }

        if (isPlaying) {
            if (mPause == 0) {
                XercaMusic.LOGGER.error("TileEntityMusicBox mPause is 0! THIS SHOULD NOT HAPPEN!");
                return;
            }
            if (age % mPause == 0) {
                if (mTime >= mLength) {
                    //System.out.println("music bitti!");
                    age = 0;
                    mTime = 0;
                    poweringAge = 0;
                    isPlaying = false;
                    isPowering = true;

                    //if(!world.isRemote) {
                        Direction rightSide = state.get(BlockMusicBox.HORIZONTAL_FACING).rotateY();
                        world.setBlockState(pos, state.with(BlockMusicBox.POWERING, true));

                        BlockPos neighbor = pos.offset(rightSide);
                        world.neighborChanged(neighbor, this.getBlockState().getBlock(), pos);
                        world.notifyNeighborsOfStateExcept(neighbor, this.getBlockState().getBlock(), rightSide.getOpposite());

                    //}
                    return;
                }

                if(world.isRemote && mTime < music.length){
                    if (music[mTime] != 0 && music[mTime] <= 48) {

                        if(instrument.shouldCutOff && lastPlayed != null){
                            lastPlayed.stopSound();
                        }
                        lastPlayed = XercaMusic.proxy.playNote(instrument.getSound(music[mTime] - 1), pos.getX(), pos.getY() + 0.5D, pos.getZ(), SoundCategory.RECORDS, 4.0f, 1.0f);

                        this.world.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5D, pos.getY() + 2.2D, pos.getZ() + 0.5D, (music[mTime] -1) / 24.0D, 0.0D, 0.0D);
                    }
                }
                mTime++;
            }
        }
        age++;
    }

    public ItemStack getNoteStack() {
        return noteStack;
    }

    public void setNoteStack(ItemStack noteStack) {
        if(noteStack.getItem() instanceof ItemMusicSheet){
            if(world != null && !world.isRemote){
                updateClient(noteStack, instrument);
            }

            this.noteStack = noteStack;
            if (noteStack.hasTag() && noteStack.getTag().contains("music")) {
                CompoundNBT comp = noteStack.getTag();
                music = comp.getByteArray("music");
                mLength = comp.getInt("length");
                mPause = comp.getByte("pause");
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
        this.read(getBlockState(), pkt.getNbtCompound());
        if(world != null && getBlockState().get(BlockMusicBox.POWERING)){
            stopPowering();
        }
    }
}
