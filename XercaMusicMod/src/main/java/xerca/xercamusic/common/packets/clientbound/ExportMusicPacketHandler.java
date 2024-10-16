package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import xerca.xercamusic.common.CommandExport;

public class ExportMusicPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<ExportMusicPacket> {

    private static void processMessage(ExportMusicPacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) {
            if (CommandExport.doExport(player, msg.name())) {
                player.sendSystemMessage(Component.translatable("xercamusic.export.success", msg.name()).withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.translatable("xercamusic.export.fail").withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public void receive(ExportMusicPacket packet, ClientPlayNetworking.Context context) {
        if(packet != null) {
            context.client().execute(()->processMessage(packet));
        }
    }
}
