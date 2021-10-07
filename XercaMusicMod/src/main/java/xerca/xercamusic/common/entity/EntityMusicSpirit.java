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
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockInstrument;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

public class EntityMusicSpirit extends Entity implements IEntityAdditionalSpawnData {
    private Player body;
    private ItemStack note;
    private ItemInstrument instrument;
    private byte[] music;
    private int mLength;
    private byte mPause;
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
        if (note.hasTag() && note.getTag().contains("music")) {
            CompoundTag comp = note.getTag();
            music = comp.getByteArray("music");
            mLength = comp.getInt("length");
            mPause = comp.getByte("pause");
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
    protected void readAdditionalSaveData(CompoundTag tagCompound) {
        this.music = tagCompound.getByteArray("music");
        this.mLength = tagCompound.getInt("length");
        this.mPause = tagCompound.getByte("pause");
        this.isPlaying = tagCompound.getBoolean("playing");
        if(tagCompound.contains("bX") && tagCompound.contains("bY") && tagCompound.contains("bZ")){
            setBlockPosAndInstrument(new BlockPos(tagCompound.getInt("bX"), tagCompound.getInt("bY"), tagCompound.getInt("bZ")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tagCompound) {
        tagCompound.putByteArray("music", this.music);
        tagCompound.putInt("length", mLength);
        tagCompound.putByte("pause", mPause);
        tagCompound.putBoolean("playing", isPlaying);
        if(blockInstrument != null && blockInsPos != null){
            tagCompound.putInt("bX", blockInsPos.getX());
            tagCompound.putInt("bY", blockInsPos.getY());
            tagCompound.putInt("bZ", blockInsPos.getZ());
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
        if (note.hasTag() && note.getTag().contains("music")) {
            CompoundTag comp = note.getTag();
            music = comp.getByteArray("music");
            mLength = comp.getInt("length");
            mPause = comp.getByte("pause");
        }

        if(level.isClientSide){
            this.soundController = new SoundController(music, getX(), getY(), getZ(), instrument, mPause, getId());
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
