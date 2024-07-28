package xerca.xercapaint.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
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
        if(CommandExport.doExport(Minecraft.getInstance().player, msg.getName())){
            Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("xercapaint.export.success", msg.getName()).withStyle(TextFormatting.GREEN), Util.NIL_UUID);
        }else{
            Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("xercapaint.export.fail", msg.getName()).withStyle(TextFormatting.RED), Util.NIL_UUID);
        }
    }
}
