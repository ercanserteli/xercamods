package xerca.xercamusic.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.block.BlockInstrument;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemBlockInstrument;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static xerca.xercamusic.common.item.ItemMusicSheet.*;

public class EntityMusicSpirit extends Entity implements IEntityAdditionalSpawnData {
    private final ArrayList<NoteEvent> notes = new ArrayList<>();
    private final ArrayList<VolumeMarker> volumeMarkers = new ArrayList<>();
    private Player body;
    private ItemStack note;
    private IItemInstrument instrument;
    private int length;
    private float volume;
    private byte bps;
    private boolean isPlaying = true;
    private BlockInstrument blockInstrument;
    private BlockPos blockInsPos;
    private SoundController soundController;

    private static byte sanitizeBps(int bps) {
        return (byte) Math.max(1, Math.min(50, bps));
    }

    private static int sanitizeLengthBeats(int beats) {
        return Math.max(0, beats);
    }

    private static float sanitizeVolume(float volume) {
        return Math.max(0.0f, Math.min(1.0f, volume));
    }

    public EntityMusicSpirit(Level worldIn) {
        super(Entities.MUSIC_SPIRIT.get(), worldIn);
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

    public EntityMusicSpirit(PlayMessages.SpawnEntity ignoredSpawnEntity, Level world) {
        this(world);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntityMusicSpirit other)) {
            return false;
        }
        return Objects.equals(this.getUUID(), other.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(EntityMusicSpirit.class, this.getUUID());
    }

    private void setBlockPosAndInstrument(BlockPos pos, int instrumentId) {
        if (instrumentId >= 0 && instrumentId < Items.instruments.length) {
            IItemInstrument itemInstrument = Items.instruments[instrumentId];
            if (itemInstrument instanceof ItemBlockInstrument itemBlockInstrument) {
                this.blockInstrument = (BlockInstrument) itemBlockInstrument.getBlock();
                this.blockInsPos = pos;
                setPos(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5);
                return;
            }
        }

        XercaMusic.LOGGER.warn("Did not find a block instrument at the set position");
        blockInstrument = null;
        blockInsPos = null;
    }

    private boolean isBodyHandLegit() {
        ItemStack mainStack = body.getMainHandItem();
        ItemStack offStack = body.getOffhandItem();
        if (blockInstrument != null && blockInsPos != null) {
            return mainStack.getItem() == Items.MUSIC_SHEET.get() || offStack.getItem() == Items.MUSIC_SHEET.get();
        } else {
            return offStack.getItem() == Items.MUSIC_SHEET.get() && mainStack.getItem() == instrument;
        }
    }

    private void setNoteFromBody() {
        if (body == null) {
            XercaMusic.LOGGER.warn("Body is null in MusicSpirit setNoteFromBody");
            return;
        }
        ItemStack mainStack = body.getMainHandItem();
        ItemStack offStack = body.getOffhandItem();
        if (mainStack.getItem() == Items.MUSIC_SHEET.get()) {
            this.note = mainStack;
        } else if (offStack.getItem() == Items.MUSIC_SHEET.get()) {
            this.note = offStack;
        } else {
            XercaMusic.LOGGER.warn("No music sheet found on body");
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        notes.clear();
        NoteEvent.fillArrayFromNBT(notes, tag);
        this.length = sanitizeLengthBeats(tag.getInt(KEY_LENGTH));
        this.bps = sanitizeBps(tag.getInt(KEY_BPS));
        this.volume = sanitizeVolume(tag.getFloat(KEY_VOLUME));
        this.isPlaying = tag.getBoolean("playing");
        if (tag.contains("bX") && tag.contains("bY") && tag.contains("bZ") && tag.contains("bIns")) {
            setBlockPosAndInstrument(new BlockPos(tag.getInt("bX"), tag.getInt("bY"), tag.getInt("bZ")), tag.getInt("bIns"));
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        NoteEvent.fillNBTFromArray(notes, tag);
        tag.putInt(KEY_LENGTH, length);
        tag.putByte(KEY_BPS, bps);
        tag.putFloat(KEY_VOLUME, volume);
        tag.putBoolean("playing", isPlaying);
        if (blockInstrument != null && blockInsPos != null) {
            tag.putInt("bX", blockInsPos.getX());
            tag.putInt("bY", blockInsPos.getY());
            tag.putInt("bZ", blockInsPos.getZ());
            tag.putInt("bIns", blockInstrument.getItemInstrument().getInstrumentId());
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(body != null ? body.getId() : -1);
        if (blockInstrument != null && blockInsPos != null) {
            buffer.writeInt(blockInsPos.getX());
            buffer.writeInt(blockInsPos.getY());
            buffer.writeInt(blockInsPos.getZ());
            buffer.writeInt(blockInstrument.getItemInstrument().getInstrumentId());
        } else {
            buffer.writeInt(-1);
            buffer.writeInt(-1000);
            buffer.writeInt(-1);
            buffer.writeInt(-1);
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        int entityId = buffer.readInt();
        Entity ent = level().getEntity(entityId);
        if (ent instanceof Player player) {
            body = player;
        }

        int bx = buffer.readInt();
        int by = buffer.readInt();
        int bz = buffer.readInt();
        int bIns = buffer.readInt();
        if (by > -1000) {
            setBlockPosAndInstrument(new BlockPos(bx, by, bz), bIns);
        }

        if (blockInsPos != null) {
            this.instrument = blockInstrument.getItemInstrument();
            this.setNoteFromBody();
        } else if (body != null) {
            Item item = body.getMainHandItem().getItem();
            if (item instanceof IItemInstrument ins) {
                this.instrument = ins;
                this.note = body.getOffhandItem();
                this.setPos(body.getX(), body.getY(), body.getZ());
            } else {
                XercaMusic.LOGGER.warn("Could not find instrument when spawning music spirit!");
                return;
            }
        }

        if (note != null && note.hasTag() && note.getTag() != null && note.getTag().contains("id") && note.getTag().contains("ver") && note.getTag().contains("l")) {
            CompoundTag comp = note.getTag();
            length = sanitizeLengthBeats(comp.getInt("l"));
            bps = sanitizeBps(comp.contains("bps") ? comp.getInt("bps") : 8);
            volume = sanitizeVolume(comp.contains("vol") ? comp.getFloat("vol") : 1.f);
            UUID id = comp.getUUID("id");
            int ver = comp.getInt("ver");

            if (level().isClientSide) {
                MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                    MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                    if (data != null) {
                        notes.addAll(data.notes());
                        if (data.volumeMarkers() != null) {
                            volumeMarkers.addAll(data.volumeMarkers());
                        }
                    }

                    soundController = new SoundController(notes, volumeMarkers, getX(), getY(), getZ(), instrument, bps, volume, getId());
                    soundController.start();
                });
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        // No need for synching data
    }

    @Override
    public void onClientRemoval() {
        if (soundController != null) {
            soundController.setStop();
        }
    }

    private boolean checkForRemoval() {
        if (this.level().isClientSide) {
            return false;
        }
        if (body == null || !isPlaying || body.isRemoved()) {
            isPlaying = false;
            this.remove(RemovalReason.DISCARDED);
            return true;
        }
        if (!isBodyHandLegit()) {
            isPlaying = false;
            this.remove(RemovalReason.DISCARDED);
            return true;
        }
        if (blockInsPos != null && blockInstrument != null) {
            if (level().getBlockState(blockInsPos).getBlock() != blockInstrument) {
                this.remove(RemovalReason.DISCARDED);
                return true;
            }
            if (this.position().distanceToSqr(this.body.position()) > 16) {
                this.remove(RemovalReason.DISCARDED);
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        if (checkForRemoval()) {
            return;
        }
        super.tick();
        if ((blockInsPos == null || blockInstrument == null) && body != null) {  // body is checked to prevent a crash
            this.setPos(body.getX(), body.getY(), body.getZ());
            if (soundController != null) {
                soundController.setPos(getX(), getY(), getZ());
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
