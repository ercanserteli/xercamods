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

public class CommandExport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicexport")
                .requires((p) -> p.hasPermission(1))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> musicExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicExport(CommandSourceStack stack, String name){
        XercaMusic.LOGGER.debug("Music export called. name: " + name);
        if(stack.getEntity() == null){
            XercaMusic.LOGGER.error("Command entity is not found");
            return 0;
        }
        Entity commander = stack.getEntity();
        if(!(commander instanceof ServerPlayer player)){
            XercaMusic.LOGGER.error("Command entity is not a player");
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
                if(s.hasTag()){
                    @SuppressWarnings("ConstantConditions") CompoundTag tag = s.getTag().copy();
                    if(tag.contains("id") && tag.contains("ver")){
                        UUID id = tag.getUUID("id");
                        int ver = tag.getInt("ver");
                        MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                            MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                            if(data != null){
                                NoteEvent.fillNBTFromArray(data.notes, tag);
                                try {
                                    NbtIo.write(tag, new File(filepath));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
