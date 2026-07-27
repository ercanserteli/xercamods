package xerca.xercapaint.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.common.XercaPaint;

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
        } catch (RuntimeException ioe) {
            XercaPaint.LOGGER.error("Exception while reading ImportPaintingSendPacket", ioe);
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
