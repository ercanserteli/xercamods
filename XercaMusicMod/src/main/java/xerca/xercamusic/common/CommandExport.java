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
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.ExportMusicPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.sendToClient;

public class CommandExport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicexport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> musicExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicExport(CommandSourceStack stack, String name){
        Mod.LOGGER.debug("Music export called. name: {}", name);
        if(stack.getEntity() == null){
            Mod.LOGGER.error("Command entity is not found");
            return 0;
        }
        Entity commander = stack.getEntity();
        if(!(commander instanceof ServerPlayer player)){
            Mod.LOGGER.error("Command entity is not a player");
            return 0;
        }

        ExportMusicPacket pack = new ExportMusicPacket(name);
        sendToClient(player, pack);
        return 1;
    }

    public static boolean doExport(Player player, String name){
        String dir = "music_sheets";
        String filename = name + ".sheet";
        String filepath = dir + "/" + filename;
        File directory = new File(dir);
        if (!directory.exists()){
            directory.mkdir();
        }

        for(ItemStack s : player.getHandSlots()){
            if(s.getItem() instanceof ItemMusicSheet){
                UUID id = s.get(Items.SHEET_ID);
                int ver = s.getOrDefault(Items.SHEET_VERSION, -1);
                int length = s.getOrDefault(Items.SHEET_LENGTH, 0);
                if (id != null && ver >= 0 && length > 0) {
                    MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                        MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                        if(data != null){
                            CompoundTag tag = new CompoundTag();
                            tag.putInt("ver", ver);
                            tag.putUUID("id", id);
                            tag.putInt("l", length);
                            tag.putInt("generation", s.getOrDefault(Items.SHEET_GENERATION, 0));
                            Byte bps = s.get(Items.SHEET_BPS);
                            if (bps != null) {
                                tag.putByte("bps", bps);
                            }
                            Boolean piLocked = s.get(Items.SHEET_PREV_INSTRUMENT_LOCKED);
                            if (piLocked != null){
                                tag.putBoolean("piLocked", piLocked);
                            }
                            Byte prevIns = s.get(Items.SHEET_PREV_INSTRUMENT);
                            if (prevIns != null) {
                                tag.putByte("prevIns", prevIns);
                            }
                            String title = s.get(Items.SHEET_TITLE);
                            String author = s.get(Items.SHEET_AUTHOR);
                            if (title != null && author != null) {
                                tag.putString("title", title);
                                tag.putString("author", author);
                            }
                            Byte highlightInterval = s.get(Items.SHEET_HIGHLIGHT_INTERVAL);
                            if (highlightInterval != null) {
                                tag.putByte("hl", highlightInterval);
                            }
                            Float volume = s.get(Items.SHEET_VOLUME);
                            if (volume != null) {
                                tag.putFloat("vol", volume);
                            }

                            NoteEvent.fillNBTFromArray(data.notes(), tag);
                            try {
                                NbtIo.write(tag, Path.of(filepath));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                return true;
            }
        }
        return false;
    }
}
