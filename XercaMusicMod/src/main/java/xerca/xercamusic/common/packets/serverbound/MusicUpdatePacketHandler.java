package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.Triggers;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;
import java.util.UUID;

public class MusicUpdatePacketHandler implements ServerPlayNetworking.PlayPayloadHandler<MusicUpdatePacket> {
    private static void processMessage(MusicUpdatePacket msg, ServerPlayer pl) {
        ItemStack note = pl.getMainHandItem();
        if (!note.isEmpty() && note.getItem() == Items.MUSIC_SHEET) {
            MusicUpdatePacket.FieldFlag flag = msg.availability();
            if(flag.hasId) note.set(Items.SHEET_ID, msg.id());
            if(flag.hasVersion) note.set(Items.SHEET_VERSION, msg.version());
            if(flag.hasLength) note.set(Items.SHEET_LENGTH, (int)msg.lengthBeats());
            if(flag.hasBps) note.set(Items.SHEET_BPS, msg.bps());
            if(flag.hasVolume) note.set(Items.SHEET_VOLUME, msg.volume());
            if(flag.hasPrevIns) note.set(Items.SHEET_PREV_INSTRUMENT, msg.prevInstrument());
            if(flag.hasPrevInsLocked) note.set(Items.SHEET_PREV_INSTRUMENT_LOCKED, msg.prevInsLocked());
            if(flag.hasHlInterval) note.set(Items.SHEET_HIGHLIGHT_INTERVAL, msg.highlightInterval());
            if(flag.hasSigned && msg.signed()) {
                if(flag.hasTitle) note.set(Items.SHEET_TITLE, msg.title().trim());
                note.set(Items.SHEET_AUTHOR, pl.getName().getString());
                note.set(Items.SHEET_GENERATION, 1);
                Triggers.BECOME_MUSICIAN.trigger(pl);
            }
            if(flag.hasNotes){
                ArrayList<NoteEvent> notes = msg.notes();
                UUID id = note.get(Items.SHEET_ID);
                if(notes == null) {
                    // Get if large note was sent in parts
                    notes = MusicManager.getFinishedNotesFromBuffer(id);
                    if(notes == null){
                        return;
                    }
                }
                MusicManager.setMusicData(id, note.getOrDefault(Items.SHEET_VERSION, 0), notes, pl.server);
                if(note.get(Items.SHEET_BPS) == null) {
                    note.set(Items.SHEET_BPS, (byte)8);
                }
            }
        }
    }

    @Override
    public void receive(MusicUpdatePacket packet, ServerPlayNetworking.Context context) {
        if(packet != null){
            context.server().execute(()->processMessage(packet, context.player()));
        }
    }
}
