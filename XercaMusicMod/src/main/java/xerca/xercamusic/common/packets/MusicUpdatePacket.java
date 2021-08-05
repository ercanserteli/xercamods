package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;

import java.util.Arrays;

public class MusicUpdatePacket {
    private byte[] music;
    private int length;
    private byte pause;
    private boolean signed;
    private String title;
    private byte prevInstrument;
    private boolean prevInsLocked;
    private boolean messageIsValid;

    public MusicUpdatePacket(byte[] music, int length, byte pause, boolean signed, String title, byte prevInstrument, boolean prevInsLocked) {
        this.music = Arrays.copyOfRange(music, 0, length);
        this.length = length;
        this.pause = pause;
        this.signed = signed;
        this.title = title;
        this.prevInstrument = prevInstrument;
        this.prevInsLocked = prevInsLocked;
        XercaMusic.LOGGER.debug("MusicUpdatePacket length: " + length + " music length: " + this.music.length);
    }

    public MusicUpdatePacket() {
        this.messageIsValid = false;
    }

    public static MusicUpdatePacket decode(FriendlyByteBuf buf) {
        MusicUpdatePacket result = new MusicUpdatePacket();
        try {
            result.title = buf.readUtf(255);
            result.signed = buf.readBoolean();
            result.pause = buf.readByte();
            result.length = buf.readInt();
            result.music = new byte[result.length];
            buf.readBytes(result.music, 0, result.length);
            result.prevInstrument = buf.readByte();
            result.prevInsLocked = buf.readBoolean();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicUpdatePacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.title);
        buf.writeBoolean(pkt.signed);
        buf.writeByte(pkt.pause);
        buf.writeInt(pkt.length);
        buf.writeBytes(pkt.music);
        buf.writeByte(pkt.prevInstrument);
        buf.writeBoolean(pkt.prevInsLocked);
    }

    public byte[] getMusic() {
        return music;
    }

    public int getLength() {
        return length;
    }

    public byte getPause() {
        return pause;
    }

    public boolean getSigned() {
        return signed;
    }

    public String getTitle() {
        return title;
    }

    public byte getPrevInstrument() {
        return prevInstrument;
    }

    public boolean getPrevInsLocked() {
        return prevInsLocked;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

}
