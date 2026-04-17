package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.ImportMusicPacket;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.minecraft.network.chat.Component.translatable;
import static xerca.xercamusic.common.Mod.sendToClient;
import static xerca.xercamusic.common.item.ItemMusicSheet.*;

public final class CommandImport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicimport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(p -> musicImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicImport(CommandSourceStack stack, String name) {
        Mod.LOGGER.debug("Music import called. name: {}", name);

        ImportMusicPacket pack = new ImportMusicPacket(name);
        try {
            ServerPlayer player = stack.getPlayerOrException();
            sendToClient(player, pack);
        } catch (CommandSyntaxException e) {
            Mod.LOGGER.warn("Command source is not a player", e);
            return 0;
        }

        return 1;
    }

    public static void doImport(CompoundTag tag, List<NoteEvent> notes, ServerPlayer player) {
        if (!sanitizeTag(tag, player)) {
            return;
        }

        if (!loadAndSendMusicData(tag, notes, player)) {
            // load failed / broken / partial
            return;
        }

        if (!giveImportedSheetToPlayer(tag, player)) {
            return;
        }

        player.sendSystemMessage(translatable("xercamusic.import.success").withStyle(ChatFormatting.GREEN));
    }

    private static boolean sanitizeTag(CompoundTag tag, ServerPlayer player) {
        boolean hasAuthor = tag.contains(KEY_AUTHOR, 8);
        boolean hasTitle = tag.contains(KEY_TITLE, 8);

        // only one of them is present -> broken
        if (hasAuthor ^ hasTitle) {
            player.sendSystemMessage(translatable("xercamusic.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken sheet file");
            return false;
        }

        if (hasTitle) {
            String title = tag.getString(KEY_TITLE);
            if (title.length() > 16) {
                tag.putString(KEY_TITLE, title.substring(0, 16));
            }
        }

        if (hasAuthor) {
            String author = tag.getString(KEY_AUTHOR);
            if (author.length() > 16) {
                tag.putString(KEY_AUTHOR, author.substring(0, 16));
            }
        }

        if (!tag.contains(KEY_VERSION, 3)) {
            tag.putInt(KEY_VERSION, 1);
        }

        if (tag.getInt(KEY_GENERATION) > 0) {
            tag.putInt(KEY_GENERATION, tag.getInt(KEY_GENERATION) + 1);
        }

        List<VolumeMarker> volumeMarkers = readVolumeMarkers(tag);
        if (volumeMarkers != null && !validateVolumeMarkers(volumeMarkers)) {
            player.sendSystemMessage(translatable("xercamusic.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken sheet file: overlapping or invalid volume markers");
            return false;
        }

        return true;
    }

    private static boolean loadAndSendMusicData(CompoundTag tag, List<NoteEvent> notes, ServerPlayer player) {
        if (tag.contains(KEY_ID) && tag.contains(KEY_VERSION)) {
            UUID id = tag.getUUID(KEY_ID);
            int ver = tag.getInt(KEY_VERSION);
            List<VolumeMarker> volumeMarkers = readVolumeMarkers(tag);

            if (notes == null) {
                // maybe it was sent in parts
                notes = MusicManager.getFinishedNotesFromBuffer(id);
                if (notes.isEmpty()) {
                    return false;
                }
            }

            if (!validateNotes(notes, player)) {
                return false;
            }

            MusicManager.setMusicData(id, ver, notes, volumeMarkers, player.server);
            sendToClient(player, new MusicDataResponsePacket(id, ver, notes, volumeMarkers));
            return true;
        }

        if (tag.contains(KEY_MUSIC_OLD)) {
            // old version
            Mod.LOGGER.info("Old music file version");
            List<NoteEvent> converted = convertFromOld(tag, player.server);
            if (!validateNotes(converted, player)) {
                return false;
            }
            UUID id = tag.getUUID(KEY_ID);
            int ver = tag.getInt(KEY_VERSION);
            sendToClient(player, new MusicDataResponsePacket(id, ver, converted, null));
            return true;
        }

        Mod.LOGGER.warn("Broken music file");
        return false;
    }

    private static List<VolumeMarker> readVolumeMarkers(CompoundTag tag) {
        ArrayList<VolumeMarker> volumeMarkers = new ArrayList<>();
        VolumeMarker.fillArrayFromNBT(volumeMarkers, tag);
        return volumeMarkers.isEmpty() ? null : volumeMarkers;
    }

    private static boolean validateNotes(List<NoteEvent> notes, ServerPlayer player) {
        for (NoteEvent note : notes) {
            int noteLength = note.length & 0xFF;
            byte[] glissandoWaypoints = note.getEffectiveWaypoints();
            if (glissandoWaypoints != null && glissandoWaypoints.length > noteLength) {
                player.sendSystemMessage(translatable("xercamusic.import.fail.5").withStyle(ChatFormatting.RED));
                Mod.LOGGER.warn("Broken sheet file: note at time {} has {} glissando points for note length {}",
                        note.time, glissandoWaypoints.length, noteLength);
                return false;
            }
        }
        return true;
    }

    private static boolean validateVolumeMarkers(List<VolumeMarker> volumeMarkers) {
        for (int i = 0; i < volumeMarkers.size(); i++) {
            VolumeMarker marker = volumeMarkers.get(i);
            if (!marker.isValid()) {
                return false;
            }
            for (int j = i + 1; j < volumeMarkers.size(); j++) {
                if (marker.overlaps(volumeMarkers.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean giveImportedSheetToPlayer(CompoundTag tag, ServerPlayer player) {
        if (player.isCreative()) {
            ItemStack itemStack = new ItemStack(Items.MUSIC_SHEET);
            importIntoStack(itemStack, tag);
            player.addItem(itemStack);
            return true;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof ItemMusicSheet) || !ItemMusicSheet.isEmptySheet(mainHandItem)) {
            player.sendSystemMessage(translatable("xercamusic.import.fail.1").withStyle(ChatFormatting.RED));
            return false;
        }

        importIntoStack(mainHandItem, tag);
        return true;
    }


    private static void importIntoStack(ItemStack sheet, CompoundTag tag) {
        sheet.set(Items.SHEET_ID, tag.getUUID(KEY_ID));
        sheet.set(Items.SHEET_GENERATION, tag.getInt(KEY_GENERATION));
        sheet.set(Items.SHEET_VERSION, tag.getInt(KEY_VERSION));
        sheet.set(Items.SHEET_LENGTH, tag.getInt(KEY_LENGTH));
        if (tag.contains(KEY_BPS, Tag.TAG_BYTE)) {
            sheet.set(Items.SHEET_BPS, tag.getByte(KEY_BPS));
        }
        if (tag.contains(KEY_PREV_INSTRUMENT_LOCKED, Tag.TAG_BYTE)) {
            sheet.set(Items.SHEET_PREV_INSTRUMENT_LOCKED, tag.getBoolean(KEY_PREV_INSTRUMENT_LOCKED));
        }
        if (tag.contains(KEY_PREV_INSTRUMENT, Tag.TAG_BYTE)) {
            sheet.set(Items.SHEET_PREV_INSTRUMENT, tag.getByte(KEY_PREV_INSTRUMENT));
        }
        if (tag.contains(KEY_TITLE, Tag.TAG_STRING) && tag.contains(KEY_AUTHOR, Tag.TAG_STRING)) {
            sheet.set(Items.SHEET_TITLE, tag.getString(KEY_TITLE));
            sheet.set(Items.SHEET_AUTHOR, tag.getString(KEY_AUTHOR));
        }
        if (tag.contains(KEY_HIGHLIGHT_INTERVAL, Tag.TAG_BYTE)) {
            sheet.set(Items.SHEET_HIGHLIGHT_INTERVAL, tag.getByte(KEY_HIGHLIGHT_INTERVAL));
        }
        if (tag.contains(KEY_VOLUME, Tag.TAG_FLOAT)) {
            sheet.set(Items.SHEET_VOLUME, tag.getFloat(KEY_VOLUME));
        }
    }
}
