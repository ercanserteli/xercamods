package xerca.xercamusic.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.ExportMusicPacket;

public class CommandExport {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("musicexport")
                .requires((p) -> p.hasPermissionLevel(1))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> musicExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicExport(CommandSource stack, String name){
        XercaMusic.LOGGER.debug("Music export called. name: " + name);
        if(stack.getEntity() == null){
            XercaMusic.LOGGER.error("Command entity is not found");
            return 0;
        }
        Entity commander = stack.getEntity();
        if(!(commander instanceof ServerPlayerEntity)){
            XercaMusic.LOGGER.error("Command entity is not a player");
            return 0;
        }
        ServerPlayerEntity player = (ServerPlayerEntity)commander;
        ExportMusicPacket pack = new ExportMusicPacket(name);
        XercaMusic.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), pack);
        return 1;
    }

    public static boolean doExport(PlayerEntity player, String name){
        String dir = "music_sheets";
        String filename = name + ".sheet";
        String filepath = dir + "/" + filename;
        File directory = new File(dir);
        if (!directory.exists()){
            directory.mkdir();
        }
        ArrayList<ItemStack> itemsInHands = new ArrayList<ItemStack>();
        itemsInHands.add(player.getHeldItemMainhand());
        itemsInHands.add(player.getHeldItemOffhand());
        for(ItemStack s : itemsInHands){
            if(s.getItem() instanceof ItemMusicSheet){
                if(s.hasTag()){
                    CompoundNBT tag = s.getTag().copy();
                    if(tag.contains("id") && tag.contains("ver")){
                        UUID id = tag.getUniqueId("id");
                        int ver = tag.getInt("ver");
                        MusicManagerClient.checkMusicDataAndRun(id, ver, () -> {
                            MusicManager.MusicData data = MusicManagerClient.getMusicData(id, ver);
                            if(data != null){
                                NoteEvent.fillNBTFromArray(data.notes, tag);
                                try {
                                    CompressedStreamTools.write(tag, new File(filepath));
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
