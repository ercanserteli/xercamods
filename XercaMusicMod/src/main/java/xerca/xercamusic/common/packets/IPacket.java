package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPacket {
    ResourceLocation getID();
    FriendlyByteBuf encode();
}
