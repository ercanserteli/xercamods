package xerca.xercapaint.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ImportPaintingSendPacket {
    private CompoundTag tag;
    private boolean messageIsValid;

    public ImportPaintingSendPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ImportPaintingSendPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ImportPaintingSendPacket pkt, FriendlyByteBuf buf) {
        buf.writeNbt(pkt.tag);
    }

    public static ImportPaintingSendPacket decode(FriendlyByteBuf buf) {
        ImportPaintingSendPacket result = new ImportPaintingSendPacket();
        try {
            result.tag = buf.readNbt();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportPaintingSendPacket: " + ioe);
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
