package xerca.xercapaint.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.Mod;

public record ImportPaintingSendPacket(CompoundTag tag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImportPaintingSendPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("import_painting_send"));
    public static final StreamCodec<FriendlyByteBuf, ImportPaintingSendPacket> PACKET_CODEC = StreamCodec.ofMember(ImportPaintingSendPacket::encode, ImportPaintingSendPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public static ImportPaintingSendPacket decode(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        return new ImportPaintingSendPacket(tag);
    }

    public ImportPaintingSendPacket {
        if (tag != null) {
            tag = tag.copy();
        }
    }

    @Override
    public CompoundTag tag() {
        if (tag == null) {
            return null;
        }
        return tag.copy();
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

