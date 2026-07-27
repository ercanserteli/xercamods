package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.serverbound.MusicEndedPacket;

import java.util.UUID;

@EventBusSubscriber(modid = Mod.MODID, value = Dist.CLIENT)
public final class ModClient {

    private ModClient() {
    }

    public static void showMusicGui() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemMusicSheet) {
                player.playSound(SoundEvents.OPEN_SCROLL, 1.0f, 0.8f + player.level().random.nextFloat() * 0.4f);
                UUID id = heldItem.get(Items.SHEET_ID);
                int version = heldItem.getOrDefault(Items.SHEET_VERSION, -1);
                if (id != null && version >= 0) {
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
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof IItemInstrument iItemInstrument) {
                Minecraft.getInstance().setScreen(new GuiInstrument(player, iItemInstrument, Component.translatable("item.xercamusic.instrument_gui"), null));
            }
        }
    }

    public static void showInstrumentGui(IItemInstrument instrument, BlockPos blockInsPos) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Minecraft.getInstance().setScreen(new GuiInstrument(player, instrument, Component.translatable("item.xercamusic.instrument_gui"), blockInsPos));
        }
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

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Entities.MUSIC_SPIRIT, new RenderNothingFactory());
    }

    @SubscribeEvent
    static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        Mod.LOGGER.debug("ClientPacketListener Join Event");
        MusicManagerClient.load();
    }
}
