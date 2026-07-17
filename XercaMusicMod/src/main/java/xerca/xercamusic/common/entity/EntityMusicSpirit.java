package xerca.xercamusic.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.client.SoundController;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.block.BlockInstrument;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemBlockInstrument;
import xerca.xercamusic.common.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static xerca.xercamusic.common.item.ItemMusicSheet.*;

public class EntityMusicSpirit extends Entity {
    private final ArrayList<NoteEvent> notes = new ArrayList<>();
    private final ArrayList<VolumeMarker> volumeMarkers = new ArrayList<>();
    private @Nullable Player body;
    private @Nullable ItemStack note;
    private @Nullable IItemInstrument instrument;
    private int length;
    private float volume;
    private byte bps;
    private boolean isPlaying = true;
    private @Nullable BlockInstrument blockInstrument;
    private @Nullable BlockPos blockInsPos;
    private @Nullable SoundController soundController;

    private static byte sanitizeBps(int bps) {
        return (byte) Math.clamp(bps, 1, 50);
    }

    private static int sanitizeLengthBeats(int beats) {
        return Math.max(0, beats);
    }

    private static float sanitizeVolume(float volume) {
        return Math.clamp(volume, 0.0f, 1.0f);
    }

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
        if (instrumentId >= 0 && instrumentId < Items.INSTRUMENTS.size()) {
            IItemInstrument itemInstrument = Items.INSTRUMENTS.get(instrumentId);
            if (itemInstrument instanceof ItemBlockInstrument itemBlockInstrument) {
                this.blockInstrument = (BlockInstrument) itemBlockInstrument.getBlock();
                this.blockInsPos = pos;
                setPos(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5);
                return;
            }
        }

        Level level = this.level();
        if (level.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
            Block block = level.getBlockState(pos).getBlock();
            if (block instanceof BlockInstrument foundInstrument) {
                this.blockInstrument = foundInstrument;
                this.blockInsPos = pos;
                setPos(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5);
                return;
            }
        }

        Mod.LOGGER.warn("Did not find a block instrument at the set position");
        blockInstrument = null;
        blockInsPos = null;
    }

    private boolean isBodyHandLegit() {
        if (body == null) {
            return false;
        }
        ItemStack mainStack = body.getMainHandItem();
        ItemStack offStack = body.getOffhandItem();
        if (blockInstrument != null && blockInsPos != null) {
            return mainStack.getItem() == Items.MUSIC_SHEET || offStack.getItem() == Items.MUSIC_SHEET;
        } else {
            return offStack.getItem() == Items.MUSIC_SHEET && Objects.equals(mainStack.getItem(), instrument);
        }
    }

    private void setNoteFromBody() {
        if (body == null) {
            Mod.LOGGER.warn("Body is null in MusicSpirit setNoteFromBody");
            return;
        }
        ItemStack mainStack = body.getMainHandItem();
        ItemStack offStack = body.getOffhandItem();
        if (mainStack.getItem() == Items.MUSIC_SHEET) {
            this.note = mainStack;
        } else if (offStack.getItem() == Items.MUSIC_SHEET) {
            this.note = offStack;
        } else {
            Mod.LOGGER.warn("No music sheet found on body");
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        notes.clear();
        NoteEvent.fillArrayFromNBT(notes, input);
        this.length = sanitizeLengthBeats(input.getIntOr(KEY_LENGTH, 0));
        this.bps = sanitizeBps(input.getIntOr(KEY_BPS, 0));
        this.volume = sanitizeVolume(input.getFloatOr(KEY_VOLUME, 0.0f));
        this.isPlaying = input.getBooleanOr("playing", false);
        if (input.getInt("bX").isPresent() && input.getInt("bY").isPresent() && input.getInt("bZ").isPresent() && input.getInt("bIns").isPresent()) {
            setBlockPosAndInstrument(new BlockPos(input.getIntOr("bX", 0), input.getIntOr("bY", 0), input.getIntOr("bZ", 0)), input.getIntOr("bIns", 0));
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        NoteEvent.fillNBTFromArray(notes, output);
        output.putInt(KEY_LENGTH, length);
        output.putByte(KEY_BPS, bps);
        output.putFloat(KEY_VOLUME, volume);
        output.putBoolean("playing", isPlaying);
        if (blockInstrument != null && blockInsPos != null) {
            output.putInt("bX", blockInsPos.getX());
            output.putInt("bY", blockInsPos.getY());
            output.putInt("bZ", blockInsPos.getZ());
            output.putInt("bIns", blockInstrument.getItemInstrument().getInstrumentId());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        if (body == null) {
            Mod.LOGGER.error("Body is null on EntityMusicSpirit.getAddEntityPacket!");
            return new ClientboundAddEntityPacket(this, serverEntity, 0);
        }

        int data = blockInstrument == null ? body.getId() : -body.getId();
        return new ClientboundAddEntityPacket(this, serverEntity, data);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        int data = packet.getData();
        if (data == 0) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        int bodyId = data > 0 ? data : -data;
        int biX = -1;
        int biY = -10000;
        int biZ = -1;
        int bIns = -1;
        if (data < 0) {
            biX = (int) Math.round(packet.getX() - 0.5);
            biY = (int) Math.round(packet.getY() + 0.5);
            biZ = (int) Math.round(packet.getZ() - 0.5);
        }
        this.buildFromSpawnData(bodyId, biX, biY, biZ, bIns);
    }

    public void buildFromSpawnData(int bodyId, int bx, int by, int bz, int bIns) {
        Entity ent = level().getEntity(bodyId);
        if (ent instanceof Player player) {
            body = player;
        }
        if (by > -10000) {
            setBlockPosAndInstrument(new BlockPos(bx, by, bz), bIns);
        }

        if (this.blockInstrument != null && blockInsPos != null) {
            this.instrument = this.blockInstrument.getItemInstrument();
            this.setNoteFromBody();
        } else if (body != null) {
            Item item = body.getMainHandItem().getItem();
            if (item instanceof IItemInstrument ins) {
                this.instrument = ins;
                this.note = body.getOffhandItem();
                this.setPos(body.getX(), body.getY(), body.getZ());
            } else {
                Mod.LOGGER.warn("Could not find instrument when spawning music spirit!");
                return;
            }
        }

        ItemStack note = this.note;
        if (note == null || !level().isClientSide()) {
            return;
        }
        UUID id = note.get(Items.SHEET_ID);
        int ver = note.getOrDefault(Items.SHEET_VERSION, -1);
        length = note.getOrDefault(Items.SHEET_LENGTH, 0);
        if (id == null || ver < 0 || length <= 0) {
            return;
        }

        bps = sanitizeBps(note.getOrDefault(Items.SHEET_BPS, (byte) 8));
        volume = sanitizeVolume(note.getOrDefault(Items.SHEET_VOLUME, 1.f));
        MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
            MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
            if (data != null) {
                notes.addAll(data.notes());
                if (data.volumeMarkers() != null) {
                    volumeMarkers.addAll(data.volumeMarkers());
                }
            }

            if (this.instrument == null) {
                return;
            }
            soundController = new SoundController(notes, volumeMarkers, getX(), getY(), getZ(), this.instrument, bps, volume, getId());
            soundController.start();
        });
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No need for synching data
    }

    @Override
    public void onClientRemoval() {
        if (soundController != null) {
            soundController.setStop();
        }
    }

    private boolean checkForRemoval() {
        if (this.level().isClientSide()) {
            return false;
        }
        if (this.body == null || !isPlaying) {
            this.remove(RemovalReason.DISCARDED);
            return true;
        }
        if (!isBodyHandLegit()) {
            isPlaying = false;
            this.remove(RemovalReason.DISCARDED);
            return true;
        }
        BlockPos insPos = this.blockInsPos;
        if (insPos != null && this.blockInstrument != null) {
            if (!Objects.equals(level().getBlockState(insPos).getBlock(), this.blockInstrument)) {
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

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        return false;
    }

    @Nullable
    public Player getBody() {
        return body;
    }

}
