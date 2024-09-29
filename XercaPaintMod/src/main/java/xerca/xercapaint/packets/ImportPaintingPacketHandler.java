package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import xerca.xercapaint.Mod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ImportPaintingPacketHandler implements ClientPlayNetworking.PlayChannelHandler {

    private static void processMessage(ImportPaintingPacket msg) {
        String filename = msg.getName() + ".paint";
        String filepath = "paintings/" + filename;
        try {
            CompoundTag tag = NbtIo.read(Path.of(filepath));

            ImportPaintingSendPacket pack = new ImportPaintingSendPacket(tag);
            ClientPlayNetworking.send(Mod.IMPORT_PAINTING_SEND_PACKET_ID, pack.encode());
        } catch (IOException e) {
            e.printStackTrace();
            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null) {
                Minecraft.getInstance().player.sendSystemMessage(Component.translatable("xercapaint.import.fail.4", filepath).withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ImportPaintingPacket packet = ImportPaintingPacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }
}
