package xerca.xercamusic.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockInstrument;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;

public class EntityMusicSpirit extends Entity implements IEntityAdditionalSpawnData {
    private Player body;
    private ItemStack note;
    private ItemInstrument instrument;
    private ArrayList<NoteEvent> notes = new ArrayList<>();
    private int mLengthBeats;
    private float mVolume;
    private byte mBPS;
    private NoteSound lastPlayed = null;
    private boolean isPlaying = true;
    private BlockInstrument blockInstrument = null;
    private BlockPos blockInsPos = null;
    private SoundController soundController = null;

    public EntityMusicSpirit(Level worldIn) {
        super(Entities.MUSIC_SPIRIT, worldIn);
    }

    public EntityMusicSpirit(Level worldIn, Player body, ItemInstrument instrument) {
        super(Entities.MUSIC_SPIRIT, worldIn);
        this.body = body;
        this.instrument = instrument;
        setNoteFromBody();
        this.setPos(body.getX(), body.getY(), body.getZ());
        if (note.hasTag() && note.getTag().contains("notes") && note.getTag().contains("l") && note.getTag().contains("bps")) {
            CompoundTag comp = note.getTag();
            NoteEvent.fillArrayFromNBT(notes, comp);
            mLengthBeats = comp.getInt("l");
            mBPS = comp.getByte("bps");
            mVolume = comp.contains("vol") ? comp.getFloat("vol") : 1.f;
        }
    }

    public EntityMusicSpirit(Level worldIn, Player body, BlockPos blockInsPos, ItemInstrument instrument) {
        this(worldIn, body, instrument);
        setBlockPosAndInstrument(blockInsPos);
    }

    public EntityMusicSpirit(EntityType<EntityMusicSpirit> type, Level world) {
        super(type, world);
    }

    public EntityMusicSpirit(FMLPlayMessages.SpawnEntity spawnEntity, Level world) {
        this(world);
    }

    private void setBlockPosAndInstrument(BlockPos pos){
        this.blockInsPos = pos;
        Block block = level.getBlockState(blockInsPos).getBlock();

        if(block instanceof BlockInstrument) {
            blockInstrument = (BlockInstrument)block;
            setPos((double)blockInsPos.getX()+0.5, (double)blockInsPos.getY()-0.5, (double)blockInsPos.getZ()+0.5);
        }
        else{
            XercaMusic.LOGGER.warn("Got invalid block as instrument");
            blockInstrument = null;
            blockInsPos = null;
        }
    }

    private boolean isBodyHandLegit(){
        ItemStack mainStack = body.getMainHandItem();
        ItemStack offStack = body.getOffhandItem();
        if(blockInstrument != null && blockInsPos != null){
            return mainStack.getItem() == Items.MUSIC_SHEET || offStack.getItem() == Items.MUSIC_SHEET;
        }
        else{
            return offStack.getItem() == Items.MUSIC_SHEET && mainStack.getItem() == instrument;
        }
    }

    private void setNoteFromBody(){
        ItemStack mainStack = body.getMainHandItem();
        ItemStack offStack = body.getOffhandItem();
        if(mainStack.getItem() == Items.MUSIC_SHEET){
            this.note = mainStack;
        }
        else if(offStack.getItem() == Items.MUSIC_SHEET){
            this.note = offStack;
        }
        else{
            XercaMusic.LOGGER.warn("No music sheet found on body");
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        NoteEvent.fillArrayFromNBT(notes, tag);
        this.mLengthBeats = tag.getInt("l");
        this.mBPS = tag.getByte("bps");
        this.mVolume = tag.getFloat("vol");
        this.isPlaying = tag.getBoolean("playing");
        if(tag.contains("bX") && tag.contains("bY") && tag.contains("bZ")){
            setBlockPosAndInstrument(new BlockPos(tag.getInt("bX"), tag.getInt("bY"), tag.getInt("bZ")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        NoteEvent.fillNBTFromArray(notes, tag);
        tag.putInt("l", mLengthBeats);
        tag.putByte("bps", mBPS);
        tag.putFloat("vol", mVolume);
        tag.putBoolean("playing", isPlaying);
        if(blockInstrument != null && blockInsPos != null){
            tag.putInt("bX", blockInsPos.getX());
            tag.putInt("bY", blockInsPos.getY());
            tag.putInt("bZ", blockInsPos.getZ());
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(body != null ? body.getId() : -1);
        if(blockInstrument != null && blockInsPos != null){
            buffer.writeInt(blockInsPos.getX());
            buffer.writeInt(blockInsPos.getY());
            buffer.writeInt(blockInsPos.getZ());
        }
        else{
            buffer.writeInt(-1);
            buffer.writeInt(-1);
            buffer.writeInt(-1);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        int id = buffer.readInt();
        Entity ent = level.getEntity(id);
        if (ent instanceof Player) {
            body = (Player) ent;
        }

        int bx = buffer.readInt();
        int by = buffer.readInt();
        int bz = buffer.readInt();
        if(by >= 0){
            setBlockPosAndInstrument(new BlockPos(bx, by ,bz));
        }

        if(blockInsPos != null){
            this.instrument = blockInstrument.getItemInstrument();
            this.setNoteFromBody();
        }
        else{
            this.instrument = (ItemInstrument) body.getMainHandItem().getItem();
            this.note = body.getOffhandItem();
            this.setPos(body.getX(), body.getY(), body.getZ());
        }
        if (note.hasTag() && note.getTag().contains("notes") && note.getTag().contains("l") && note.getTag().contains("bps")) {
            CompoundTag comp = note.getTag();
            NoteEvent.fillArrayFromNBT(notes, comp);
            mLengthBeats = comp.getInt("l");
            mBPS = comp.getByte("bps");
            mVolume = comp.getFloat("vol");
        }

        if(level.isClientSide){
            this.soundController = new SoundController(notes, getX(), getY(), getZ(), instrument, mBPS, mVolume, getId());
            this.soundController.start();
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        if(soundController != null){
            soundController.setStop();
        }
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (body == null || !isPlaying) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }
            if (!isBodyHandLegit()) {
                isPlaying = false;
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            if(blockInsPos != null && blockInstrument != null){
                if(level.getBlockState(blockInsPos).getBlock() != blockInstrument){
                    this.remove(RemovalReason.DISCARDED);
                    return;
                }
                if(this.position().distanceTo(this.body.position()) > 4){
                    this.remove(RemovalReason.DISCARDED);
                    return;
                }
            }
        }
        super.tick();
        if(blockInsPos == null || blockInstrument == null){
            if(body != null) {  // this check is added to work around a strange crash
                this.setPos(body.getX(), body.getY(), body.getZ());
                if(soundController != null) {
                    soundController.setPos(getX(), getY(), getZ());
                }
            }
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Player getBody() {
        return body;
    }

    public void setBody(Player body) {
        this.body = body;
    }

}
