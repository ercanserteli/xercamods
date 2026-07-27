package xerca.xercapaint.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;

import java.util.function.Supplier;

public class CanvasMiniUpdatePacketHandler {
    private static final double MAX_EASEL_DISTANCE_SQR = 64.0D;

    public static void handle(final CanvasMiniUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            XercaPaint.LOGGER.error("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            XercaPaint.LOGGER.error("Sending player was null when CanvasUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    public static void processMessage(CanvasMiniUpdatePacket msg, ServerPlayer pl) {
        ItemStack canvas;
        Entity entityEasel = null;

        if (msg.getEaselId() > -1) {
            entityEasel = pl.level().getEntity(msg.getEaselId());
            if (entityEasel == null) {
                XercaPaint.LOGGER.error("CanvasMiniUpdatePacket: Easel entity not found! easelId: {}", msg.getEaselId());
                return;
            }
            if (!(entityEasel instanceof EntityEasel easel)) {
                XercaPaint.LOGGER.error("CanvasMiniUpdatePacket: Entity found is not an easel! easelId: {}", msg.getEaselId());
                return;
            }
            if (easel.getPainter() == null || !easel.getPainter().getUUID().equals(pl.getUUID())) {
                XercaPaint.LOGGER.warn("CanvasMiniUpdatePacket: Unauthorized paint update. easelId: {} player: {}", msg.getEaselId(), pl.getName().getString());
                return;
            }
            if (pl.distanceToSqr(easel) > MAX_EASEL_DISTANCE_SQR) {
                XercaPaint.LOGGER.warn("CanvasMiniUpdatePacket: Player too far from easel. easelId: {} player: {}", msg.getEaselId(), pl.getName().getString());
                return;
            }
            canvas = easel.getItem();
            if (!(canvas.getItem() instanceof ItemCanvas)) {
                XercaPaint.LOGGER.error("CanvasMiniUpdatePacket: Canvas not found inside easel!");
                return;
            }
        } else {
            canvas = pl.getMainHandItem();
            ItemStack offHandItem = pl.getOffhandItem();
            if (canvas.getItem() instanceof ItemPalette) {
                canvas = offHandItem;
            }
        }

        if (!canvas.isEmpty() && canvas.getItem() instanceof ItemCanvas) {
            CompoundTag comp = canvas.getOrCreateTag();

            comp.putIntArray(ItemCanvas.TAG_PIXELS, msg.getPixels());
            comp.putString(ItemCanvas.TAG_CANVAS_ID, msg.getName());
            comp.putInt(ItemCanvas.TAG_VERSION, msg.getVersion());
            comp.putInt(ItemCanvas.TAG_GENERATION, 0);
            comp.putBoolean(ItemCanvas.TAG_SIDES_ACTIVE, msg.isSidesActive());
            if (msg.getSidePixels().length > 0) {
                comp.putIntArray(ItemCanvas.TAG_SIDE_PIXELS, msg.getSidePixels());
            }

            if (entityEasel instanceof EntityEasel easel) {
                easel.setItem(canvas, false);
            }

            XercaPaint.LOGGER.debug("Handling canvas update: Name: {} V: {}", msg.getName(), msg.getVersion());
        }
    }
}
