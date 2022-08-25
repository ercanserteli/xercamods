package xerca.xercapaint;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.packets.ExportPaintingPacket;

import java.io.File;
import java.io.IOException;

public class CommandExport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("paintexport")
                .requires((p) -> p.hasPermission(1))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> paintExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintExport(CommandSourceStack stack, String name){
        Mod.LOGGER.debug("Paint export called. name: " + name);
        if(stack.getEntity() == null){
            Mod.LOGGER.error("Command entity is not found");
            return 0;
        }
        Entity commander = stack.getEntity();
        if(!(commander instanceof ServerPlayer player)){
            Mod.LOGGER.error("Command entity is not a player");
            return 0;
        }

        ExportPaintingPacket pack = new ExportPaintingPacket(name);
        ServerPlayNetworking.send(player, Mod.EXPORT_PAINTING_PACKET_ID, pack.encode());
        return 1;
    }

    public static boolean doExport(Player player, String name){
        String dir = "paintings";
        String filename = name + ".paint";
        String filepath = dir + "/" + filename;
        File directory = new File(dir);
        if (!directory.exists()){
            directory.mkdir();
        }

        for(ItemStack s : player.getHandSlots()){
            if(s.getItem() instanceof ItemCanvas){
                if(s.hasTag()){
                    try {
                        CompoundTag tag = s.getTag().copy();
                        tag.putByte("ct", (byte)((ItemCanvas) s.getItem()).getCanvasType().ordinal());
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
