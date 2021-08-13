package xerca.xercapaint.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

import java.io.File;
import java.io.IOException;

public class CommandImport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("paintimport")
                        .requires((p) -> p.hasPermission(1))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> paintImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintImport(CommandSourceStack stack, String name){
        XercaPaint.LOGGER.debug("Paint import called. name: " + name);
        String filename = name + ".paint";
        String filepath = "paintings/" + filename;
        try {
            ServerPlayer player = stack.getPlayerOrException();

            CompoundTag tag = NbtIo.read(new File(filepath));
            byte canvasType = tag.getByte("ct");
            tag.remove("ct");
            if(tag.contains("generation")){
                tag.putInt("generation", tag.getInt("generation") + 1);
            }

            if(player.isCreative()){
                ItemStack itemStack;
                switch (CanvasType.fromByte(canvasType)){
                    case SMALL -> itemStack = new ItemStack(Items.ITEM_CANVAS);
                    case LONG -> itemStack = new ItemStack(Items.ITEM_CANVAS_LONG);
                    case TALL -> itemStack = new ItemStack(Items.ITEM_CANVAS_TALL);
                    case LARGE -> itemStack = new ItemStack(Items.ITEM_CANVAS_LARGE);
                    default -> {
                        XercaPaint.LOGGER.error("Invalid canvas type");
                        return 0;
                    }
                }
                itemStack.setTag(tag);
                player.addItem(itemStack);
            }
            else{
                ItemStack mainhand = player.getMainHandItem();
                ItemStack offhand = player.getOffhandItem();

                if(!(mainhand.getItem() instanceof ItemCanvas) || (mainhand.hasTag() && !mainhand.getTag().isEmpty())){
                    stack.sendFailure(new TranslatableComponent("import.fail.1"));
                    return 0;
                }
                if(((ItemCanvas)mainhand.getItem()).getCanvasType() != CanvasType.fromByte(canvasType)){
                    Component type = Items.ITEM_CANVAS.getName(ItemStack.EMPTY);
                    switch (CanvasType.fromByte(canvasType)){
                        case LONG -> type = Items.ITEM_CANVAS_LONG.getName(ItemStack.EMPTY);
                        case TALL -> type = Items.ITEM_CANVAS_TALL.getName(ItemStack.EMPTY);
                        case LARGE -> type = Items.ITEM_CANVAS_LARGE.getName(ItemStack.EMPTY);
                    }
                    stack.sendFailure(new TranslatableComponent("import.fail.2", type));
                    return 0;
                }
                if(!ItemPalette.isFull(offhand)){
                    stack.sendFailure(new TranslatableComponent("import.fail.3"));
                    return 0;
                }
                mainhand.setTag(tag);
            }
            stack.sendSuccess(new TranslatableComponent("import.success", filepath), false);
        } catch (IOException | CommandSyntaxException e) {
            stack.sendFailure(new TranslatableComponent("import.fail.4", filepath));
        }
        return 1;
    }
}
