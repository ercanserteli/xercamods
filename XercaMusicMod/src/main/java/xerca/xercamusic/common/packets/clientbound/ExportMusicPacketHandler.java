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
        if (player != null) {
            if (CommandExport.doExport(player, msg.name())) {
                player.displayClientMessage(Component.translatable("xercamusic.export.success", msg.name()).withStyle(ChatFormatting.GREEN), false);
            } else {
                player.displayClientMessage(Component.translatable("xercamusic.export.fail").withStyle(ChatFormatting.RED), false);
            }
        }
    }

    @Override
    public void receive(ExportMusicPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> processMessage(packet));
    }
}
