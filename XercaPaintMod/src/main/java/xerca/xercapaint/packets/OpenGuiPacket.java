package xerca.xercapaint.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;

public record OpenGuiPacket(int easelId, boolean allowed, boolean edit, InteractionHand hand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenGuiPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("open_gui"));
    public static final StreamCodec<FriendlyByteBuf, OpenGuiPacket> PACKET_CODEC = StreamCodec.ofMember(OpenGuiPacket::encode, OpenGuiPacket::decode);

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeInt(easelId);
        buf.writeBoolean(allowed);
        buf.writeBoolean(edit);
        buf.writeByte(hand.ordinal());
        return buf;
    }

    public static OpenGuiPacket decode(FriendlyByteBuf buf) {
        int easelId = buf.readInt();
        boolean allowed = buf.readBoolean();
        boolean edit = buf.readBoolean();
        int handOrdinal = buf.readByte();
        InteractionHand hand;
        if(InteractionHand.values().length > handOrdinal){
            hand = InteractionHand.values()[handOrdinal];
        }
        else{
            hand = InteractionHand.MAIN_HAND;
        }
        return new OpenGuiPacket(easelId, allowed, edit, hand);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
