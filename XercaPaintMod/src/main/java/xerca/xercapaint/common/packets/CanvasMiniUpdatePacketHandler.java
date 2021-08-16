package xerca.xercapaint.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;

import java.util.function.Supplier;

public class CanvasMiniUpdatePacketHandler {
    public static void handle(final CanvasMiniUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
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

    private static void processMessage(CanvasMiniUpdatePacket msg, ServerPlayer pl) {
        ItemStack canvas;
        ItemStack palette;
        Entity entityEasel = null;

        if(msg.getEaselId() > -1){
            entityEasel = pl.level.getEntity(msg.getEaselId());
            if(entityEasel == null){
                XercaPaint.LOGGER.error("CanvasMiniUpdatePacket: Easel entity not found! easelId: " + msg.getEaselId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel easel)){
                XercaPaint.LOGGER.error("CanvasMiniUpdatePacket: Entity found is not an easel! easelId: " + msg.getEaselId());
                return;
            }
            canvas = easel.getItem();
            if(!(canvas.getItem() instanceof ItemCanvas)){
                XercaPaint.LOGGER.error("CanvasMiniUpdatePacket: Canvas not found inside easel!");
                return;
            }
        }
        else{
            canvas = pl.getMainHandItem();
            palette = pl.getOffhandItem();
            if(canvas.getItem() instanceof ItemPalette){
                ItemStack temp = canvas;
                canvas = palette;
                palette = temp;
            }
        }

        if (!canvas.isEmpty() && canvas.getItem() instanceof ItemCanvas) {
            CompoundTag comp = canvas.getOrCreateTag();

            comp.putIntArray("pixels", msg.getPixels());
            comp.putString("name", msg.getName());
            comp.putInt("v", msg.getVersion());
            comp.putInt("generation", 0);

            if(entityEasel instanceof EntityEasel easel){
                easel.setItem(canvas, false);
            }

            XercaPaint.LOGGER.debug("Handling canvas update: Name: " + msg.getName() + " V: " + msg.getVersion());
        }
    }
}
