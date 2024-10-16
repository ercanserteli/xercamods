package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;


public record ImportMusicPacket(String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImportMusicPacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "import_music"));
    public static final StreamCodec<FriendlyByteBuf, ImportMusicPacket> PACKET_CODEC = StreamCodec.ofMember(ImportMusicPacket::encode, ImportMusicPacket::decode);

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        return buf;
    }

    public static ImportMusicPacket decode(FriendlyByteBuf buf) {
        try {
            String name = buf.readUtf(64);
            return new ImportMusicPacket(name);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportMusicPacket: " + ioe);
            return null;
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
