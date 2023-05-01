package xerca.xercamusic.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.ImportMusicPacket;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.sendToClient;
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
            sendToClient(player, pack);
        } catch (CommandSyntaxException e) {
            XercaMusic.LOGGER.debug("Command executor is not a player");
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    public static void doImport(CompoundTag tag, ServerPlayer player){
        if(tag.getInt("generation") > 0){
            tag.putInt("generation", tag.getInt("generation") + 1);
        }
        if(tag.contains("notes") && tag.contains("id") && tag.contains("ver")) {
            ArrayList<NoteEvent> notes = new ArrayList<>();
            NoteEvent.fillArrayFromNBT(notes, tag);
            UUID id = tag.getUUID("id");
            int ver = tag.getInt("ver");
            MusicManager.setMusicData(id, ver, notes, player.server);

            MusicDataResponsePacket packet = new MusicDataResponsePacket(id, ver, notes);
            sendToClient(player, packet);
            tag.remove("notes");
        }
        else if(tag.contains("music")){
            // Old version
            XercaMusic.LOGGER.info("Old music file version");
            ArrayList<NoteEvent> notes = convertFromOld(tag, player.server);
            UUID id = tag.getUUID("id");
            int ver = tag.getInt("ver");

            MusicDataResponsePacket packet = new MusicDataResponsePacket(id, ver, notes);
            sendToClient(player, packet);
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
            ItemStack mainHandItem = player.getMainHandItem();

            //noinspection ConstantConditions
            if(!(mainHandItem.getItem() instanceof ItemMusicSheet) || (mainHandItem.hasTag() && !mainHandItem.getTag().isEmpty())){
                player.sendSystemMessage(Component.translatable("import.fail.1").withStyle(ChatFormatting.RED));
                return;
            }
            mainHandItem.setTag(tag);
        }
        player.sendSystemMessage(Component.translatable("import.success").withStyle(ChatFormatting.GREEN));
    }
}
