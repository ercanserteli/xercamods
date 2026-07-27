package xerca.xercapaint.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.network.NetworkSender;
import xerca.xercapaint.common.packets.ExportPaintingPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CommandExport {
    private CommandExport() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("paintexport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(p -> paintExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintExport(CommandSourceStack stack, String name) {
        XercaPaint.LOGGER.debug("Paint export called. name: {}", name);
        if (stack.getEntity() == null) {
            XercaPaint.LOGGER.error("Command entity is not found");
            return 0;
        }
        Entity commander = stack.getEntity();
        if (!(commander instanceof ServerPlayer player)) {
            XercaPaint.LOGGER.error("Command entity is not a player");
            return 0;
        }

        ExportPaintingPacket pack = new ExportPaintingPacket(name);
        NetworkSender.sendToPlayer(player, pack);
        return 1;
    }

    public static boolean doExport(Player player, String name) {
        String dir = "paintings";
        String filename = name + ".paint";
        String filepath = dir + "/" + filename;
        File directory = new File(dir);
        if (!directory.exists()) {
            try {
                Files.createDirectories(directory.toPath());
            } catch (IOException e) {
                XercaPaint.LOGGER.error("Could not create paintings directory", e);
                return false;
            }
        }

        for (ItemStack s : player.getHandSlots()) {
            CompoundTag stackTag = s.getTag();
            if (s.getItem() instanceof ItemCanvas itemCanvas && stackTag != null && stackTag.contains(ItemCanvas.TAG_PIXELS)) {
                try {
                    CompoundTag tag = stackTag.copy();
                    tag.putByte("ct", itemCanvas.getCanvasType().toByte());
                    if (itemCanvas.isGlass()) {
                        tag.putBoolean("glass", true);
                    }
                    if (!tag.contains(ItemCanvas.TAG_AUTHOR)) {
                        tag.remove(ItemCanvas.TAG_CANVAS_ID);
                        tag.remove(ItemCanvas.TAG_VERSION);
                        tag.remove(ItemCanvas.TAG_GENERATION);
                        tag.remove(ItemCanvas.TAG_TITLE);
                    }
                    NbtIo.write(tag, new File(filepath));
                    return true;
                } catch (IOException e) {
                    XercaPaint.LOGGER.error("Error while exporting painting", e);
                }
            }
        }
        return false;
    }
}
