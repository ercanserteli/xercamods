package xerca.xercamusic.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.*;
import xerca.xercamusic.common.packets.serverbound.MusicEndedPacket;

import java.util.UUID;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ClientStuff implements ClientModInitializer {

    static public void showMusicGui(){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemMusicSheet){
                UUID id = heldItem.get(Items.SHEET_ID);
                int version = heldItem.getOrDefault(Items.SHEET_VERSION, -1);
                if (id != null && version >= 0) {
                    MusicManagerClient.checkMusicDataAndRun(id, version, () -> Minecraft.getInstance().setScreen(new GuiMusicSheet(player, heldItem, Component.translatable("item.xercamusic.music_sheet"))));
                }
                else{
                    Minecraft.getInstance().setScreen(new GuiMusicSheet(player, heldItem, Component.translatable("item.xercamusic.music_sheet")));
                }
            }
        }
    }

    static public void showInstrumentGui(){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack heldItem = player.getMainHandItem();
            if(!heldItem.isEmpty() && heldItem.getItem() instanceof IItemInstrument){
                Minecraft.getInstance().setScreen(new GuiInstrument(player, (IItemInstrument) heldItem.getItem(), Component.translatable("item.xercamusic.instrument_gui"), null));
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
            sendToServer(pack);
        }
    }

    public static void sendToServer(CustomPacketPayload packet) {
        ClientPlayNetworking.send(packet);
    }

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Entities.MUSIC_SPIRIT, new RenderNothingFactory());

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
