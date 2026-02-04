package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

import java.util.UUID;

public class NotesPartAckFromServerPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "notes_part_ack_from_server");
    private UUID musicId;
    private boolean messageIsValid;

    public NotesPartAckFromServerPacket(UUID musicId) {
        this.musicId = musicId;
    }

    public NotesPartAckFromServerPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(musicId);
        return buf;
    }

    public static NotesPartAckFromServerPacket decode(FriendlyByteBuf buf) {
        NotesPartAckFromServerPacket result = new NotesPartAckFromServerPacket();
        try {
            result.musicId = buf.readUUID();
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading NotesPartAckFromServerPacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public UUID getMusicId() {
        return musicId;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}

