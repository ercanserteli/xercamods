package xerca.xercamusic.common.packets;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.XercaMusic;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

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
            CompoundTag tag = NbtIo.read(new File(filepath));

            ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);
        } catch (IOException e) {
            e.printStackTrace();
            Minecraft.getInstance().player.sendMessage(new TranslatableComponent("import.fail.4", filepath).withStyle(ChatFormatting.RED), Util.NIL_UUID);
        }
    }
}
