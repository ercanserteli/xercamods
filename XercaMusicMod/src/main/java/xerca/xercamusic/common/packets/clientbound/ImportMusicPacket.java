package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

public class ImportMusicPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "import_music");
    private String name;
    private boolean messageIsValid;

    public ImportMusicPacket(String name) {
        this.name = name;
    }

    public ImportMusicPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(name);
        return buf;
    }

    public static ImportMusicPacket decode(FriendlyByteBuf buf) {
        ImportMusicPacket result = new ImportMusicPacket();
        try {
            result.name = buf.readUtf(64);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportMusicPacket: " + ioe);
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
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
