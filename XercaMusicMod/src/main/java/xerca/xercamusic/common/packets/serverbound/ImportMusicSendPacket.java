package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

@SuppressWarnings("unused")
public class ImportMusicSendPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "import_music_send");
    private CompoundTag tag;
    private boolean messageIsValid;

    public ImportMusicSendPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public ImportMusicSendPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(tag);
        return buf;
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

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
