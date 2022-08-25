package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import xerca.xercamusic.common.packets.serverbound.ImportMusicSendPacket;

import java.io.File;
import java.io.IOException;

import static xerca.xercamusic.client.ClientStuff.sendToServer;

public class ImportMusicPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(ImportMusicPacket msg) {
        String filename = msg.getName() + ".sheet";
        String filepath = "music_sheets/" + filename;
        try {
            CompoundTag tag = NbtIo.read(new File(filepath));

            ImportMusicSendPacket pack = new ImportMusicSendPacket(tag);
            sendToServer(pack);
        } catch (IOException e) {
            e.printStackTrace();
            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null) {
                player.sendMessage(new TranslatableComponent("import.fail.4", filepath).withStyle(ChatFormatting.RED), Util.NIL_UUID);
            }
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ImportMusicPacket packet = ImportMusicPacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }
}
