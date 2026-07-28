package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.IPacket;
import xerca.xercamusic.common.packets.serverbound.MusicEndedPacket;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ClientStuff {

    public static void showMusicGui() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemMusicSheet) {
                player.playSound(SoundEvents.openScroll, 1.0f, 0.8f + player.level().random.nextFloat() * 0.4f);
                var noteTag = heldItem.getTag();
                if (noteTag != null && !noteTag.isEmpty() && noteTag.contains("id") && noteTag.contains("ver")) {
                    UUID id = noteTag.getUUID("id");
                    int version = noteTag.getInt("ver");
                    MusicManagerClient.checkMusicDataAndRun(id, version, () -> Minecraft.getInstance().setScreen(new GuiMusicSheet(player, heldItem, Component.translatable("item.xercamusic.music_sheet"))));
                } else {
                    Minecraft.getInstance().setScreen(new GuiMusicSheet(player, heldItem, Component.translatable("item.xercamusic.music_sheet")));
                }
            }
        }
    }

    public static void showInstrumentGui() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof IItemInstrument itemInstrument) {
                Minecraft.getInstance().setScreen(new GuiInstrument(player, itemInstrument, Component.translatable("item.xercamusic.instrument_gui"), null));
            }
        }
    }

    public static void showInstrumentGui(IItemInstrument instrument, BlockPos blockInsPos) {
        LocalPlayer player = Minecraft.getInstance().player;
        Minecraft.getInstance().setScreen(new GuiInstrument(player, instrument, Component.translatable("item.xercamusic.instrument_gui"), blockInsPos));
    }

    public static NoteSound playNote(SoundEvent event, double x, double y, double z, float volume, float pitch, byte lengthTicks) {
        return playNote(event, x, y, z, SoundSource.PLAYERS, volume, pitch, lengthTicks);
    }

    public static NoteSound playNote(SoundEvent event, double x, double y, double z, float volume, float pitch) {
        return playNote(event, x, y, z, SoundSource.PLAYERS, volume, pitch, (byte) -1);
    }

    public static NoteSound playNoteTE(SoundEvent event, double x, double y, double z, float volume, float pitch, byte lengthTicks) {
        return playNote(event, x, y, z, SoundSource.RECORDS, volume, pitch, lengthTicks);
    }

    public static NoteSound playNote(SoundEvent event, double x, double y, double z, SoundSource category, float volume, float pitch, byte lengthTicks) {
        NoteSound sound = new NoteSound(event, category, (float) x, (float) y, (float) z, volume, pitch, lengthTicks);
        Minecraft.getInstance().getSoundManager().play(sound);
        return sound;
    }

    public static void endMusic(int spiritID, int playerID) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && playerID == player.getId()) {
            MusicEndedPacket pack = new MusicEndedPacket(spiritID);
            sendToServer(pack);
        }
    }

    public static void sendToServer(IPacket packet) {
        XercaMusic.NETWORK_HANDLER.sendToServer(packet);
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEventHandler {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.MUSIC_SPIRIT.get(), new RenderNothingFactory());
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    static class ForgeBusSubscriber {
        @SubscribeEvent
        public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
            XercaMusic.LOGGER.debug("onPlayerLoggedIn Event");
            MusicManagerClient.load();
        }
    }
}
