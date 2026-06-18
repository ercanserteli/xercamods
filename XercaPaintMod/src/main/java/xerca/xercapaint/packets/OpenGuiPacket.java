package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import xerca.xercapaint.Mod;

public record OpenGuiPacket(int easelId, boolean allowed, boolean edit,
                            InteractionHand hand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenGuiPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("open_gui"));
    public static final StreamCodec<FriendlyByteBuf, OpenGuiPacket> PACKET_CODEC = StreamCodec.ofMember(OpenGuiPacket::encode, OpenGuiPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(easelId);
        buf.writeBoolean(allowed);
        buf.writeBoolean(edit);
        buf.writeByte(switch (hand) {
            case MAIN_HAND -> 0;
            case OFF_HAND -> 1;
        });
    }

    public static OpenGuiPacket decode(FriendlyByteBuf buf) {
        int easelId = buf.readInt();
        boolean allowed = buf.readBoolean();
        boolean edit = buf.readBoolean();
        byte handOrdinal = buf.readByte();
        InteractionHand hand = handOrdinal == 1 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        return new OpenGuiPacket(easelId, allowed, edit, hand);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

