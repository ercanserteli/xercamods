package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;

import java.util.Arrays;

public class CanvasMiniUpdatePacketHandler implements ServerPlayNetworking.PlayPayloadHandler<CanvasMiniUpdatePacket> {
    public static void processMessage(CanvasMiniUpdatePacket msg, ServerPlayer pl) {
        ItemStack canvas;
        ItemStack palette;
        Entity entityEasel = null;

        if(msg.easelId() > -1){
            entityEasel = pl.level().getEntity(msg.easelId());
            if(entityEasel == null){
                Mod.LOGGER.error("CanvasMiniUpdatePacket: Easel entity not found! easelId: {}", msg.easelId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel easel)){
                Mod.LOGGER.error("CanvasMiniUpdatePacket: Entity found is not an easel! easelId: {}", msg.easelId());
                return;
            }
            canvas = easel.getItem();
            if(!(canvas.getItem() instanceof ItemCanvas)){
                Mod.LOGGER.error("CanvasMiniUpdatePacket: Canvas not found inside easel!");
                return;
            }
        }
        else{
            canvas = pl.getMainHandItem();
            palette = pl.getOffhandItem();
            if(canvas.getItem() instanceof ItemPalette){
                canvas = palette;
            }
        }

        if (!canvas.isEmpty() && canvas.getItem() instanceof ItemCanvas) {
            canvas.set(Items.CANVAS_PIXELS, Arrays.stream(msg.pixels()).boxed().toList());
            canvas.set(Items.CANVAS_ID, msg.canvasId());
            canvas.set(Items.CANVAS_VERSION, msg.version());
            canvas.set(Items.CANVAS_GENERATION, 0);

            if(entityEasel instanceof EntityEasel easel){
                easel.setItem(canvas, false);
            }

            Mod.LOGGER.debug("Handling canvas update: Name: {} V: {}", msg.canvasId(), msg.version());
        }
    }

    @Override
    public void receive(CanvasMiniUpdatePacket packet, ServerPlayNetworking.Context context) {
        if(packet != null){
            context.server().execute(()->processMessage(packet, context.player()));
        }
    }
}
