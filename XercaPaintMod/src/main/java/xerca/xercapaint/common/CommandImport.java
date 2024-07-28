package xerca.xercapaint.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.packets.ImportPaintingPacket;

public class CommandImport {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("paintimport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> paintImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintImport(CommandSource stack, String name){
        XercaPaint.LOGGER.debug("Paint import called. name: " + name);
        ImportPaintingPacket pack = new ImportPaintingPacket(name);
        try {
            ServerPlayerEntity player = stack.getPlayerOrException();
            XercaPaint.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), pack);
        } catch (CommandSyntaxException e) {
            XercaPaint.LOGGER.debug("Command executor is not a player");
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    public static void doImport(CompoundNBT tag, ServerPlayerEntity player){
        // Sanitizing
        if (!tag.contains("name", 8)) {
            player.sendMessage(new TranslationTextComponent("xercapaint.import.fail.5").withStyle(TextFormatting.RED), Util.NIL_UUID);
            XercaPaint.LOGGER.warn("Broken paint file");
            return;
        }
        String name = tag.getString("name");
        if (!name.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}_[0-9]+$")) {
            player.sendMessage(new TranslationTextComponent("xercapaint.import.fail.5").withStyle(TextFormatting.RED), Util.NIL_UUID);
            XercaPaint.LOGGER.warn("Broken paint file");
            return;
        }
        if ((tag.contains("author", 8) && !tag.contains("title", 8)) ||
             (!tag.contains("author", 8) && tag.contains("title", 8))) {
            player.sendMessage(new TranslationTextComponent("xercapaint.import.fail.5").withStyle(TextFormatting.RED), Util.NIL_UUID);
            XercaPaint.LOGGER.warn("Broken paint file");
            return;
        }
        if (tag.contains("title", 8) && tag.getString("title").length() > 16) {
            tag.putString("title", tag.getString("title").substring(0, 16));
        }
        if (tag.contains("author", 8) && tag.getString("author").length() > 16) {
            tag.putString("author", tag.getString("author").substring(0, 16));
        }
        if (!tag.contains("v", 3)) {
            tag.putInt("v", 1);
        }

        byte canvasType = tag.getByte("ct");
        tag.remove("ct");
        if(tag.getInt("generation") > 0){
            tag.putInt("generation", tag.getInt("generation") + 1);
        }

        if(player.isCreative()){
            ItemStack itemStack;
            switch (CanvasType.fromByte(canvasType)){
                case SMALL:
                    itemStack = new ItemStack(Items.ITEM_CANVAS);
                    break;
                case LONG:
                    itemStack = new ItemStack(Items.ITEM_CANVAS_LONG);
                    break;
                case TALL:
                    itemStack = new ItemStack(Items.ITEM_CANVAS_TALL);
                    break;
                case LARGE:
                    itemStack = new ItemStack(Items.ITEM_CANVAS_LARGE);
                    break;
                default:
                    XercaPaint.LOGGER.error("Invalid canvas type");
                    return;
            }
            itemStack.setTag(tag);
            player.addItem(itemStack);
        }
        else{
            ItemStack mainhand = player.getMainHandItem();
            ItemStack offhand = player.getOffhandItem();

            if(!(mainhand.getItem() instanceof ItemCanvas) || (mainhand.hasTag() && !mainhand.getTag().isEmpty())){
                player.sendMessage(new TranslationTextComponent("xercapaint.import.fail.1").withStyle(TextFormatting.RED), Util.NIL_UUID);
                return;
            }
            if(((ItemCanvas)mainhand.getItem()).getCanvasType() != CanvasType.fromByte(canvasType)){
                ITextComponent type = Items.ITEM_CANVAS.getName(ItemStack.EMPTY);
                switch (CanvasType.fromByte(canvasType)){
                    case LONG:
                        type = Items.ITEM_CANVAS_LONG.getName(ItemStack.EMPTY);
                        break;
                    case TALL:
                        type = Items.ITEM_CANVAS_TALL.getName(ItemStack.EMPTY);
                        break;
                    case LARGE:
                        type = Items.ITEM_CANVAS_LARGE.getName(ItemStack.EMPTY);
                        break;
                }
                player.sendMessage(new TranslationTextComponent("xercapaint.import.fail.2", type).withStyle(TextFormatting.RED), Util.NIL_UUID);
                return;
            }
            if(!ItemPalette.isFull(offhand)){
                player.sendMessage(new TranslationTextComponent("xercapaint.import.fail.3").withStyle(TextFormatting.RED), Util.NIL_UUID);
                return;
            }
            mainhand.setTag(tag);
        }
        player.sendMessage(new TranslationTextComponent("xercapaint.import.success").withStyle(TextFormatting.GREEN), Util.NIL_UUID);
    }
}
