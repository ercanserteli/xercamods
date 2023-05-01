package xerca.xercapaint.common;

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
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.packets.ExportPaintingPacket;

import java.io.File;
import java.io.IOException;

public class CommandExport {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("paintexport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> paintExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintExport(CommandSource stack, String name){
        XercaPaint.LOGGER.debug("Paint export called. name: " + name);
        if(stack.getEntity() == null){
            XercaPaint.LOGGER.error("Command entity is not found");
            return 0;
        }
        Entity commander = stack.getEntity();
        if(!(commander instanceof ServerPlayerEntity)){
            XercaPaint.LOGGER.error("Command entity is not a player");
            return 0;
        }

        ExportPaintingPacket pack = new ExportPaintingPacket(name);
        XercaPaint.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) commander), pack);
        return 1;
    }

    public static boolean doExport(PlayerEntity player, String name){
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
                        CompoundNBT tag = s.getTag().copy();
                        tag.putByte("ct", (byte)((ItemCanvas) s.getItem()).getCanvasType().ordinal());
                        CompressedStreamTools.write(tag, new File(filepath));
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
