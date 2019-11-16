package xerca.xercapaint.common.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercapaint.client.GuiCanvasEdit;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.Items;

import java.util.function.Supplier;

public class CanvasUpdatePacketHandler {
    public static void handle(final CanvasUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when CanvasUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(CanvasUpdatePacket msg, ServerPlayerEntity pl) {
        ItemStack canvas = pl.getHeldItemMainhand();
        ItemStack palette = pl.getHeldItemOffhand();
        if(canvas.getItem() == Items.ITEM_PALETTE){
            ItemStack temp = canvas;
            canvas = palette;
            palette = temp;
        }

        if (!canvas.isEmpty() && canvas.getItem() instanceof ItemCanvas) {
            CompoundNBT comp = canvas.getOrCreateTag();

            comp.putIntArray("pixels", msg.getPixels());
            comp.putString("name", msg.getName());
            comp.putInt("v", msg.getVersion());
            if (msg.getSigned()) {
                comp.putString("author", pl.getName().getString());
                comp.putString("title", msg.getTitle().trim());
            }

            if (!palette.isEmpty() && palette.getItem() == Items.ITEM_PALETTE) {
                CompoundNBT paletteComp = palette.getOrCreateTag();
                GuiCanvasEdit.writeCustomColorArrayToNBT(paletteComp, msg.getPaletteColors());
            }

            XercaPaint.LOGGER.debug("Handling canvas update: Name: " + msg.getName() + " V: " + msg.getVersion());
        }
    }
}
