package xerca.xercamusic.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockInstrument;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;
import java.util.UUID;

public class EntityMusicSpirit extends Entity implements IEntityAdditionalSpawnData {
    private PlayerEntity body;
    private ItemStack note;
    private ItemInstrument instrument;
    private final ArrayList<NoteEvent> notes = new ArrayList<>();
    private int mLengthBeats;
    private float mVolume;
    private byte mBPS;
    private NoteSound lastPlayed = null;
    private boolean isPlaying = true;
    private BlockInstrument blockInstrument = null;
    private BlockPos blockInsPos = null;
    private SoundController soundController = null;

    public EntityMusicSpirit(World worldIn) {
        super(Entities.MUSIC_SPIRIT, worldIn);
    }

    public EntityMusicSpirit(World worldIn, PlayerEntity body, ItemInstrument instrument) {
        super(Entities.MUSIC_SPIRIT, worldIn);
        this.body = body;
        this.instrument = instrument;
        setNoteFromBody();
        this.setPosition(body.getPosX(), body.getPosY(), body.getPosZ());
    }

    public EntityMusicSpirit(World worldIn, PlayerEntity body, BlockPos blockInsPos, ItemInstrument instrument) {
        this(worldIn, body, instrument);
        setBlockPosAndInstrument(blockInsPos);
    }

    public EntityMusicSpirit(EntityType<EntityMusicSpirit> type, World world) {
        super(type, world);
    }

    public EntityMusicSpirit(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(world);
    }

    private void setBlockPosAndInstrument(BlockPos pos){
        this.blockInsPos = pos;
        Block block = world.getBlockState(blockInsPos).getBlock();

        if(block instanceof BlockInstrument) {
            blockInstrument = (BlockInstrument)block;
            setPosition((double)blockInsPos.getX()+0.5, (double)blockInsPos.getY()-0.5, (double)blockInsPos.getZ()+0.5);
        }
        else{
            XercaMusic.LOGGER.warn("Got invalid block as instrument");
            blockInstrument = null;
            blockInsPos = null;
        }
    }

    private boolean isBodyHandLegit(){
        ItemStack mainStack = body.getHeldItemMainhand();
        ItemStack offStack = body.getHeldItemOffhand();
        if(blockInstrument != null && blockInsPos != null){
            return mainStack.getItem() == Items.MUSIC_SHEET || offStack.getItem() == Items.MUSIC_SHEET;
        }
        else{
            return offStack.getItem() == Items.MUSIC_SHEET && mainStack.getItem() == instrument;
        }
    }

    private void setNoteFromBody(){
        ItemStack mainStack = body.getHeldItemMainhand();
        ItemStack offStack = body.getHeldItemOffhand();
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
    protected void readAdditional(CompoundNBT tag) {
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
    protected void writeAdditional(CompoundNBT tag) {
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
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(body != null ? body.getEntityId() : -1);
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
    public void readSpawnData(PacketBuffer buffer) {
        int entityId = buffer.readInt();
        Entity ent = world.getEntityByID(entityId);
        if (ent instanceof PlayerEntity) {
            body = (PlayerEntity) ent;
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
            this.instrument = (ItemInstrument) body.getHeldItemMainhand().getItem();
            this.note = body.getHeldItemOffhand();
            this.setPosition(body.getPosX(), body.getPosY(), body.getPosZ());
        }

        if (note.hasTag() && note.getTag().contains("id") && note.getTag().contains("ver") && note.getTag().contains("l")) {
            CompoundNBT comp = note.getTag();
            mLengthBeats = comp.getInt("l");
            mBPS = comp.contains("bps") ? comp.getByte("bps") : 8;
            mVolume = comp.contains("vol") ? comp.getFloat("vol") : 1.f;
            UUID id = comp.getUniqueId("id");
            int ver = comp.getInt("ver");

            if(world.isRemote){
                MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                    MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                    if(data != null){
                        notes.addAll(data.notes);
                    }

                    soundController = new SoundController(notes, getPosX(), getPosY(), getPosZ(), instrument, mBPS, mVolume, getEntityId());
                    soundController.start();
                });
            }
        }
    }

    @Override
    protected void registerData() {

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
        if (!this.world.isRemote) {
            if (body == null || !isPlaying) {
                this.remove();
                return;
            }
            if (!isBodyHandLegit()) {
                isPlaying = false;
                this.remove();
                return;
            }

            if(blockInsPos != null && blockInstrument != null){
                if(world.getBlockState(blockInsPos).getBlock() != blockInstrument){
                    this.remove();
                    return;
                }
                if(this.getPosition().distanceSq(this.body.getPosition()) > 16){
                    this.remove();
                    return;
                }
            }
        }
        super.tick();
        if(blockInsPos == null || blockInstrument == null){
            if(body != null) {  // this check is added to work around a strange crash
                this.setPosition(body.getPosX(), body.getPosY(), body.getPosZ());
                if(soundController != null) {
                    soundController.setPos(getPosX(), getPosY(), getPosZ());
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

    public PlayerEntity getBody() {
        return body;
    }

    public void setBody(PlayerEntity body) {
        this.body = body;
    }

}
