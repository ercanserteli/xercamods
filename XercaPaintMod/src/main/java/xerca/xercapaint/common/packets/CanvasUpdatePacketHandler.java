package xerca.xercapaint.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.Items;

import java.util.function.Supplier;

import static xerca.xercapaint.common.PaletteUtil.writeCustomColorArrayToNBT;

public class CanvasUpdatePacketHandler {
    public static void handle(final CanvasUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when CanvasUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(CanvasUpdatePacket msg, ServerPlayer pl) {
        ItemStack canvas = pl.getMainHandItem();
        ItemStack palette = pl.getOffhandItem();
        if(canvas.getItem() == Items.ITEM_PALETTE){
            ItemStack temp = canvas;
            canvas = palette;
            palette = temp;
        }

        if (!canvas.isEmpty() && canvas.getItem() instanceof ItemCanvas) {
            CompoundTag comp = canvas.getOrCreateTag();

            comp.putIntArray("pixels", msg.getPixels());
            comp.putString("name", msg.getName());
            comp.putInt("v", msg.getVersion());
            comp.putInt("generation", 0);
            if (msg.getSigned()) {
                comp.putString("author", pl.getName().getString());
                comp.putString("title", msg.getTitle().trim());
                comp.putInt("generation", 1);
            }

            if (!palette.isEmpty() && palette.getItem() == Items.ITEM_PALETTE) {
                CompoundTag paletteComp = palette.getOrCreateTag();
                writeCustomColorArrayToNBT(paletteComp, msg.getPaletteColors());
            }

            XercaPaint.LOGGER.debug("Handling canvas update: Name: " + msg.getName() + " V: " + msg.getVersion());
        }
    }
}
