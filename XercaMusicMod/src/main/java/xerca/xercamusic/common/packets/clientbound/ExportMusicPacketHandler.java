package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.common.CommandExport;

public final class ExportMusicPacketHandler {

    private static void processMessage(ExportMusicPacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (CommandExport.doExport(player, msg.name())) {
                player.sendSystemMessage(Component.translatable("xercamusic.export.success", msg.name()).withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.translatable("xercamusic.export.fail").withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void handle(ExportMusicPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet));
    }
}
