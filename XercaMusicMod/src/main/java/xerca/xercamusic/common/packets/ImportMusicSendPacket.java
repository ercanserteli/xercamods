package xerca.xercamusic.common.packets;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class ImportMusicSendPacket {
    private CompoundNBT tag;
    private boolean messageIsValid;

    public ImportMusicSendPacket(CompoundNBT tag) {
        this.tag = tag;
    }

    public ImportMusicSendPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ImportMusicSendPacket pkt, PacketBuffer buf) {
        buf.writeCompoundTag(pkt.tag);
    }

    public static ImportMusicSendPacket decode(PacketBuffer buf) {
        ImportMusicSendPacket result = new ImportMusicSendPacket();
        try {
            result.tag = buf.readCompoundTag();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportMusicSendPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public CompoundNBT getTag() {
        return tag;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
