package xerca.xercamusic.common.entity;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockInstrument;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemBlockInstrument;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;
import java.util.UUID;

public class EntityMusicSpirit extends Entity {
    public static final ResourceLocation spawnPacketId = new ResourceLocation(XercaMusic.MODID, "spawn_music_spirit");
    private Player body;
    private ItemStack note;
    private IItemInstrument instrument;
    private final ArrayList<NoteEvent> notes = new ArrayList<>();
    private int mLengthBeats;
    private float mVolume;
    private byte mBPS;
    private boolean isPlaying = true;
    private BlockInstrument blockInstrument = null;
    private BlockPos blockInsPos = null;
    private SoundController soundController = null;

    public EntityMusicSpirit(Level worldIn) {
        super(Entities.MUSIC_SPIRIT, worldIn);
    }

    public EntityMusicSpirit(Level worldIn, Player body, IItemInstrument instrument) {
        this(worldIn);
        this.body = body;
        this.instrument = instrument;
        setNoteFromBody();
        this.setPos(body.getX(), body.getY(), body.getZ());
    }

    public EntityMusicSpirit(Level worldIn, Player body, BlockPos blockInsPos, IItemInstrument instrument) {
        this(worldIn, body, instrument);
        setBlockPosAndInstrument(blockInsPos, instrument.getInstrumentId());
    }

    public EntityMusicSpirit(EntityType<EntityMusicSpirit> type, Level world) {
        super(type, world);
    }

    private void setBlockPosAndInstrument(BlockPos pos, int instrumentId){
        if (instrumentId < Items.instruments.length) {
            IItemInstrument instrument = Items.instruments[instrumentId];
            if (instrument instanceof ItemBlockInstrument itemBlockInstrument) {
                this.blockInstrument = (BlockInstrument) itemBlockInstrument.getBlock();
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
        if(body == null) {
            XercaMusic.LOGGER.warn("Body is null in MusicSpirit setNoteFromBody");
            return;
        }
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
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
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
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
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
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        ClientboundAddEntityPacket pack = new ClientboundAddEntityPacket(this);
        pack.write(buffer);
        writeSpawnData(buffer);
        return ServerPlayNetworking.createS2CPacket(spawnPacketId, buffer);
    }

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

        if(blockInsPos != null) {
            this.instrument = blockInstrument.getItemInstrument();
            this.setNoteFromBody();
        }
        else if(body != null) {
            Item item = body.getMainHandItem().getItem();
            if(item instanceof IItemInstrument ins) {
                this.instrument = ins;
                this.note = body.getOffhandItem();
                this.setPos(body.getX(), body.getY(), body.getZ());
            }
            else {
                XercaMusic.LOGGER.warn("Could not find instrument when spawning music spirit!");
                return;
            }
        }

        if (note != null && note.hasTag() && note.getTag() != null && note.getTag().contains("id") && note.getTag().contains("ver") && note.getTag().contains("l")) {
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
    public void onClientRemoval() {
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
