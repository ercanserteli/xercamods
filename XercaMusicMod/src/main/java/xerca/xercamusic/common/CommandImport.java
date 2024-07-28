package xerca.xercamusic.common;

import static xerca.xercamusic.common.item.ItemMusicSheet.convertFromOld;

import java.util.ArrayList;
import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.ImportMusicPacket;
import xerca.xercamusic.common.packets.MusicDataResponsePacket;

public class CommandImport {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("musicimport")
                    .then(Commands.argument("name", StringArgumentType.word())
                            .executes((p) -> musicImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicImport(CommandSource stack, String name){
        XercaMusic.LOGGER.debug("Music import called. name: " + name);

        ImportMusicPacket pack = new ImportMusicPacket(name);
        try {
            ServerPlayerEntity player = stack.asPlayer();
            XercaMusic.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), pack);
        } catch (CommandSyntaxException e) {
            XercaMusic.LOGGER.debug("Command executor is not a player");
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    public static void doImport(CompoundNBT tag, ArrayList<NoteEvent> notes, ServerPlayerEntity player){
        // Sanitizing
        if ((tag.contains("author", 8) && !tag.contains("title", 8)) ||
                (!tag.contains("author", 8) && tag.contains("title", 8))) {
            player.sendMessage(new TranslationTextComponent("xercamusic.import.fail.5").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            XercaMusic.LOGGER.warn("Broken paint file");
            return;
        }
        if (tag.contains("title", 8) && tag.getString("title").length() > 16) {
            tag.putString("title", tag.getString("title").substring(0, 16));
        }
        if (tag.contains("author", 8) && tag.getString("author").length() > 16) {
            tag.putString("author", tag.getString("author").substring(0, 16));
        }
        if (!tag.contains("ver", 3)) {
            tag.putInt("ver", 1);
        }

        if(tag.getInt("generation") > 0){
            tag.putInt("generation", tag.getInt("generation") + 1);
        }
        if(tag.contains("id")) {
            UUID id = tag.getUniqueId("id");
            int ver = tag.getInt("ver");
            if(notes == null) {
                // Get if large note was sent in parts
                notes = MusicManager.getFinishedNotesFromBuffer(id);
                if(notes == null){
                    return;
                }
            }
            MusicManager.setMusicData(id, ver, notes, player.server);

            MusicDataResponsePacket packet = new MusicDataResponsePacket(id, ver, notes);
            XercaMusic.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), packet);
            tag.remove("notes");
        }
        else if(tag.contains("music")){
            // Old version
            XercaMusic.LOGGER.info("Old music file version");
            notes = convertFromOld(tag, player.server);
            UUID id = tag.getUniqueId("id");
            int ver = tag.getInt("ver");

            MusicDataResponsePacket packet = new MusicDataResponsePacket(id, ver, notes);
            XercaMusic.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(()->player), packet);
            tag.remove("notes"); // Just in case
        }
        else {
            XercaMusic.LOGGER.warn("Broken music file");
            return;
        }

        if(player.isCreative()){
            ItemStack itemStack = new ItemStack(Items.MUSIC_SHEET);
            itemStack.setTag(tag);
            player.addItemStackToInventory(itemStack);
        }
        else{
            ItemStack mainhand = player.getHeldItemMainhand();

            if(!(mainhand.getItem() instanceof ItemMusicSheet) || (mainhand.hasTag() && !mainhand.getTag().isEmpty())){
                player.sendMessage(new TranslationTextComponent("xercamusic.import.fail.1").mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
                return;
            }
            mainhand.setTag(tag);
        }
        player.sendMessage(new TranslationTextComponent("xercamusic.import.success").mergeStyle(TextFormatting.GREEN), Util.DUMMY_UUID);
    }
}
