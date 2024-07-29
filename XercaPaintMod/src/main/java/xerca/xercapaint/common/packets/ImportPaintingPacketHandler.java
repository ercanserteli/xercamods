package xerca.xercapaint.common.packets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercapaint.common.XercaPaint;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class ImportPaintingPacketHandler {
    public static void handle(final ImportPaintingPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(ImportPaintingPacket msg) {
        String filename = msg.getName() + ".paint";
        String filepath = "paintings/" + filename;
        try {
            CompoundTag tag = NbtIo.read(new File(filepath));

            ImportPaintingSendPacket pack = new ImportPaintingSendPacket(tag);
            XercaPaint.NETWORK_HANDLER.sendToServer(pack);
        } catch (IOException e) {
            e.printStackTrace();
            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null) {
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.4", filepath).withStyle(ChatFormatting.RED));
            }
        }
    }
}
