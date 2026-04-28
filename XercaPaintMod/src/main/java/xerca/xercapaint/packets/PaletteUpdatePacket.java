package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xerca.xercapaint.Mod;
import xerca.xercapaint.PaletteUtil;

public record PaletteUpdatePacket(PaletteUtil.CustomColor[] paletteColors) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PaletteUpdatePacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("palette_update"));
    public static final StreamCodec<FriendlyByteBuf, PaletteUpdatePacket> PACKET_CODEC = StreamCodec.ofMember(PaletteUpdatePacket::encode, PaletteUpdatePacket::decode);

    public void encode(FriendlyByteBuf buf) {
        for (PaletteUtil.CustomColor color : paletteColors) {
            color.writeToBuffer(buf);
        }
    }

    public static PaletteUpdatePacket decode(FriendlyByteBuf buf) {
        PaletteUtil.CustomColor[] paletteColors = new PaletteUtil.CustomColor[12];
        for (int i = 0; i < paletteColors.length; i++) {
            paletteColors[i] = new PaletteUtil.CustomColor(buf);
        }
        return new PaletteUpdatePacket(paletteColors);
    }

    public PaletteUpdatePacket {
        paletteColors = paletteColors.clone();
    }

    @Override
    public PaletteUtil.CustomColor[] paletteColors() {
        return paletteColors.clone();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

