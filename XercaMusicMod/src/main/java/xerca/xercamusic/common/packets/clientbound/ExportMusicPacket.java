package xerca.xercamusic.common.packets.clientbound;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

@SuppressWarnings("unused")
public class ExportMusicPacket implements IPacket {
    private String name;
    private boolean messageIsValid;

    public ExportMusicPacket(String name) {
        this.name = name;
    }

    public ExportMusicPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(name);
        return buf;
    }

    public static ExportMusicPacket decode(FriendlyByteBuf buf) {
        ExportMusicPacket result = new ExportMusicPacket();
        try {
            result.name = buf.readUtf(64);
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading ExportMusicPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public String getName() {
        return name;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }}
