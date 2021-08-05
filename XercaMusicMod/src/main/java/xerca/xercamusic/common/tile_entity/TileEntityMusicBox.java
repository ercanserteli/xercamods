package xerca.xercamusic.common.tile_entity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.SoundEvents;
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
    private int mLength;
    private int mTime = 0;
    private byte mPause;
    private int age = 0;
    private int poweringAge = 0;
    private NoteSound lastPlayed = null;

    public TileEntityMusicBox(BlockPos blockPos, BlockState blockState) {
        super(TileEntities.MUSIC_BOX, blockPos, blockState);
    }

//    @Override
//    public void setPosition(BlockPos pos)
//    {
//        super.setPosition(pos);
//    }

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
//        XercaMusic.LOGGER.debug("TileEntityMusicBox getUpdateTag called");
        return this.save(new CompoundTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
//        XercaMusic.LOGGER.debug("TileEntityMusicBox handleUpdateTag called");
//        this.load(state, nbt);
        this.load(nbt);
    }

    private void stopPowering(){
        BlockState state = this.getBlockState();
        level.setBlockAndUpdate(worldPosition, state.setValue(BlockMusicBox.POWERING, false));
        isPowering = false;
        poweringAge = 0;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TileEntityMusicBox t) {
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
            return;
        }
        BlockState state = t.getBlockState();

//        XercaMusic.LOGGER.debug("oldPoweredState: " + oldPoweredState);

        if (state.getValue(BlockMusicBox.POWERED)) {
            if (!t.oldPoweredState) {
//                XercaMusic.LOGGER.debug(this.pos + " Unpowered to powered");
                // unpowered to powered
                t.isPlaying = !t.isPlaying;
                t.age = 0;
                t.mTime = 0;
                t.poweringAge = 0;
                t.oldPoweredState = true;
            }
        }else{
            if (t.oldPoweredState) {
//                XercaMusic.LOGGER.debug(this.pos + " Powered to unpowered");
                t.oldPoweredState = false;
            }
        }

        if (t.isPlaying) {
            if (t.mPause == 0) {
                XercaMusic.LOGGER.error("TileEntityMusicBox mPause is 0! THIS SHOULD NOT HAPPEN!");
                return;
            }
            if (t.age % t.mPause == 0) {
                if (t.mTime >= t.mLength) {
                    //System.out.println("music bitti!");
                    t.age = 0;
                    t.mTime = 0;
                    t.poweringAge = 0;
                    t.isPlaying = false;
                    t.isPowering = true;

                    //if(!world.isRemote) {
                        Direction rightSide = state.getValue(BlockMusicBox.FACING).getClockWise();
                        level.setBlockAndUpdate(t.worldPosition, state.setValue(BlockMusicBox.POWERING, true));

                        BlockPos neighbor = t.worldPosition.relative(rightSide);
                        level.neighborChanged(neighbor, t.getBlockState().getBlock(), t.worldPosition);
                        level.updateNeighborsAtExceptFromFacing(neighbor, t.getBlockState().getBlock(), rightSide.getOpposite());

                    //}
                    return;
                }

                if(level.isClientSide && t.mTime < t.music.length){
                    if (t.music[t.mTime] != 0 && t.music[t.mTime] <= 48) {

                        if(t.instrument.shouldCutOff && t.lastPlayed != null){
                            t.lastPlayed.stopSound();
                        }
//                        t.lastPlayed = XercaMusic.proxy.playNote(t.instrument.getSound(t.music[t.mTime] - 1), t.worldPosition.getX(), t.worldPosition.getY() + 0.5D, t.worldPosition.getZ(), SoundSource.RECORDS, 4.0f, 1.0f);
                        t.lastPlayed = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () ->
                                ClientStuff.playNote(t.instrument.getSound(t.music[t.mTime] - 1), t.worldPosition.getX(), t.worldPosition.getY() + 0.5D, t.worldPosition.getZ(), SoundSource.RECORDS, 4.0f, 1.0f));

                        t.level.addParticle(ParticleTypes.NOTE, t.worldPosition.getX() + 0.5D, t.worldPosition.getY() + 2.2D, t.worldPosition.getZ() + 0.5D, (t.music[t.mTime] -1) / 24.0D, 0.0D, 0.0D);
                    }
                }
                t.mTime++;
            }
        }
        t.age++;
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
                mLength = comp.getInt("length");
                mPause = comp.getByte("pause");
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
//        this.load(getBlockState(), pkt.getTag());
        this.load(pkt.getTag());
        if(level != null && getBlockState().getValue(BlockMusicBox.POWERING)){
            stopPowering();
        }
    }
}
