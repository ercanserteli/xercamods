package xerca.xercamusic.common.packets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamusic.common.XercaMusic;

public class MusicBoxUpdatePacket {
    private BlockPos pos;
    private CompoundNBT noteStackNBT;
    private String instrumentId;
    private boolean messageIsValid;

    public MusicBoxUpdatePacket(BlockPos pos, ItemStack noteStack, Item itemInstrument) {
        this.pos = pos;

        if(noteStack == null){
            this.noteStackNBT = null;
        }
        else{
            if(noteStack.hasTag()){
                this.noteStackNBT = noteStack.getTag();
            }else{
                this.noteStackNBT = new CompoundNBT();
            }
        }
        if(itemInstrument != null){
            ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(itemInstrument);
            this.instrumentId =  resourcelocation == null ? "minecraft:air" : resourcelocation.toString();
        }else{
            this.instrumentId = "";
        }
    }

    public MusicBoxUpdatePacket() {
        this.messageIsValid = false;
    }

    public static MusicBoxUpdatePacket decode(PacketBuffer buf) {
        MusicBoxUpdatePacket result = new MusicBoxUpdatePacket();
        try {
            result.pos = buf.readBlockPos();
            result.noteStackNBT = buf.readCompoundTag();
            result.instrumentId = buf.readString(255);
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading MusicBoxUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicBoxUpdatePacket pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeCompoundTag(pkt.noteStackNBT);
        buf.writeString(pkt.instrumentId);
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }


    public CompoundNBT getNoteStackNBT() {
        return noteStackNBT;
    }

    public void setNoteStackNBT(CompoundNBT noteStackNBT) {
        this.noteStackNBT = noteStackNBT;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

}
