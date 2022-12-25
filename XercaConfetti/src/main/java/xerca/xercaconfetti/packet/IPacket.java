package xerca.xercaconfetti.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPacket {
    ResourceLocation getID();
    FriendlyByteBuf encode();
}
