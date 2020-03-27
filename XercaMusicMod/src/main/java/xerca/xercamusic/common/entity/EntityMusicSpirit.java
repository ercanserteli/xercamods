package xerca.xercamusic.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import xerca.xercamusic.client.NoteSound;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

public class EntityMusicSpirit extends Entity implements IEntityAdditionalSpawnData {
    private PlayerEntity body;
    private ItemStack note;
    private ItemInstrument instrument;
    private byte[] music;
    private int mLength;
    private int mTime;
    private byte mPause;
    private NoteSound lastPlayed = null;
    private boolean isPlaying = true;

    public EntityMusicSpirit(World worldIn) {
        super(Entities.MUSIC_SPIRIT, worldIn);
    }

    public EntityMusicSpirit(World worldIn, PlayerEntity body) {
        super(Entities.MUSIC_SPIRIT, worldIn);
        this.body = body;
        this.instrument = (ItemInstrument) body.getHeldItemMainhand().getItem();
        this.note = body.getHeldItemOffhand();
        this.mTime = 0;
        this.setPosition(body.getPosX(), body.getPosY(), body.getPosZ());
        if (note.hasTag() && note.getTag().contains("music")) {
            CompoundNBT comp = note.getTag();
            music = comp.getByteArray("music");
            mLength = comp.getInt("length");
            mPause = comp.getByte("pause");
        }
    }

    public EntityMusicSpirit(EntityType<EntityMusicSpirit> type, World world) {
        super(type, world);
    }

    public EntityMusicSpirit(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        this(world);
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
        this.music = tagCompound.getByteArray("music");
        this.mLength = tagCompound.getInt("length");
        this.mPause = tagCompound.getByte("pause");
        this.isPlaying = tagCompound.getBoolean("playing");
    }

    @Override
    protected void writeAdditional(CompoundNBT tagCompound) {
        tagCompound.putByteArray("music", this.music);
        tagCompound.putInt("length", mLength);
        tagCompound.putByte("pause", mPause);
        tagCompound.putBoolean("playing", isPlaying);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(body != null ? body.getEntityId() : -1);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        int id = additionalData.readInt();
        Entity ent = world.getEntityByID(id);
        if (ent instanceof PlayerEntity) {
            body = (PlayerEntity) ent;
        }
        this.instrument = (ItemInstrument) body.getHeldItemMainhand().getItem();
        this.note = body.getHeldItemOffhand();
        this.mTime = 0;
        this.setPosition(body.getPosX(), body.getPosY(), body.getPosZ());
        if (note.hasTag() && note.getTag().contains("music")) {
            CompoundNBT comp = note.getTag();
            music = comp.getByteArray("music");
            mLength = comp.getInt("length");
            mPause = comp.getByte("pause");
        }
    }

    @Override
    protected void registerData() {

    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (body == null || !isPlaying) {
                this.remove();
                return;
            }
            ItemStack mainStack = body.getHeldItemMainhand();
            ItemStack offStack = body.getHeldItemOffhand();
            if (offStack.isEmpty() || offStack.getItem() != Items.MUSIC_SHEET || mainStack.isEmpty() || mainStack.getItem() != instrument) {
                isPlaying = false;
                this.remove();
                return;
            }
        }
        super.tick();
        this.setPosition(body.getPosX(), body.getPosY(), body.getPosZ());
        if (this.world.isRemote) {
            if(mPause == 0){
                System.err.println("EntityMusicSpirit mPause is 0! THIS SHOULD NOT HAPPEN!");
                return;
            }
            if (this.ticksExisted % mPause == 0) {
                if (mTime == mLength) {
                    XercaMusic.proxy.endMusic(getEntityId(), body.getEntityId());
                    this.remove();
                    return;
                }
                if (music[mTime] != 0 && music[mTime] <= 48) {
                    if(instrument.shouldCutOff && lastPlayed != null){
                        lastPlayed.stopSound();
                    }
                    lastPlayed = XercaMusic.proxy.playNote(instrument.getSound(music[mTime] - 1), getPosX(), getPosY() + 0.5d, getPosZ());
                    this.world.addParticle(ParticleTypes.NOTE, getPosX(), getPosY() + 2.2D, getPosZ(), (music[mTime] -1) / 24.0D, 0.0D, 0.0D);

                }
                mTime++;
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
