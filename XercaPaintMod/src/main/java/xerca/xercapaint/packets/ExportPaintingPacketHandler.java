package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import xerca.xercapaint.CommandExport;

public class ExportPaintingPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(ExportPaintingPacket msg) {
        Minecraft m = Minecraft.getInstance();
        if(m.player != null) {
            if (CommandExport.doExport(m.player, msg.getName())){
                m.player.sendSystemMessage(Component.translatable("xercapaint.export.success", msg.getName()).withStyle(ChatFormatting.GREEN));
            }else{
                m.player.sendSystemMessage(Component.translatable("xercapaint.export.fail", msg.getName()).withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ExportPaintingPacket packet = ExportPaintingPacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }
}
