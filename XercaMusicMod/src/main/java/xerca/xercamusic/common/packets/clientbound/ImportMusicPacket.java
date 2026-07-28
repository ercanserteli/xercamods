package xerca.xercamusic.common.packets.clientbound;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

public class ImportMusicPacket implements IPacket {
    private String name;
    private boolean messageIsValid;

    public ImportMusicPacket(String name) {
        this.name = name;
    }

    public ImportMusicPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(name);
        return buf;
    }

    public static ImportMusicPacket decode(FriendlyByteBuf buf) {
        ImportMusicPacket result = new ImportMusicPacket();
        try {
            result.name = buf.readUtf(64);
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading ImportMusicPacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }}
