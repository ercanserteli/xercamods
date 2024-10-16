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
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.ExportPaintingPacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CommandExport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("paintexport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> paintExport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintExport(CommandSourceStack stack, String name){
        Mod.LOGGER.debug("Paint export called. name: {}", name);
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
        ServerPlayNetworking.send(player, pack);
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
                List<Integer> pixels = s.get(Items.CANVAS_PIXELS);
                String canvasId = s.get(Items.CANVAS_ID);
                if(pixels != null && canvasId != null){
                    try {
                        int version = s.getOrDefault(Items.CANVAS_VERSION, 1);
                        int generation = s.getOrDefault(Items.CANVAS_GENERATION, 0);
                        String title = s.get(Items.CANVAS_TITLE);
                        String author = s.get(Items.CANVAS_AUTHOR);

                        CompoundTag tag = new CompoundTag();

                        tag.putIntArray("pixels", pixels);
                        tag.putString("name", canvasId);
                        tag.putInt("v", version);
                        tag.putInt("generation", generation);
                        tag.putByte("ct", (byte)((ItemCanvas) s.getItem()).getCanvasType().ordinal());
                        if (title != null && author != null) {
                            tag.putString("title", title);
                            tag.putString("author", author);
                        }
                        NbtIo.write(tag, Path.of(filepath));
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
