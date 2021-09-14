package xerca.xercamusic.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ImportMusicSendPacket {
    private CompoundTag tag;
    private boolean messageIsValid;

    public ImportMusicSendPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ImportMusicSendPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ImportMusicSendPacket pkt, FriendlyByteBuf buf) {
        buf.writeNbt(pkt.tag);
    }

    public static ImportMusicSendPacket decode(FriendlyByteBuf buf) {
        ImportMusicSendPacket result = new ImportMusicSendPacket();
        try {
            result.tag = buf.readNbt();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportMusicSendPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
