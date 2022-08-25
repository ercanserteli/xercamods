package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.Triggers;
import xerca.xercamusic.common.item.Items;

public class MusicUpdatePacketHandler implements ServerPlayNetworking.PlayChannelHandler {
    private static void processMessage(MusicUpdatePacket msg, ServerPlayer pl) {
        ItemStack note = pl.getMainHandItem();
        if (!note.isEmpty() && note.getItem() == Items.MUSIC_SHEET) {
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
                MusicManager.setMusicData(comp.getUUID("id"), comp.getInt("ver"), msg.getNotes(), pl.server);
                if(!comp.contains("bps")) {
                    comp.putByte("bps", (byte)8);
                }
            }
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MusicUpdatePacket packet = MusicUpdatePacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
