package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.ExportMusicPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.sendToClient;
import static xerca.xercamusic.common.item.ItemMusicSheet.*;

public final class CommandExport {
    private static boolean ensureDirectoryExists(File directory) {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                Mod.LOGGER.warn("Music export path exists but is not a directory: {}", directory.getAbsolutePath());
                return false;
            }
            return true;
        }
        if (!directory.mkdirs()) {
            Mod.LOGGER.warn("Could not create music export directory: {}", directory.getAbsolutePath());
            return false;
        }
        return true;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicexport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(p -> musicExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicExport(CommandSourceStack stack, String name) {
        Mod.LOGGER.debug("Music export called. name: {}", name);
        if (stack.getEntity() == null) {
            Mod.LOGGER.error("Command entity is not found");
            return 0;
        }
        Entity commander = stack.getEntity();
        if (!(commander instanceof ServerPlayer player)) {
            Mod.LOGGER.error("Command entity is not a player");
            return 0;
        }

        ExportMusicPacket pack = new ExportMusicPacket(name);
        sendToClient(player, pack);
        return 1;
    }

    public static boolean doExport(Player player, String name) {
        File directory = new File("music_sheets");
        if (!ensureDirectoryExists(directory)) {
            return false;
        }
        Path filePath = directory.toPath().resolve(name + ".sheet");

        for (ItemStack stack : player.getHandSlots()) {
            if (stack.getItem() instanceof ItemMusicSheet) {
                exportSheetIfValid(stack, filePath);
                return true;
            }
        }

        return false;
    }

    private static void exportSheetIfValid(ItemStack stack, Path filePath) {
        UUID id = stack.get(Items.SHEET_ID);
        int ver = stack.getOrDefault(Items.SHEET_VERSION, -1);
        int length = stack.getOrDefault(Items.SHEET_LENGTH, 0);

        if (id == null || ver < 0 || length <= 0) {
            return;
        }

        MusicManagerClient.checkMusicDataAndRun(id, ver, () -> writeSheetFile(stack, id, ver, length, filePath));
    }

    private static void writeSheetFile(ItemStack stack, UUID id, int ver, int length, Path filePath) {
        MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
        if (data == null) {
            return;
        }

        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_VERSION, ver);
        tag.putUUID(KEY_ID, id);
        tag.putInt(KEY_LENGTH, length);
        tag.putInt(KEY_GENERATION, stack.getOrDefault(Items.SHEET_GENERATION, 0));

        putOptionalByte(stack, Items.SHEET_BPS, tag, KEY_BPS);
        putOptionalBoolean(stack, Items.SHEET_PREV_INSTRUMENT_LOCKED, tag, KEY_PREV_INSTRUMENT_LOCKED);
        putOptionalByte(stack, Items.SHEET_PREV_INSTRUMENT, tag, KEY_PREV_INSTRUMENT);
        putOptionalTitleAuthor(stack, tag);
        putOptionalByte(stack, Items.SHEET_HIGHLIGHT_INTERVAL, tag, KEY_HIGHLIGHT_INTERVAL);
        putOptionalFloat(stack, Items.SHEET_VOLUME, tag, KEY_VOLUME);

        NoteEvent.fillNBTFromArray(data.notes(), tag);
        if (data.volumeMarkers() != null && !data.volumeMarkers().isEmpty()) {
            VolumeMarker.fillNBTFromArray(data.volumeMarkers(), tag);
        }

        try {
            NbtIo.write(tag, filePath);
        } catch (IOException e) {
            Mod.LOGGER.error("Failed to write music sheet file {}: {}", filePath, e);
        }
    }

    private static void putOptionalByte(ItemStack stack, DataComponentType<Byte> key, CompoundTag tag, String tagName) {
        Byte value = stack.get(key);
        if (value != null) {
            tag.putByte(tagName, value);
        }
    }

    private static void putOptionalBoolean(ItemStack stack, DataComponentType<Boolean> key, CompoundTag tag, String tagName) {
        Boolean value = stack.get(key);
        if (value != null) {
            tag.putBoolean(tagName, value);
        }
    }

    private static void putOptionalFloat(ItemStack stack, DataComponentType<Float> key, CompoundTag tag, String tagName) {
        Float value = stack.get(key);
        if (value != null) {
            tag.putFloat(tagName, value);
        }
    }

    private static void putOptionalTitleAuthor(ItemStack stack, CompoundTag tag) {
        String title = stack.get(Items.SHEET_TITLE);
        String author = stack.get(Items.SHEET_AUTHOR);
        if (title != null && author != null) {
            tag.putString(KEY_TITLE, title);
            tag.putString(KEY_AUTHOR, author);
        }
    }
}
