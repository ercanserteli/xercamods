package xerca.xercamusic.common.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockInstrument;
import xerca.xercamusic.common.item.ItemBlockInstrument;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EntityMusicSpirit extends Entity implements IEntityAdditionalSpawnData {
    private Player body;
    private ItemStack note;
    private ItemInstrument instrument;
    private final ArrayList<NoteEvent> notes = new ArrayList<>();
    private int mLengthBeats;
    private float mVolume;
    private byte mBPS;
    private boolean isPlaying = true;
    private BlockInstrument blockInstrument = null;
    private BlockPos blockInsPos = null;
    private SoundController soundController = null;

    public EntityMusicSpirit(Level worldIn) {
        super(Objects.requireNonNull(Entities.MUSIC_SPIRIT), worldIn);
    }

    public EntityMusicSpirit(Level worldIn, Player body, ItemInstrument instrument) {
        super(Objects.requireNonNull(Entities.MUSIC_SPIRIT), worldIn);
        this.body = body;
        this.instrument = instrument;
        setNoteFromBody();
        this.setPos(body.getX(), body.getY(), body.getZ());
    }

    public EntityMusicSpirit(Level worldIn, Player body, BlockPos blockInsPos, ItemInstrument instrument) {
        this(worldIn, body, instrument);
        setBlockPosAndInstrument(blockInsPos, instrument.getInstrumentId());
    }

    public EntityMusicSpirit(EntityType<EntityMusicSpirit> type, Level world) {
        super(type, world);
    }

    public EntityMusicSpirit(PlayMessages.SpawnEntity ignoredSpawnEntity, Level world) {
        this(world);
    }

    private void setBlockPosAndInstrument(BlockPos pos, int instrumentId){
        if (instrumentId < Items.instruments.length) {
            ItemInstrument instrument = Items.instruments[instrumentId];
            if (instrument instanceof ItemBlockInstrument) {
                ItemBlockInstrument itemBlockInstrument = (ItemBlockInstrument) instrument;
                this.blockInstrument = itemBlockInstrument.getBlock();
                this.blockInsPos = pos;
                setPos((double)pos.getX()+0.5, (double)pos.getY()-0.5, (double)pos.getZ()+0.5);
                return;
            }
        }

        XercaMusic.LOGGER.warn("Got invalid block as instrument");
        blockInstrument = null;
        blockInsPos = null;
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
        if(tag.contains("bX") && tag.contains("bY") && tag.contains("bZ") && tag.contains("bIns")){
            setBlockPosAndInstrument(new BlockPos(tag.getInt("bX"), tag.getInt("bY"), tag.getInt("bZ")), tag.getInt("bIns"));
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
            tag.putInt("bIns", blockInstrument.getItemInstrument().getInstrumentId());
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
            buffer.writeInt(blockInstrument.getItemInstrument().getInstrumentId());
        }
        else{
            buffer.writeInt(-1);
            buffer.writeInt(-1000);
            buffer.writeInt(-1);
            buffer.writeInt(-1);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        int entityId = buffer.readInt();
        Entity ent = level.getEntity(entityId);
        if (ent instanceof Player) {
            body = (Player) ent;
        }

        int bx = buffer.readInt();
        int by = buffer.readInt();
        int bz = buffer.readInt();
        int bIns = buffer.readInt();
        if(by > -1000){
            setBlockPosAndInstrument(new BlockPos(bx, by ,bz), bIns);
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

        if (note.hasTag() && note.getTag() != null && note.getTag().contains("id") && note.getTag().contains("ver") && note.getTag().contains("l")) {
            CompoundTag comp = note.getTag();
            mLengthBeats = comp.getInt("l");
            mBPS = comp.contains("bps") ? comp.getByte("bps") : 8;
            mVolume = comp.contains("vol") ? comp.getFloat("vol") : 1.f;
            UUID id = comp.getUUID("id");
            int ver = comp.getInt("ver");

            if(level.isClientSide){
                MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                    MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                    if(data != null){
                        notes.addAll(data.notes);
                    }

                    soundController = new SoundController(notes, getX(), getY(), getZ(), instrument, mBPS, mVolume, getId());
                    soundController.start();
                });
            }
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
                if(this.position().distanceToSqr(this.body.position()) > 16){
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

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Player getBody() {
        return body;
    }

}
