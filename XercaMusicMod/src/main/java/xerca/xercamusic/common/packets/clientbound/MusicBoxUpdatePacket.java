package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.Items;

import java.util.UUID;


public record MusicBoxUpdatePacket(BlockPos pos, String instrumentId, boolean sheetSent, boolean noSheet, UUID sheetId, int version, byte bps, int length, float volume) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MusicBoxUpdatePacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "music_box_update"));
    public static final StreamCodec<FriendlyByteBuf, MusicBoxUpdatePacket> PACKET_CODEC = StreamCodec.ofMember(MusicBoxUpdatePacket::encode, MusicBoxUpdatePacket::decode);

    public static MusicBoxUpdatePacket create(BlockPos pos, ItemStack sheetStack, Item itemInstrument) {
        String instrumentId = "";
        if (itemInstrument != null) {
            ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(itemInstrument);
            instrumentId = resourcelocation.toString();
        }

        if (sheetStack != null) {
            UUID sheetId = sheetStack.get(Items.SHEET_ID);
            int version = sheetStack.getOrDefault(Items.SHEET_VERSION, -1);
            byte bps = sheetStack.getOrDefault(Items.SHEET_BPS, (byte)0);
            int length = sheetStack.getOrDefault(Items.SHEET_LENGTH, 0);
            float volume = sheetStack.getOrDefault(Items.SHEET_VOLUME, 1.f);
            if (sheetId != null && version >= 0 && bps > 0 && length > 0) {
                return new MusicBoxUpdatePacket(pos, instrumentId, true, false, sheetId, version, bps, length, volume);
            }
            else {
                return new MusicBoxUpdatePacket(pos, instrumentId, true, true, sheetId, version, bps, length, volume);
            }
        }
        return new MusicBoxUpdatePacket(pos, instrumentId, false, false,null, 0, (byte)0, 0, 0);
    }

    public static MusicBoxUpdatePacket decode(FriendlyByteBuf buf) {
        try {
            BlockPos pos = buf.readBlockPos();
            String instrumentId = buf.readUtf(255);
            boolean noSheet = buf.readBoolean();
            boolean sheetSent = buf.readBoolean();
            if (sheetSent) {
                if (!noSheet) {
                    UUID sheetId = buf.readUUID();
                    int version = buf.readInt();
                    byte bps = buf.readByte();
                    int length = buf.readInt();
                    float volume = buf.readFloat();
                    return new MusicBoxUpdatePacket(pos, instrumentId, true, false, sheetId, version, bps, length, volume);
                }
                else {
                    return new MusicBoxUpdatePacket(pos, instrumentId, true, true, null, 0, (byte)0, 0, 0);
                }
            }
            else {
                return new MusicBoxUpdatePacket(pos, instrumentId, false, true, null, 0, (byte)0, 0, 0);
            }
        } catch (IndexOutOfBoundsException ioe) {
            Mod.LOGGER.error("Exception while reading MusicBoxUpdatePacket:", ioe);
            return null;
        }
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(instrumentId);
        buf.writeBoolean(noSheet);
        buf.writeBoolean(sheetSent);
        if (sheetSent && !noSheet) {
            buf.writeUUID(sheetId);
            buf.writeInt(version);
            buf.writeByte(bps);
            buf.writeInt(length);
            buf.writeFloat(volume);
        }
        return buf;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
