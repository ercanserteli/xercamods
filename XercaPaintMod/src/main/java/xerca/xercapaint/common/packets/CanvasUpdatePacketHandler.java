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
import xerca.xercapaint.common.item.Items;

import java.util.function.Supplier;

import static xerca.xercapaint.common.PaletteUtil.writeCustomColorArrayToNBT;

public class CanvasUpdatePacketHandler {
    private static final double MAX_EASEL_DISTANCE_SQR = 64.0D;

    public static void handle(final CanvasUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
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

    public static void processMessage(CanvasUpdatePacket msg, ServerPlayer pl) {
        ItemStack canvas;
        ItemStack palette;
        Entity entityEasel = null;

        if (msg.getEaselId() > -1) {
            entityEasel = pl.level().getEntity(msg.getEaselId());
            if (entityEasel == null) {
                XercaPaint.LOGGER.error("CanvasUpdatePacketHandler: Easel entity not found! easelId: {}", msg.getEaselId());
                return;
            }
            if (!(entityEasel instanceof EntityEasel easel)) {
                XercaPaint.LOGGER.error("CanvasUpdatePacketHandler: Entity found is not an easel! easelId: {}", msg.getEaselId());
                return;
            }
            if (easel.getPainter() == null || !easel.getPainter().getUUID().equals(pl.getUUID())) {
                XercaPaint.LOGGER.warn("CanvasUpdatePacketHandler: Unauthorized paint update. easelId: {} player: {}", msg.getEaselId(), pl.getName().getString());
                return;
            }
            if (pl.distanceToSqr(easel) > MAX_EASEL_DISTANCE_SQR) {
                XercaPaint.LOGGER.warn("CanvasUpdatePacketHandler: Player too far from easel. easelId: {} player: {}", msg.getEaselId(), pl.getName().getString());
                return;
            }
            canvas = easel.getItem();
            if (!(canvas.getItem() instanceof ItemCanvas)) {
                XercaPaint.LOGGER.error("CanvasUpdatePacketHandler: Canvas not found inside easel!");
                return;
            }
            ItemStack mainHandItem = pl.getMainHandItem();
            ItemStack offHandItem = pl.getOffhandItem();
            if (mainHandItem.getItem() instanceof ItemPalette) {
                palette = mainHandItem;
            } else if (offHandItem.getItem() instanceof ItemPalette) {
                palette = offHandItem;
            } else {
                XercaPaint.LOGGER.error("CanvasUpdatePacketHandler: Palette not found on player's hands!");
                return;
            }
        } else {
            canvas = pl.getMainHandItem();
            palette = pl.getOffhandItem();
            if (canvas.getItem() instanceof ItemPalette) {
                ItemStack temp = canvas;
                canvas = palette;
                palette = temp;
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
            if (msg.getSigned()) {
                comp.putString(ItemCanvas.TAG_AUTHOR, pl.getName().getString());
                comp.putString(ItemCanvas.TAG_TITLE, msg.getTitle().trim());
                comp.putInt(ItemCanvas.TAG_GENERATION, 1);
            }

            if (!palette.isEmpty() && palette.getItem() == Items.ITEM_PALETTE.get()) {
                CompoundTag paletteComp = palette.getOrCreateTag();
                writeCustomColorArrayToNBT(paletteComp, msg.getPaletteColors());
            }

            if (entityEasel instanceof EntityEasel easel) {
                easel.setItem(canvas, false);
                easel.setPainter(null);
            }

            XercaPaint.LOGGER.debug("Handling canvas update: Name: {} V: {}", msg.getName(), msg.getVersion());
        }
    }
}
