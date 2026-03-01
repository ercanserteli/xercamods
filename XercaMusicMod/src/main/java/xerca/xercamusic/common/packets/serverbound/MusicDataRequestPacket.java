package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.packets.IPacket;

import java.util.UUID;

public class MusicDataRequestPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(Mod.MODID, "music_data_request");
    private UUID musicId;
    private int version;
    private boolean messageIsValid;

    public MusicDataRequestPacket(UUID musicId, int version) {
        this.musicId = musicId;
        this.version = version;
    }

    public MusicDataRequestPacket() {
        this.messageIsValid = false;
    }

    public static MusicDataRequestPacket decode(FriendlyByteBuf buf) {
        MusicDataRequestPacket result = new MusicDataRequestPacket();
        try {
            result.musicId = buf.readUUID();
            result.version = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            Mod.LOGGER.error("Exception while reading MusicDataRequestPacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(getMusicId());
        buf.writeInt(getVersion());
        return buf;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }


    public UUID getMusicId() {
        return musicId;
    }

    @SuppressWarnings("unused")
    public void setMusicId(UUID musicId) {
        this.musicId = musicId;
    }

    public int getVersion() {
        return version;
    }

    @SuppressWarnings("unused")
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
