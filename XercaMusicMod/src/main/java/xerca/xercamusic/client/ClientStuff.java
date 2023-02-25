package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.MusicEndedPacket;

import java.util.UUID;

public class ClientStuff {

    static public void showMusicGui(){
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getHeldItemMainhand();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemMusicSheet) {
                CompoundNBT noteTag = heldItem.getTag();
                if (noteTag != null && !noteTag.isEmpty() && noteTag.contains("id") && noteTag.contains("ver")) {
                    UUID id = noteTag.getUniqueId("id");
                    int version = noteTag.getInt("ver");
                    MusicManagerClient.checkMusicDataAndRun(id, version, () -> Minecraft.getInstance().displayGuiScreen(new GuiMusicSheet(player, noteTag, new TranslationTextComponent("item.xercamusic.music_sheet"))));
                } else {
                    Minecraft.getInstance().displayGuiScreen(new GuiMusicSheet(player, noteTag, new TranslationTextComponent("item.xercamusic.music_sheet")));
                }
            }
        }
    }

    static public void showInstrumentGui(){
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getHeldItemMainhand();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemInstrument) {
                Minecraft.getInstance().displayGuiScreen(new GuiInstrument(player, (ItemInstrument) heldItem.getItem(), new TranslationTextComponent("item.xercamusic.instrument_gui"), null));
            }
        }
    }

    static public void showInstrumentGui(ItemInstrument instrument, BlockPos blockInsPos){
    	Minecraft mc = Minecraft.getInstance();
        mc.displayGuiScreen(new GuiInstrument(mc.player, instrument, new TranslationTextComponent("item.xercamusic.instrument_gui"), blockInsPos));
    }

    static public NoteSound playNote(SoundEvent event, double x, double y, double z, float volume, float pitch, byte lengthTicks) {
        return playNote(event, x, y, z, SoundCategory.PLAYERS, volume, pitch, lengthTicks);
    }

    static public NoteSound playNote(SoundEvent event, double x, double y, double z, float volume, float pitch) {
        return playNote(event, x, y, z, SoundCategory.PLAYERS, volume, pitch, (byte)-1);
    }

    static public NoteSound playNoteTE(SoundEvent event, double x, double y, double z, float volume, float pitch, byte lengthTicks) {
        return playNote(event, x, y, z, SoundCategory.RECORDS, volume, pitch, lengthTicks);
    }

    static public NoteSound playNote(SoundEvent event, double x, double y, double z, SoundCategory category, float volume, float pitch, byte lengthTicks) {
        NoteSound sound = new NoteSound(event, category, (float)x, (float)y, (float)z, volume, pitch, lengthTicks);
        Minecraft.getInstance().getSoundHandler().play(sound);
        return sound;
    }

    static public void endMusic(int spiritID, int playerID) {
    	Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && playerID == mc.player.getEntityId()) {
            MusicEndedPacket pack = new MusicEndedPacket(spiritID);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    static class ForgeBusSubscriber {
        @SubscribeEvent
        public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
            XercaMusic.LOGGER.debug("onPlayerLoggedIn Event");
            MusicManagerClient.load();
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusSubscriber {
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(Entities.MUSIC_SPIRIT, new RenderNothingFactory());
        }
    }
}
