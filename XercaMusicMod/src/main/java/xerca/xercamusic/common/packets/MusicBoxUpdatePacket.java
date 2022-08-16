package xerca.xercamusic.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import xerca.xercamusic.common.XercaMusic;

public class MusicBoxUpdatePacket {
    private BlockPos pos;
    private CompoundTag noteStackNBT;
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
                this.noteStackNBT = new CompoundTag();
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

    public static MusicBoxUpdatePacket decode(FriendlyByteBuf buf) {
        MusicBoxUpdatePacket result = new MusicBoxUpdatePacket();
        try {
            result.pos = buf.readBlockPos();
            result.noteStackNBT = buf.readNbt();
            result.instrumentId = buf.readUtf(255);
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading MusicBoxUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicBoxUpdatePacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeNbt(pkt.noteStackNBT);
        buf.writeUtf(pkt.instrumentId);
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }


    public CompoundTag getNoteStackNBT() {
        return noteStackNBT;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public BlockPos getPos() {
        return pos;
    }

}
