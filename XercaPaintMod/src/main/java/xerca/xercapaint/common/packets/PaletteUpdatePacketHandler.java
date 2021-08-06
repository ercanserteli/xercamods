package xerca.xercapaint.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercapaint.common.item.Items;

import java.util.function.Supplier;

import static xerca.xercapaint.common.PaletteUtil.writeCustomColorArrayToNBT;

public class PaletteUpdatePacketHandler {
    public static void handle(final PaletteUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when PaletteUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(PaletteUpdatePacket msg, ServerPlayer pl) {
        ItemStack palette = pl.getMainHandItem();

        if (!palette.isEmpty() && palette.getItem() == Items.ITEM_PALETTE) {
            CompoundTag paletteComp = palette.getOrCreateTag();
            writeCustomColorArrayToNBT(paletteComp, msg.getPaletteColors());
        }
    }
}
