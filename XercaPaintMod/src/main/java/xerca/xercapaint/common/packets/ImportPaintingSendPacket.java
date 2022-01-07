package xerca.xercapaint.common.packets;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class ImportPaintingSendPacket {
    private CompoundNBT tag;
    private boolean messageIsValid;

    public ImportPaintingSendPacket(CompoundNBT tag) {
        this.tag = tag;
    }

    public ImportPaintingSendPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ImportPaintingSendPacket pkt, PacketBuffer buf) {
        buf.writeNbt(pkt.tag);
    }

    public static ImportPaintingSendPacket decode(PacketBuffer buf) {
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

    public CompoundNBT getTag() {
        return tag;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
