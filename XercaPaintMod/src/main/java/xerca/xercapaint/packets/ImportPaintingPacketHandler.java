package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import xerca.xercapaint.Mod;

import java.io.IOException;
import java.nio.file.Path;

public class ImportPaintingPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<ImportPaintingPacket> {

    private static void processMessage(ImportPaintingPacket msg) {
        String filename = msg.canvasId() + ".paint";
        String filepath = "paintings/" + filename;
        try {
            CompoundTag tag = NbtIo.read(Path.of(filepath));
            if (tag == null) {
                throw new IOException("Painting file did not contain NBT data");
            }
            ClientPlayNetworking.send(new ImportPaintingSendPacket(tag));
        } catch (IOException e) {
            Mod.LOGGER.error("Could not read painting file {}", filepath, e);
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player != null) {
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.4", filepath).withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public void receive(ImportPaintingPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> processMessage(packet));
    }
}
