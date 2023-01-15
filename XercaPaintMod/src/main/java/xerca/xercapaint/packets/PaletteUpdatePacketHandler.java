package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.item.Items;

import static xerca.xercapaint.PaletteUtil.writeCustomColorArrayToNBT;

public class PaletteUpdatePacketHandler implements ServerPlayNetworking.PlayChannelHandler {

    private static void processMessage(PaletteUpdatePacket msg, ServerPlayer pl) {
        ItemStack palette = pl.getMainHandItem();

        if (palette.isEmpty() || palette.getItem() != Items.ITEM_PALETTE) {
            palette = pl.getOffhandItem();
            if (palette.isEmpty() || palette.getItem() != Items.ITEM_PALETTE) {
                return;
            }
        }

        CompoundTag paletteComp = palette.getOrCreateTag();
        writeCustomColorArrayToNBT(paletteComp, msg.getPaletteColors());
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        PaletteUpdatePacket packet = PaletteUpdatePacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
