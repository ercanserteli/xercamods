package xerca.xercapaint;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.ImportPaintingPacket;

import java.util.Arrays;

public class CommandImport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("paintimport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> paintImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintImport(CommandSourceStack stack, String name){
        Mod.LOGGER.debug("Paint import called. name: {}", name);

        ImportPaintingPacket pack = new ImportPaintingPacket(name);
        try {
            ServerPlayer player = stack.getPlayerOrException();
            ServerPlayNetworking.send(player, pack);
        } catch (CommandSyntaxException e) {
            Mod.LOGGER.debug("Command executor is not a player");
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    public static void doImport(CompoundTag tag, ServerPlayer player){
        // Sanitizing
        if (!tag.contains("name", 8)) {
            player.sendSystemMessage(Component.translatable("xercapaint.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken paint file");
            return;
        }
        String canvasId = tag.getString("name");
        if (!canvasId.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}_[0-9]+$")) {
            player.sendSystemMessage(Component.translatable("xercapaint.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken paint file");
            return;
        }
        if ((tag.contains("author", 8) && !tag.contains("title", 8)) ||
                (!tag.contains("author", 8) && tag.contains("title", 8))) {
            player.sendSystemMessage(Component.translatable("xercapaint.import.fail.5").withStyle(ChatFormatting.RED));
            Mod.LOGGER.warn("Broken paint file");
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

        ItemStack itemStack;
        boolean doAddItem = false;
        if(player.isCreative()){
            CanvasType type = CanvasType.fromByte(canvasType);
            if (type == null) {
                Mod.LOGGER.error("Invalid canvas type");
                return;
            }
            switch (type){
                case SMALL -> itemStack = new ItemStack(Items.ITEM_CANVAS);
                case LONG -> itemStack = new ItemStack(Items.ITEM_CANVAS_LONG);
                case TALL -> itemStack = new ItemStack(Items.ITEM_CANVAS_TALL);
                case LARGE -> itemStack = new ItemStack(Items.ITEM_CANVAS_LARGE);
                default -> {
                    Mod.LOGGER.error("Unknown canvas type");
                    return;
                }
            }
            doAddItem = true;
        }
        else {
            ItemStack mainhand = player.getMainHandItem();
            ItemStack offhand = player.getOffhandItem();

            if(!(mainhand.getItem() instanceof ItemCanvas) || (mainhand.get(Items.CANVAS_PIXELS) != null || mainhand.get(Items.CANVAS_ID) != null)){
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.1").withStyle(ChatFormatting.RED));
                return;
            }
            if(((ItemCanvas)mainhand.getItem()).getCanvasType() != CanvasType.fromByte(canvasType)){
                Component typeName = Items.ITEM_CANVAS.getName(ItemStack.EMPTY);
                CanvasType type = CanvasType.fromByte(canvasType);
                if (type == null) {
                    return;
                }
                switch (type){
                    case LONG -> typeName = Items.ITEM_CANVAS_LONG.getName(ItemStack.EMPTY);
                    case TALL -> typeName = Items.ITEM_CANVAS_TALL.getName(ItemStack.EMPTY);
                    case LARGE -> typeName = Items.ITEM_CANVAS_LARGE.getName(ItemStack.EMPTY);
                }
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.2", typeName).withStyle(ChatFormatting.RED));
                return;
            }
            if(!ItemPalette.isFull(offhand)){
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.3").withStyle(ChatFormatting.RED));
                return;
            }
            itemStack = mainhand;
        }

        itemStack.set(Items.CANVAS_VERSION, tag.getInt("v"));
        itemStack.set(Items.CANVAS_ID, canvasId);
        itemStack.set(Items.CANVAS_PIXELS, Arrays.stream(tag.getIntArray("pixels")).boxed().toList());
        itemStack.set(Items.CANVAS_GENERATION, tag.getInt("generation"));
        if (tag.contains("title", 8) && tag.contains("author", 8)) {
            itemStack.set(Items.CANVAS_TITLE, tag.getString("title"));
            itemStack.set(Items.CANVAS_AUTHOR, tag.getString("author"));
        }
        if (doAddItem) {
            player.addItem(itemStack);
        }

        player.sendSystemMessage(Component.translatable("xercapaint.import.success").withStyle(ChatFormatting.GREEN));
    }
}
