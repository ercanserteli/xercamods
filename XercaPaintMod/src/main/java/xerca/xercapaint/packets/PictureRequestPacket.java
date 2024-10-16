package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;

public record PictureRequestPacket(String canvasId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PictureRequestPacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "picture_request"));
    public static final StreamCodec<FriendlyByteBuf, PictureRequestPacket> PACKET_CODEC = StreamCodec.ofMember(PictureRequestPacket::encode, PictureRequestPacket::decode);

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeUtf(canvasId);
        return buf;
    }

    public static PictureRequestPacket decode(FriendlyByteBuf buf) {
        String canvasId = buf.readUtf(64);
        return new PictureRequestPacket(canvasId);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
