package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import xerca.xercapaint.CommandExport;

public class ExportPaintingPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<ExportPaintingPacket> {
    private static void processMessage(ExportPaintingPacket msg) {
        Minecraft m = Minecraft.getInstance();
        if (m.player != null) {
            if (CommandExport.doExport(m.player, msg.canvasId())) {
                m.player.displayClientMessage(Component.translatable("xercapaint.export.success", msg.canvasId()).withStyle(ChatFormatting.GREEN), false);
            } else {
                m.player.displayClientMessage(Component.translatable("xercapaint.export.fail", msg.canvasId()).withStyle(ChatFormatting.RED), false);
            }
        }
    }

    @Override
    public void receive(ExportPaintingPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> processMessage(packet));
    }
}
