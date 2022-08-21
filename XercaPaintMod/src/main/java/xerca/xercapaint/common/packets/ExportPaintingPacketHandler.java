package xerca.xercapaint.common.packets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercapaint.common.CommandExport;

import java.util.function.Supplier;

public class ExportPaintingPacketHandler {
    public static void handle(final ExportPaintingPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(ExportPaintingPacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) {
            if (CommandExport.doExport(player, msg.getName())) {
                player.sendSystemMessage(Component.translatable("export.success", msg.getName()).withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.translatable("export.fail").withStyle(ChatFormatting.RED));
            }
        }
    }
}
