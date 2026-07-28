package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;

public interface IPacket {
    FriendlyByteBuf encode();
}
