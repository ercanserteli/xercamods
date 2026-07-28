package xerca.xercamusic.common;

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
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.clientbound.ExportMusicPacket;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.sendToClient;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_ID;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_VERSION;

public final class CommandExport {
    private CommandExport() {
    }

    private static boolean ensureDirectoryExists(File directory) {
        if (directory.exists()) {
            return directory.isDirectory();
        }
        return directory.mkdirs();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicexport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(p -> musicExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicExport(CommandSourceStack stack, String name) {
        XercaMusic.LOGGER.debug("Music export called. name: {}", name);
        if (stack.getEntity() == null) {
            XercaMusic.LOGGER.error("Command entity is not found");
            return 0;
        }
        Entity commander = stack.getEntity();
        if (!(commander instanceof ServerPlayer player)) {
            XercaMusic.LOGGER.error("Command entity is not a player");
            return 0;
        }

        ExportMusicPacket pack = new ExportMusicPacket(name);
        sendToClient(player, pack);
        return 1;
    }

    public static boolean doExport(Player player, String name) {
        String dir = "music_sheets";
        String filename = name + ".sheet";
        String filepath = dir + "/" + filename;
        File directory = new File(dir);
        if (!ensureDirectoryExists(directory)) {
            XercaMusic.LOGGER.warn("Could not create music export directory: {}", directory.getAbsolutePath());
            return false;
        }

        for (ItemStack s : player.getHandSlots()) {
            if (s.getItem() instanceof ItemMusicSheet && s.hasTag() && s.getTag() != null
                    && s.getTag().hasUUID(KEY_ID) && s.getTag().contains(KEY_VERSION)) {
                CompoundTag tag = s.getTag().copy();
                UUID id = tag.getUUID(KEY_ID);
                int ver = tag.getInt(KEY_VERSION);
                MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                    MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                    if (data != null) {
                        NoteEvent.fillNBTFromArray(data.notes(), tag);
                        if (data.volumeMarkers() != null && !data.volumeMarkers().isEmpty()) {
                            VolumeMarker.fillNBTFromArray(data.volumeMarkers(), tag);
                        }
                        try {
                            NbtIo.write(tag, new File(filepath));
                        } catch (IOException e) {
                            XercaMusic.LOGGER.error("Failed to write music sheet file {}", filepath, e);
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }
}
