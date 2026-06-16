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
    private CommandImport() {
    }

    private static final String TAG_AUTHOR = "author";
    private static final String TAG_TITLE = "title";
    private static final String TAG_GENERATION = "generation";
    private static final String TAG_CANVAS_ID = "name";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("paintimport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(p -> paintImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintImport(CommandSourceStack stack, String name) {
        Mod.LOGGER.debug("Paint import called. name: {}", name);

        ImportPaintingPacket pack = new ImportPaintingPacket(name);
        try {
            ServerPlayer player = stack.getPlayerOrException();
            ServerPlayNetworking.send(player, pack);
        } catch (CommandSyntaxException e) {
            Mod.LOGGER.debug("Command executor is not a player", e);
            return 0;
        }

        return 1;
    }

    public static void doImport(CompoundTag tag, ServerPlayer player) {
        // Sanitizing
        if (!tag.contains("ct", 1)) {
            notifyBrokenPaintFile(player);
            return;
        }
        if ((tag.contains(TAG_AUTHOR, 8) && !tag.contains(TAG_TITLE, 8)) ||
                (!tag.contains(TAG_AUTHOR, 8) && tag.contains(TAG_TITLE, 8))) {
            notifyBrokenPaintFile(player);
            return;
        }
        if (tag.contains(TAG_TITLE, 8) && tag.getString(TAG_TITLE).length() > 16) {
            tag.putString(TAG_TITLE, tag.getString(TAG_TITLE).substring(0, 16));
        }
        if (tag.contains(TAG_AUTHOR, 8) && tag.getString(TAG_AUTHOR).length() > 16) {
            tag.putString(TAG_AUTHOR, tag.getString(TAG_AUTHOR).substring(0, 16));
        }
        String canvasId;
        if (tag.contains(TAG_TITLE)) {
            if (!tag.contains(TAG_CANVAS_ID, 8)) {
                notifyBrokenPaintFile(player);
                return;
            }
            canvasId = tag.getString(TAG_CANVAS_ID);
            if (!canvasId.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}_\\d+$")) {
                notifyBrokenPaintFile(player);
                return;
            }
            if (!tag.contains("v", 3)) {
                tag.putInt("v", 1);
            }
        } else {
            canvasId = ItemCanvas.generateName(player);
            tag.putString(TAG_CANVAS_ID, canvasId);
            tag.putInt("v", 1);
            tag.remove(TAG_GENERATION);
        }

        byte canvasType = tag.getByte("ct");
        CanvasType importedCanvasType = CanvasType.fromByte(canvasType);
        boolean importedGlass = tag.getBoolean("glass");
        tag.remove("ct");
        tag.remove("glass");
        if (tag.getInt(TAG_GENERATION) > 0 && tag.getInt(TAG_GENERATION) < 3) {
            tag.putInt(TAG_GENERATION, tag.getInt(TAG_GENERATION) + 1);
        }

        ItemStack itemStack;
        boolean doAddItem = false;
        if (player.isCreative()) {
            itemStack = new ItemStack(ItemCanvas.canvasItemFor(importedCanvasType, importedGlass));
            doAddItem = true;
        } else {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            if (!(mainHand.getItem() instanceof ItemCanvas heldCanvas) || (mainHand.get(Items.CANVAS_PIXELS) != null || mainHand.get(Items.CANVAS_ID) != null)) {
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.1").withStyle(ChatFormatting.RED));
                return;
            }
            if (heldCanvas.getCanvasType() != importedCanvasType) {
                Component typeName = ItemCanvas.canvasItemFor(importedCanvasType, importedGlass).getName(ItemStack.EMPTY);
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.2", typeName).withStyle(ChatFormatting.RED));
                return;
            }
            if (heldCanvas.isGlass() != importedGlass) {
                Component typeName = ItemCanvas.canvasItemFor(importedCanvasType, importedGlass).getName(ItemStack.EMPTY);
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.material", typeName).withStyle(ChatFormatting.RED));
                return;
            }
            if (!ItemPalette.isFull(offHand)) {
                player.sendSystemMessage(Component.translatable("xercapaint.import.fail.3").withStyle(ChatFormatting.RED));
                return;
            }
            itemStack = mainHand;
        }

        itemStack.set(Items.CANVAS_VERSION, tag.getInt("v"));
        itemStack.set(Items.CANVAS_ID, canvasId);
        itemStack.set(Items.CANVAS_PIXELS, Arrays.stream(tag.getIntArray("pixels")).boxed().toList());
        itemStack.set(Items.CANVAS_GENERATION, tag.getInt(TAG_GENERATION));
        if (tag.contains("sidePixels")) {
            int[] sidePixels = tag.getIntArray("sidePixels");
            if (sidePixels.length == CanvasSides.count(importedCanvasType)) {
                itemStack.set(Items.CANVAS_SIDES_ACTIVE, tag.getBoolean("sidesActive"));
                itemStack.set(Items.CANVAS_SIDE_PIXELS, Arrays.stream(sidePixels).boxed().toList());
            }
        }
        if (tag.contains(TAG_TITLE, 8) && tag.contains(TAG_AUTHOR, 8)) {
            itemStack.set(Items.CANVAS_TITLE, tag.getString(TAG_TITLE));
            itemStack.set(Items.CANVAS_AUTHOR, tag.getString(TAG_AUTHOR));
        }
        ItemCanvas.updateStackSize(itemStack);
        if (doAddItem) {
            player.addItem(itemStack);
        }

        player.sendSystemMessage(Component.translatable("xercapaint.import.success").withStyle(ChatFormatting.GREEN));
    }

    private static void notifyBrokenPaintFile(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("xercapaint.import.fail.5").withStyle(ChatFormatting.RED));
        Mod.LOGGER.warn("Broken paint file");
    }
}
