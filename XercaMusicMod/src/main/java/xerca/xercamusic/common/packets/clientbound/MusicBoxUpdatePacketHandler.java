package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

public class MusicBoxUpdatePacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(MusicBoxUpdatePacket msg) {
        Level world = Minecraft.getInstance().level;
        if(world == null || !world.hasChunkAt(msg.getPos())){
            return;
        }

        BlockEntity te =  world.getBlockEntity(msg.getPos());
        if(te instanceof TileEntityMusicBox tileEntityMusicBox){

            if(msg.getNoteStackNBT() != null){
                if(msg.getNoteStackNBT().isEmpty()){
                    tileEntityMusicBox.removeNoteStack();
                }
                else{
                    ItemStack noteStack = new ItemStack(Items.MUSIC_SHEET);
                    noteStack.setTag(msg.getNoteStackNBT());
                    tileEntityMusicBox.setNoteStack(noteStack, false);
                }
            }

            if(!msg.getInstrumentId().isEmpty()){
                tileEntityMusicBox.setInstrument(Registry.ITEM.get(new ResourceLocation(msg.getInstrumentId())));
            }else{
                tileEntityMusicBox.removeInstrument();
            }
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MusicBoxUpdatePacket packet = MusicBoxUpdatePacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }
}
