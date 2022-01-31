package xerca.xercamusic.common.packets;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamusic.common.XercaMusic;

public class ImportMusicPacketHandler {
    public static void handle(final ImportMusicPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(ImportMusicPacket msg) {
        String filename = msg.getName() + ".sheet";
        String filepath = "music_sheets/" + filename;
        try {
            CompoundNBT tag = CompressedStreamTools.read(new File(filepath));

            ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);
        } catch (IOException e) {
            e.printStackTrace();
            Minecraft mc = Minecraft.getInstance();
            mc.player.sendMessage(new TranslationTextComponent("import.fail.4", filepath).mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
        }
    }
}
