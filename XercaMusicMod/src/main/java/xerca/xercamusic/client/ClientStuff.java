package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.MusicEndedPacket;

import java.util.UUID;

public class ClientStuff {

    static public void showMusicGui(){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemMusicSheet){
                CompoundTag noteTag = heldItem.getTag();
                if (noteTag != null && !noteTag.isEmpty() && noteTag.contains("id") && noteTag.contains("ver")) {
                    UUID id = noteTag.getUUID("id");
                    int version = noteTag.getInt("ver");
                    MusicManagerClient.checkMusicDataAndRun(id, version, () -> Minecraft.getInstance().setScreen(new GuiMusicSheet(player, noteTag, Component.translatable("item.xercamusic.music_sheet"))));
                }
                else{
                    Minecraft.getInstance().setScreen(new GuiMusicSheet(player, noteTag, Component.translatable("item.xercamusic.music_sheet")));
                }
            }
        }
    }

    static public void showInstrumentGui(){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof IItemInstrument instrument) {
                Minecraft.getInstance().setScreen(new GuiInstrument(player, instrument, Component.translatable("item.xercamusic.instrument_gui"), null));
            }
        }
    }

    static public void showInstrumentGui(IItemInstrument instrument, BlockPos blockInsPos){
        LocalPlayer player = Minecraft.getInstance().player;
        Minecraft.getInstance().setScreen(new GuiInstrument(player, instrument, Component.translatable("item.xercamusic.instrument_gui"), blockInsPos));
    }

    static public NoteSound playNote(SoundEvent event, double x, double y, double z, float volume, float pitch, byte lengthTicks) {
        return playNote(event, x, y, z, SoundSource.PLAYERS, volume, pitch, lengthTicks);
    }

    static public NoteSound playNote(SoundEvent event, double x, double y, double z, float volume, float pitch) {
        return playNote(event, x, y, z, SoundSource.PLAYERS, volume, pitch, (byte)-1);
    }

    static public void playNoteTE(SoundEvent event, double x, double y, double z, float volume, float pitch, byte lengthTicks) {
        playNote(event, x, y, z, SoundSource.RECORDS, volume, pitch, lengthTicks);
    }

    static public NoteSound playNote(SoundEvent event, double x, double y, double z, SoundSource category, float volume, float pitch, byte lengthTicks) {
        NoteSound sound = new NoteSound(event, category, (float)x, (float)y, (float)z, volume, pitch, lengthTicks);
        Minecraft.getInstance().getSoundManager().play(sound);
        return sound;
    }

    static public void endMusic(int spiritID, int playerID) {
        if (Minecraft.getInstance().player != null && playerID == Minecraft.getInstance().player.getId()) {
            MusicEndedPacket pack = new MusicEndedPacket(spiritID);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, value= Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEventHandler {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.MUSIC_SPIRIT.get(), new RenderNothingFactory());
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    static class ForgeBusSubscriber {
        @SubscribeEvent
        public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
            XercaMusic.LOGGER.debug("onPlayerLoggedIn Event");
            MusicManagerClient.load();
        }
    }
}
