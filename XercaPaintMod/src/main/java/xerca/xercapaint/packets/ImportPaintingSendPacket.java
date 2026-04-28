package xerca.xercapaint.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xerca.xercapaint.Mod;

import java.util.Objects;

public record ImportPaintingSendPacket(CompoundTag tag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImportPaintingSendPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("import_painting_send"));
    public static final StreamCodec<FriendlyByteBuf, ImportPaintingSendPacket> PACKET_CODEC = StreamCodec.ofMember(ImportPaintingSendPacket::encode, ImportPaintingSendPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public static ImportPaintingSendPacket decode(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        if (tag == null) {
            throw new IllegalArgumentException("Missing painting tag in ImportPaintingSendPacket");
        }
        return new ImportPaintingSendPacket(tag);
    }

    public ImportPaintingSendPacket {
        tag = Objects.requireNonNull(tag, "Painting tag").copy();
    }

    @Override
    public CompoundTag tag() {
        return tag.copy();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
