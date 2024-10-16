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

public class CanvasUpdatePacketHandler implements ServerPlayNetworking.PlayPayloadHandler<CanvasUpdatePacket> {

    public static void processMessage(CanvasUpdatePacket msg, ServerPlayer pl) {
        ItemStack canvas;
        ItemStack palette;
        Entity entityEasel = null;

        if(msg.easelId() > -1){
            entityEasel = pl.level().getEntity(msg.easelId());
            if(entityEasel == null){
                Mod.LOGGER.error("CanvasUpdatePacketHandler: Easel entity not found! easelId: {}", msg.easelId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel easel)){
                Mod.LOGGER.error("CanvasUpdatePacketHandler: Entity found is not an easel! easelId: {}", msg.easelId());
                return;
            }
            canvas = easel.getItem();
            if(!(canvas.getItem() instanceof ItemCanvas)){
                Mod.LOGGER.error("CanvasUpdatePacketHandler: Canvas not found inside easel!");
                return;
            }
            ItemStack mainHandItem = pl.getMainHandItem();
            ItemStack offHandItem = pl.getOffhandItem();
            if(mainHandItem.getItem() instanceof ItemPalette){
                palette = mainHandItem;
            }else if(offHandItem.getItem() instanceof ItemPalette){
                palette = offHandItem;
            }else{
                Mod.LOGGER.error("CanvasUpdatePacketHandler: Palette not found on player's hands!");
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
            canvas.set(Items.CANVAS_PIXELS, Arrays.stream(msg.pixels()).boxed().toList());
            canvas.set(Items.CANVAS_ID, msg.canvasId());
            canvas.set(Items.CANVAS_VERSION, msg.version());
            canvas.set(Items.CANVAS_GENERATION, 0);
            if (msg.signed()) {
                canvas.set(Items.CANVAS_AUTHOR, pl.getName().getString());
                canvas.set(Items.CANVAS_TITLE, msg.title().trim());
                canvas.set(Items.CANVAS_GENERATION, 1);
            }

            if (!palette.isEmpty() && palette.getItem() == Items.ITEM_PALETTE) {
                palette.set(Items.PALETTE_CUSTOM_COLORS, new ItemPalette.ComponentCustomColor(msg.paletteColors()));
            }

            if(entityEasel instanceof EntityEasel easel){
                easel.setItem(canvas, false);
                easel.setPainter(null);
            }

            Mod.LOGGER.debug("Handling canvas update: Name: {} V: {}", msg.canvasId(), msg.version());
        }
    }

    @Override
    public void receive(CanvasUpdatePacket packet, ServerPlayNetworking.Context context) {
        context.server().execute(()->processMessage(packet, context.player()));
    }
}
