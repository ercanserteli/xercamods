package xerca.xercamusic.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.Triggers;
import xerca.xercamusic.common.item.Items;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class MusicUpdatePacketHandler {
    public static void handle(final MusicUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("ServerPlayer was null when MusicUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(MusicUpdatePacket msg, ServerPlayer pl) {
        ItemStack note = pl.getMainHandItem();
        if (!note.isEmpty() && note.getItem() == Items.MUSIC_SHEET.get()) {
            CompoundTag comp = note.getOrCreateTag();

            MusicUpdatePacket.FieldFlag flag = msg.getAvailability();
//            XercaMusic.LOGGER.info(flag);
            if(flag.hasId) comp.putUUID("id", msg.getId());
            if(flag.hasVersion) comp.putInt("ver", msg.getVersion());
            if(flag.hasLength) comp.putInt("l", msg.getLengthBeats());
            if(flag.hasBps) comp.putByte("bps", msg.getBps());
            if(flag.hasVolume) comp.putFloat("vol", msg.getVolume());
            if(flag.hasPrevIns) comp.putByte("prevIns", msg.getPrevInstrument());
            if(flag.hasPrevInsLocked) comp.putBoolean("piLocked", msg.getPrevInsLocked());
            if(flag.hasHlInterval) comp.putByte("hl", msg.getHighlightInterval());
            if(flag.hasSigned && msg.getSigned()) {
                if(flag.hasTitle) comp.putString("title", msg.getTitle().trim());
                comp.putString("author", pl.getName().getString());
                comp.putInt("generation", 1);
                Triggers.BECOME_MUSICIAN.trigger(pl);
            }
            if(!comp.contains("generation")){
                comp.putInt("generation", 0);
            }
            if(flag.hasNotes){
                ArrayList<NoteEvent> notes = msg.getNotes();
                UUID id = comp.getUUID("id");
                if(notes == null) {
                    // Get if large note was sent in parts
                    notes = MusicManager.getFinishedNotesFromBuffer(id);
                    if(notes == null){
                        return;
                    }
                }
                MusicManager.setMusicData(id, comp.getInt("ver"), notes, pl.server);
                if(!comp.contains("bps")) {
                    comp.putByte("bps", (byte)8);
                }
            }
        }
    }
}
