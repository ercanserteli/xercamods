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
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.ImportMusicPacket;

public class CommandImport {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicimport")
                        .requires((p) -> p.hasPermission(1))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes((p) -> paintImport(p.getSource(), StringArgumentType.getString(p, "name"))))
        );
    }

    private static int paintImport(CommandSourceStack stack, String name){
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

    public static void doImport(CompoundTag tag, ServerPlayer player){
        if(tag.getInt("generation") > 0){
            tag.putInt("generation", tag.getInt("generation") + 1);
        }

        if(player.isCreative()){
            ItemStack itemStack = new ItemStack(Items.MUSIC_SHEET);
            itemStack.setTag(tag);
            player.addItem(itemStack);
        }
        else{
            ItemStack mainhand = player.getMainHandItem();

            if(!(mainhand.getItem() instanceof ItemMusicSheet) || (mainhand.hasTag() && !mainhand.getTag().isEmpty())){
                player.sendMessage(new TranslatableComponent("import.fail.1").withStyle(ChatFormatting.RED), Util.NIL_UUID);
                return;
            }
            mainhand.setTag(tag);
        }
        player.sendMessage(new TranslatableComponent("import.success").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
    }
}
