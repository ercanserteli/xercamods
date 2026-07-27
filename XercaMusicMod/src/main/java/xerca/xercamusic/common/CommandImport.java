package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.ImportMusicPacket;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.Collections;
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

        notes = loadAndSendMusicData(tag, notes, player);
        if (notes.isEmpty()) {
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

        return true;
    }

    private static List<NoteEvent> loadAndSendMusicData(CompoundTag tag, List<NoteEvent> notes, ServerPlayer player) {
        if (tag.contains(KEY_ID) && tag.contains(KEY_VERSION)) {
            UUID id = tag.getUUID(KEY_ID);
            int ver = tag.getInt(KEY_VERSION);

            if (notes == null) {
                // maybe it was sent in parts
                notes = MusicManager.getFinishedNotesFromBuffer(id);
                if (notes == null) {
                    return Collections.emptyList();
                }
            }

            MusicManager.setMusicData(id, ver, notes, player.server);
            sendToClient(player, new MusicDataResponsePacket(id, ver, notes));
            return notes;
        }

        if (tag.contains(KEY_MUSIC_OLD)) {
            // old version
            Mod.LOGGER.info("Old music file version");
            List<NoteEvent> converted = convertFromOld(tag, player.server);
            UUID id = tag.getUUID(KEY_ID);
            int ver = tag.getInt(KEY_VERSION);
            sendToClient(player, new MusicDataResponsePacket(id, ver, converted));
            return converted;
        }

        Mod.LOGGER.warn("Broken music file");
        return Collections.emptyList();
    }

    private static boolean giveImportedSheetToPlayer(CompoundTag tag, ServerPlayer player) {
        if (player.isCreative()) {
            ItemStack itemStack = new ItemStack(Items.MUSIC_SHEET);
            itemStack.setTag(tag);
            player.addItem(itemStack);
            return true;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof ItemMusicSheet) || (mainHandItem.hasTag() && mainHandItem.getTag() != null && !mainHandItem.getTag().isEmpty())) {
            player.sendSystemMessage(translatable("xercamusic.import.fail.1").withStyle(ChatFormatting.RED));
            return false;
        }

        mainHandItem.setTag(tag);
        return true;
    }
}
