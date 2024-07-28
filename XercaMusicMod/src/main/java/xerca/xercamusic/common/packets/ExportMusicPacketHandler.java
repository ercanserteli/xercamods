package xerca.xercamusic.common.packets;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamusic.common.CommandExport;

public class ExportMusicPacketHandler {
    public static void handle(final ExportMusicPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(ExportMusicPacket msg) {
    	Minecraft mc = Minecraft.getInstance();
        if(CommandExport.doExport(mc.player, msg.getName())){
            mc.player.sendMessage(new TranslationTextComponent("xercamusic.export.success", msg.getName()).mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
        }else{
            mc.player.sendMessage(new TranslationTextComponent("xercamusic.export.fail", msg.getName()).mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
        }
    }
}
