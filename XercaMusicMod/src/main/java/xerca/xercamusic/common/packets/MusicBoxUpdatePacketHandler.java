package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.util.function.Supplier;

public class MusicBoxUpdatePacketHandler {
    public static void handle(final MusicBoxUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
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
                    ItemStack noteStack = new ItemStack(Items.MUSIC_SHEET.get());
                    noteStack.setTag(msg.getNoteStackNBT());
                    tileEntityMusicBox.setNoteStack(noteStack, false);
                }
            }

            if(!msg.getInstrumentId().isEmpty()){
                tileEntityMusicBox.setInstrument(ForgeRegistries.ITEMS.getValue(new ResourceLocation(msg.getInstrumentId())));
            }else{
                tileEntityMusicBox.removeInstrument();
            }
        }
    }
}
