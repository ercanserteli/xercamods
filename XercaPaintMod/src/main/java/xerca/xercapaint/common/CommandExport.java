package xerca.xercapaint.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.common.item.ItemCanvas;

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
        XercaPaint.LOGGER.debug("Paint export called. name: " + name);
        String dir = "paintings";
        String filename = name + ".paint";
        String filepath = dir + "/" + filename;
        if(stack.getEntity() == null){
            XercaPaint.LOGGER.error("Command entity is not found");
        }
        else{
            File directory = new File(dir);
            if (!directory.exists()){
                directory.mkdir();
            }

            for(ItemStack s : stack.getEntity().getHandSlots()){
                if(s.getItem() instanceof ItemCanvas){
                    if(s.hasTag()){
                        try {
                            CompoundTag tag = s.getTag().copy();
                            tag.putByte("ct", (byte)((ItemCanvas) s.getItem()).getCanvasType().ordinal());

                            NbtIo.write(tag, new File(filepath));
                            stack.sendSuccess(new TranslatableComponent("export.success", filepath), false);
                            return 1;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                }
            }
        }
        return 1;
    }
}
