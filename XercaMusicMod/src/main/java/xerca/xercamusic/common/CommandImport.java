package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.ImportMusicPacket;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.minecraft.network.chat.Component.translatable;
import static xerca.xercamusic.common.Mod.sendToClient;
import static xerca.xercamusic.common.item.ItemMusicSheet.*;

public final class CommandImport {
    private CommandImport() {
    }

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

    public static void doImport(@Nullable CompoundTag tag, @Nullable List<NoteEvent> notes, @Nullable UUID importBufferId, Player player) {
        if (tag == null) {
            player.sendSystemMessage(translatable("xercamusic.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken sheet file: missing tag");
            return;
        }

        if (!sanitizeTag(tag, player)) {
            return;
        }

        if (!loadAndSendMusicData(tag, notes, importBufferId, player)) {
            // load failed / broken / partial
            return;
        }

        if (!giveImportedSheetToPlayer(tag, player)) {
            return;
        }

        player.sendSystemMessage(translatable("xercamusic.import.success").withStyle(ChatFormatting.GREEN));
    }

    private static boolean sanitizeTag(CompoundTag tag, Player player) {
        boolean hasAuthor = tag.getString(KEY_AUTHOR).isPresent();
        boolean hasTitle = tag.getString(KEY_TITLE).isPresent();
        boolean hasLegacyMusic = tag.contains(KEY_MUSIC_OLD);

        // only one of them is present -> broken
        if (hasAuthor ^ hasTitle) {
            player.sendSystemMessage(translatable("xercamusic.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken sheet file");
            return false;
        }

        if (hasTitle) {
            String title = tag.getStringOr(KEY_TITLE, "");
            if (title.length() > 16) {
                tag.putString(KEY_TITLE, title.substring(0, 16));
            }
        }

        if (hasAuthor) {
            String author = tag.getStringOr(KEY_AUTHOR, "");
            if (author.length() > 16) {
                tag.putString(KEY_AUTHOR, author.substring(0, 16));
            }
        }

        if (!hasLegacyMusic) {
            if (hasTitle) {
                if (tag.getInt(KEY_VERSION).isEmpty()) {
                    tag.putInt(KEY_VERSION, 1);
                }
            } else {
                tag.store(KEY_ID, UUIDUtil.CODEC, UUID.randomUUID());
                tag.putInt(KEY_VERSION, 1);
                tag.putInt(KEY_GENERATION, 0);
            }
        }

        int generation = tag.getIntOr(KEY_GENERATION, 0);
        if (generation > 0 && generation < 3) {
            tag.putInt(KEY_GENERATION, generation + 1);
        }

        List<VolumeMarker> volumeMarkers = readVolumeMarkers(tag);
        if (volumeMarkers != null && !validateVolumeMarkers(volumeMarkers)) {
            player.sendSystemMessage(translatable("xercamusic.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken sheet file: overlapping or invalid volume markers");
            return false;
        }

        return true;
    }

    private static boolean loadAndSendMusicData(CompoundTag tag, @Nullable List<NoteEvent> notes, @Nullable UUID importBufferId, Player player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            Mod.LOGGER.warn("Cannot import music data without a server");
            return false;
        }

        UUID tagId = tag.read(KEY_ID, UUIDUtil.CODEC).orElse(null);
        if (tagId != null && tag.contains(KEY_VERSION)) {
            int ver = tag.getIntOr(KEY_VERSION, 0);
            List<VolumeMarker> volumeMarkers = readVolumeMarkers(tag);

            if (notes == null) {
                // maybe it was sent in parts
                UUID bufferId = importBufferId != null ? importBufferId : tagId;
                notes = MusicManager.getFinishedNotesFromBuffer(bufferId);
                if (notes.isEmpty()) {
                    return false;
                }
            }

            if (!validateNotes(notes, player)) {
                return false;
            }

            MusicManager.setMusicData(tagId, ver, notes, volumeMarkers, server);
            if (player instanceof ServerPlayer serverPlayer) {
                sendToClient(serverPlayer, new MusicDataResponsePacket(tagId, ver, notes, volumeMarkers));
            }
            return true;
        }

        if (tag.contains(KEY_MUSIC_OLD)) {
            // old version
            Mod.LOGGER.info("Old music file version");
            List<NoteEvent> converted = convertFromOld(tag, server);
            if (!validateNotes(converted, player)) {
                return false;
            }
            UUID id = tag.read(KEY_ID, UUIDUtil.CODEC).orElseThrow();
            int ver = tag.getIntOr(KEY_VERSION, 0);
            if (player instanceof ServerPlayer serverPlayer) {
                sendToClient(serverPlayer, new MusicDataResponsePacket(id, ver, converted, null));
            }
            return true;
        }

        Mod.LOGGER.warn("Broken music file");
        return false;
    }

    private static @Nullable List<VolumeMarker> readVolumeMarkers(CompoundTag tag) {
        ArrayList<VolumeMarker> volumeMarkers = new ArrayList<>();
        VolumeMarker.fillArrayFromNBT(volumeMarkers, tag);
        return volumeMarkers.isEmpty() ? null : volumeMarkers;
    }

    private static boolean validateNotes(List<NoteEvent> notes, Player player) {
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

    private static boolean giveImportedSheetToPlayer(CompoundTag tag, Player player) {
        if (player.isCreative()) {
            ItemStack itemStack = new ItemStack(Items.MUSIC_SHEET);
            importIntoStack(itemStack, tag);
            player.addItem(itemStack);
            return true;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof ItemMusicSheet) || !isEmptySheet(mainHandItem)) {
            player.sendSystemMessage(translatable("xercamusic.import.fail.1").withStyle(ChatFormatting.RED));
            return false;
        }

        importIntoStack(mainHandItem, tag);
        return true;
    }


    private static void importIntoStack(ItemStack sheet, CompoundTag tag) {
        tag.read(KEY_ID, UUIDUtil.CODEC).ifPresent(id -> sheet.set(Items.SHEET_ID, id));
        sheet.set(Items.SHEET_GENERATION, tag.getIntOr(KEY_GENERATION, 0));
        sheet.set(Items.SHEET_VERSION, tag.getIntOr(KEY_VERSION, 0));
        sheet.set(Items.SHEET_LENGTH, tag.getIntOr(KEY_LENGTH, 0));
        tag.getByte(KEY_BPS).ifPresent(bps -> sheet.set(Items.SHEET_BPS, bps));
        tag.getByte(KEY_PREV_INSTRUMENT_LOCKED).ifPresent(locked -> sheet.set(Items.SHEET_PREV_INSTRUMENT_LOCKED, locked != 0));
        tag.getByte(KEY_PREV_INSTRUMENT).ifPresent(prev -> sheet.set(Items.SHEET_PREV_INSTRUMENT, prev));
        Optional<String> title = tag.getString(KEY_TITLE);
        Optional<String> author = tag.getString(KEY_AUTHOR);
        if (title.isPresent() && author.isPresent()) {
            sheet.set(Items.SHEET_TITLE, title.get());
            sheet.set(Items.SHEET_AUTHOR, author.get());
        }
        tag.getByte(KEY_HIGHLIGHT_INTERVAL).ifPresent(interval -> sheet.set(Items.SHEET_HIGHLIGHT_INTERVAL, interval));
        tag.getFloat(KEY_VOLUME).ifPresent(volume -> sheet.set(Items.SHEET_VOLUME, volume));
        updateStackSize(sheet);
    }
}
