package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
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
        World world = Minecraft.getInstance().world;
        if(!world.isBlockLoaded(msg.getPos())){
            return;
        }

        TileEntity te =  world.getTileEntity(msg.getPos());
        if(te instanceof TileEntityMusicBox){
            TileEntityMusicBox tileEntityMusicBox = (TileEntityMusicBox) te;

            if(msg.getNoteStackNBT() != null){
                if(msg.getNoteStackNBT().isEmpty()){
                    tileEntityMusicBox.removeNoteStack();
                }
                else{
                    ItemStack noteStack = new ItemStack(Items.MUSIC_SHEET);
                    noteStack.setTag(msg.getNoteStackNBT());
                    tileEntityMusicBox.setNoteStack(noteStack);
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
