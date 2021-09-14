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
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.ExportMusicPacket;

import java.io.File;
import java.io.IOException;

public class CommandExport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicexport")
                .requires((p) -> p.hasPermission(1))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> paintExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintExport(CommandSourceStack stack, String name){
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
        XercaMusic.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), pack);
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
                    try {
                        CompoundTag tag = s.getTag().copy();
                        NbtIo.write(tag, new File(filepath));
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
