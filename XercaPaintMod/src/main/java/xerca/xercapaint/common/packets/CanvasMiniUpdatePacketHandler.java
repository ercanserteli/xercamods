package xerca.xercapaint.common.packets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
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
        ServerPlayerEntity sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when CanvasUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(CanvasMiniUpdatePacket msg, ServerPlayerEntity pl) {
        ItemStack canvas;
        ItemStack palette;
        Entity entityEasel = null;

        if(msg.getEaselId() > -1){
            entityEasel = pl.level.getEntity(msg.getEaselId());
            if(entityEasel == null){
                XercaPaint.LOGGER.error("CanvasMiniUpdatePacket: Easel entity not found! easelId: " + msg.getEaselId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel)){
                XercaPaint.LOGGER.error("CanvasMiniUpdatePacket: Entity found is not an easel! easelId: " + msg.getEaselId());
                return;
            }
            EntityEasel easel = (EntityEasel)entityEasel;
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
            CompoundNBT comp = canvas.getOrCreateTag();

            comp.putIntArray("pixels", msg.getPixels());
            comp.putString("name", msg.getName());
            comp.putInt("v", msg.getVersion());
            comp.putInt("generation", 0);

            if(entityEasel instanceof EntityEasel){
                EntityEasel easel = (EntityEasel)entityEasel;
                easel.setItem(canvas, false);
            }

            XercaPaint.LOGGER.debug("Handling canvas update: Name: " + msg.getName() + " V: " + msg.getVersion());
        }
    }
}
