package xerca.xercapaint.packets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercapaint.Mod;

import java.io.IOException;
import java.nio.file.Path;

public final class ImportPaintingPacketHandler {
    private ImportPaintingPacketHandler() {
    }

    private static void processMessage(ImportPaintingPacket msg) {
        String filename = msg.canvasId() + ".paint";
        String filepath = "paintings/" + filename;
        try {
            CompoundTag tag = NbtIo.read(Path.of(filepath));
            if (tag == null) {
                throw new IOException("Painting file did not contain NBT data");
            }
            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(new ImportPaintingSendPacket(tag));
        } catch (IOException e) {
            Mod.LOGGER.error("Could not read painting file {}", filepath, e);
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player != null) {
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.4", filepath).withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void handle(ImportPaintingPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet));
    }
}
