package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.ImportMusicPacket;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.sendToClient;
import static xerca.xercamusic.common.item.ItemMusicSheet.convertFromOld;

public class CommandImport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicimport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> musicImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicImport(CommandSourceStack stack, String name){
        Mod.LOGGER.debug("Music import called. name: {}", name);

        ImportMusicPacket pack = new ImportMusicPacket(name);
        try {
            ServerPlayer player = stack.getPlayerOrException();
            sendToClient(player, pack);
        } catch (CommandSyntaxException e) {
            Mod.LOGGER.debug("Command executor is not a player");
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    public static void doImport(CompoundTag tag, ArrayList<NoteEvent> notes, ServerPlayer player){
        // Sanitizing
        if ((tag.contains("author", 8) && !tag.contains("title", 8)) ||
                (!tag.contains("author", 8) && tag.contains("title", 8))) {
            player.sendSystemMessage(Component.translatable("xercamusic.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken sheet file");
            return;
        }
        if (tag.contains("title", 8) && tag.getString("title").length() > 16) {
            tag.putString("title", tag.getString("title").substring(0, 16));
        }
        if (tag.contains("author", 8) && tag.getString("author").length() > 16) {
            tag.putString("author", tag.getString("author").substring(0, 16));
        }
        if (!tag.contains("ver", 3)) {
            tag.putInt("ver", 1);
        }

        if(tag.getInt("generation") > 0){
            tag.putInt("generation", tag.getInt("generation") + 1);
        }
        if(tag.contains("id") && tag.contains("ver")) {
            UUID id = tag.getUUID("id");
            int ver = tag.getInt("ver");
            if(notes == null) {
                // Get if large note was sent in parts
                notes = MusicManager.getFinishedNotesFromBuffer(id);
                if(notes == null){
                    return;
                }
            }
            MusicManager.setMusicData(id, ver, notes, player.server);

            MusicDataResponsePacket packet = new MusicDataResponsePacket(id, ver, notes);
            sendToClient(player, packet);
        }
        else if(tag.contains("music")){
            // Old version
            Mod.LOGGER.info("Old music file version");
            notes = convertFromOld(tag, player.server);
            UUID id = tag.getUUID("id");
            int ver = tag.getInt("ver");

            MusicDataResponsePacket packet = new MusicDataResponsePacket(id, ver, notes);
            sendToClient(player, packet);
        }
        else {
            Mod.LOGGER.warn("Broken music file");
            return;
        }

        if (player.isCreative()) {
            ItemStack itemStack = new ItemStack(Items.MUSIC_SHEET);
            importIntoStack(itemStack, tag);
            player.addItem(itemStack);
        }
        else {
            ItemStack mainHandItem = player.getMainHandItem();

            if (!(mainHandItem.getItem() instanceof ItemMusicSheet) || !ItemMusicSheet.isEmptySheet(mainHandItem)) {
                player.sendSystemMessage(Component.translatable("xercamusic.import.fail.1").withStyle(ChatFormatting.RED));
                return;
            }
            importIntoStack(mainHandItem, tag);
        }
        player.sendSystemMessage(Component.translatable("xercamusic.import.success").withStyle(ChatFormatting.GREEN));
    }

    private static void importIntoStack(ItemStack sheet, CompoundTag tag) {
        sheet.set(Items.SHEET_ID, tag.getUUID("id"));
        sheet.set(Items.SHEET_GENERATION, tag.getInt("generation"));
        sheet.set(Items.SHEET_VERSION, tag.getInt("ver"));
        sheet.set(Items.SHEET_LENGTH, tag.getInt("l"));
        if (tag.contains("bps", Tag.TAG_BYTE)) {
            sheet.set(Items.SHEET_BPS, tag.getByte("bps"));
        }
        if (tag.contains("piLocked", Tag.TAG_BYTE)) {
            sheet.set(Items.SHEET_PREV_INSTRUMENT_LOCKED, tag.getBoolean("piLocked"));
        }
        if (tag.contains("prevIns", Tag.TAG_BYTE)) {
            sheet.set(Items.SHEET_PREV_INSTRUMENT, tag.getByte("prevIns"));
        }
        if (tag.contains("title", Tag.TAG_STRING) && tag.contains("author", Tag.TAG_STRING)) {
            sheet.set(Items.SHEET_TITLE, tag.getString("title"));
            sheet.set(Items.SHEET_AUTHOR, tag.getString("author"));
        }
        if (tag.contains("hl", Tag.TAG_BYTE)) {
            sheet.set(Items.SHEET_HIGHLIGHT_INTERVAL, tag.getByte("hl"));
        }
        if (tag.contains("vol", Tag.TAG_FLOAT)) {
            sheet.set(Items.SHEET_VOLUME, tag.getFloat("vol"));
        }
    }
}
