package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.ImportMusicPacket;
import xerca.xercamusic.common.packets.MusicDataResponsePacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.item.ItemMusicSheet.convertFromOld;

public class CommandImport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicimport")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> musicImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int musicImport(CommandSourceStack stack, String name){
        XercaMusic.LOGGER.debug("Music import called. name: " + name);

        ImportMusicPacket pack = new ImportMusicPacket(name);
        try {
            ServerPlayer player = stack.getPlayerOrException();
            XercaMusic.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), pack);
        } catch (CommandSyntaxException e) {
            XercaMusic.LOGGER.debug("Command executor is not a player");
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    public static void doImport(CompoundTag tag, ArrayList<NoteEvent> notes, ServerPlayer player){
        if(tag.getInt("generation") > 0){
            tag.putInt("generation", tag.getInt("generation") + 1);
        }
        if(tag.contains("id") && tag.contains("ver")) {
            UUID id = tag.getUUID("id");
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
            UUID id = tag.getUUID("id");
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
            player.addItem(itemStack);
        }
        else{
            ItemStack mainHandStack = player.getMainHandItem();

            if(!(mainHandStack.getItem() instanceof ItemMusicSheet) || (mainHandStack.hasTag() && mainHandStack.getTag() != null && !mainHandStack.getTag().isEmpty())){
                player.sendMessage(new TranslatableComponent("import.fail.1").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                return;
            }
            mainHandStack.setTag(tag);
        }
        player.sendMessage(new TranslatableComponent("import.success").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
    }
}
