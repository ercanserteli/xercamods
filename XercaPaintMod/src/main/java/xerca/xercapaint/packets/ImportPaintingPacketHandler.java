package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import xerca.xercapaint.Mod;

import java.io.File;
import java.io.IOException;

public class ImportPaintingPacketHandler implements ClientPlayNetworking.PlayChannelHandler {

    private static void processMessage(ImportPaintingPacket msg) {
        String filename = msg.getName() + ".paint";
        String filepath = "paintings/" + filename;
        try {
            CompoundTag tag = NbtIo.read(new File(filepath));

            ImportPaintingSendPacket pack = new ImportPaintingSendPacket(tag);
            ClientPlayNetworking.send(Mod.IMPORT_PAINTING_SEND_PACKET_ID, pack.encode());
        } catch (IOException e) {
            e.printStackTrace();
            Minecraft.getInstance().player.sendMessage(new TranslatableComponent("xercapaint.import.fail.4", filepath).withStyle(ChatFormatting.RED), Util.NIL_UUID);
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
