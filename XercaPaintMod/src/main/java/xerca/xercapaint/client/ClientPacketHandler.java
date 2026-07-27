package xerca.xercapaint.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import xerca.xercapaint.CommandExport;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.packets.ExportPaintingPacket;
import xerca.xercapaint.packets.ImportPaintingPacket;
import xerca.xercapaint.packets.ImportPaintingSendPacket;
import xerca.xercapaint.packets.OpenGuiPacket;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Client-side processing for the mod's client-bound (S2C) payloads. Kept in the client package and
 * referenced only from inside the {@code enqueueWork} lambdas of the (dist-neutral) packet handlers,
 * so the dedicated server never links the client types used in these method bodies.
 */
public final class ClientPacketHandler {
    private ClientPacketHandler() {
    }

    public static void closeGui() {
        Minecraft.getInstance().setScreen(null);
    }

    public static void openGui(OpenGuiPacket msg) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (!msg.allowed()) {
            player.sendSystemMessage(Component.translatable("easel.deny").withStyle(ChatFormatting.RED));
            return;
        }
        Entity entity = player.level().getEntity(msg.easelId());
        if (!(entity instanceof EntityEasel easel)) {
            Mod.LOGGER.error("Could not find easel");
            return;
        }
        ItemStack itemInHand = player.getItemInHand(msg.hand());
        boolean handHoldsPalette = itemInHand.getItem() instanceof ItemPalette;
        if (msg.edit()) {
            if (handHoldsPalette) {
                ModClient.showCanvasGui(easel, itemInHand);
            } else {
                Mod.LOGGER.error("Could not find palette in hand for editing painting");
            }
        } else {
            ModClient.showCanvasGui(easel, ItemStack.EMPTY);
        }
    }

    public static void exportPainting(ExportPaintingPacket msg) {
        Minecraft m = Minecraft.getInstance();
        if (m.player != null) {
            if (CommandExport.doExport(m.player, msg.canvasId())) {
                m.player.sendSystemMessage(Component.translatable("xercapaint.export.success", msg.canvasId()).withStyle(ChatFormatting.GREEN));
            } else {
                m.player.sendSystemMessage(Component.translatable("xercapaint.export.fail", msg.canvasId()).withStyle(ChatFormatting.RED));
            }
        }
    }

    public static void importPainting(ImportPaintingPacket msg) {
        String filename = msg.canvasId() + ".paint";
        String filepath = "paintings/" + filename;
        try {
            CompoundTag tag = NbtIo.read(Path.of(filepath));
            if (tag == null) {
                throw new IOException("Painting file did not contain NBT data");
            }
            PacketDistributor.sendToServer(new ImportPaintingSendPacket(tag));
        } catch (IOException e) {
            Mod.LOGGER.error("Could not read painting file {}", filepath, e);
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.4", filepath).withStyle(ChatFormatting.RED));
            }
        }
    }
}
