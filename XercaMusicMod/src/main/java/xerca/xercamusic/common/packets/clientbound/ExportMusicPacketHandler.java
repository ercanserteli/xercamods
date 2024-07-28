package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import xerca.xercamusic.common.CommandExport;

public class ExportMusicPacketHandler implements ClientPlayNetworking.PlayChannelHandler {

    private static void processMessage(ExportMusicPacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) {
            if (CommandExport.doExport(player, msg.getName())) {
                player.sendMessage(new TranslatableComponent("xercamusic.export.success", msg.getName()).withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
            } else {
                player.sendMessage(new TranslatableComponent("xercamusic.export.fail").withStyle(ChatFormatting.RED), Util.NIL_UUID);
            }
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ExportMusicPacket packet = ExportMusicPacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }
}
