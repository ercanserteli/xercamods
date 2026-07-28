package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

public class MusicBoxUpdatePacket implements IPacket {
    private BlockPos pos;
    private CompoundTag noteStackNBT;
    private String instrumentId;
    private boolean messageIsValid;

    public MusicBoxUpdatePacket(BlockPos pos, ItemStack noteStack, Item itemInstrument) {
        this.pos = pos;

        if (noteStack == null) {
            this.noteStackNBT = null;
        } else {
            if (noteStack.hasTag()) {
                this.noteStackNBT = noteStack.getTag();
            } else {
                this.noteStackNBT = new CompoundTag();
            }
        }
        if (itemInstrument != null) {
            ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(itemInstrument);
            this.instrumentId = resourcelocation.toString();
        } else {
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
            XercaMusic.LOGGER.error("Exception while reading MusicBoxUpdatePacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeNbt(noteStackNBT);
        buf.writeUtf(instrumentId);
        return buf;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }


    public CompoundTag getNoteStackNBT() {
        return noteStackNBT;
    }

    @SuppressWarnings("unused")
    public void setNoteStackNBT(CompoundTag noteStackNBT) {
        this.noteStackNBT = noteStackNBT;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    @SuppressWarnings("unused")
    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public BlockPos getPos() {
        return pos;
    }

    @SuppressWarnings("unused")
    public void setPos(BlockPos pos) {
        this.pos = pos;
    }
}
