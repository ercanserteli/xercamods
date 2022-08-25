package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

public class MusicEndedPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "music_ended");
    private int playerId;
    private boolean messageIsValid;

    public MusicEndedPacket(int playerId) {
        this.playerId = playerId;
    }

    public MusicEndedPacket() {
        this.messageIsValid = false;
    }

    public static MusicEndedPacket decode(FriendlyByteBuf buf) {
        MusicEndedPacket result = new MusicEndedPacket();
        try {
            result.playerId = buf.readInt();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicEndedPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(playerId);
        return buf;
    }

    public int getPlayerId() {
        return this.playerId;
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
