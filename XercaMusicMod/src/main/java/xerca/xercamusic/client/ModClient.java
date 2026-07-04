package xerca.xercamusic.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.*;
import xerca.xercamusic.common.packets.serverbound.MusicEndedPacket;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {

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

    public static void applyNoteEffects(NoteSound sound, NoteEvent event, float basePitch, int durationTicks) {
        if (sound == null) {
            return;
        }

        if (event.hasGlissando()) {
            byte[] waypoints = event.getEffectiveWaypoints();
            if (waypoints != null && waypoints.length > 0) {
                float[] pitchWaypoints = new float[waypoints.length];
                for (int i = 0; i < waypoints.length; i++) {
                    pitchWaypoints[i] = basePitch * (float) Math.pow(2.0, waypoints[i] / 12.0);
                }

                byte[] encodedPositions = event.getEffectivePositions();
                if (encodedPositions != null && encodedPositions.length == waypoints.length) {
                    float[] positions = new float[encodedPositions.length];
                    for (int i = 0; i < encodedPositions.length; i++) {
                        positions[i] = (encodedPositions[i] & 0xFF) / 100.0f;
                    }
                    sound.setGlissando(pitchWaypoints, positions, durationTicks);
                } else {
                    sound.setGlissando(pitchWaypoints, durationTicks);
                }
            }
        }

        if (event.hasVibrato()) {
            sound.setVibrato(
                    event.vibratoDepthSemitones(),
                    event.vibratoRateHz(),
                    event.vibratoDelaySeconds(),
                    event.vibratoFadeSeconds()
            );
        }
    }

    public static void endMusic(int spiritID, int playerID) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && playerID == player.getId()) {
            MusicEndedPacket pack = new MusicEndedPacket(spiritID);
            sendToServer(pack);
        }
    }

    public static void sendToServer(CustomPacketPayload packet) {
        ClientPlayNetworking.send(packet);
    }

    @Override
    public void onInitializeClient() {
        EntityRenderers.register(Entities.MUSIC_SPIRIT, new RenderNothingFactory());

        ClientPlayNetworking.registerGlobalReceiver(ExportMusicPacket.PACKET_ID, new ExportMusicPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(ImportMusicPacket.PACKET_ID, new ImportMusicPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(MusicBoxUpdatePacket.PACKET_ID, new MusicBoxUpdatePacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(MusicDataResponsePacket.PACKET_ID, new MusicDataResponsePacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(SingleNoteClientPacket.PACKET_ID, new SingleNoteClientPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(TripleNoteClientPacket.PACKET_ID, new TripleNoteClientPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(NotesPartAckFromServerPacket.PACKET_ID, new NotesPartAckFromServerPacketHandler());

        ClientPlayConnectionEvents.JOIN.register((ClientPacketListener handler, PacketSender sender, Minecraft client) -> {
            Mod.LOGGER.debug("ClientPacketListener Join Event");
            MusicManagerClient.load();
        });
    }
}
