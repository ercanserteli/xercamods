package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;

public class CanvasMiniUpdatePacketHandler implements ServerPlayNetworking.PlayChannelHandler {
    public static void processMessage(CanvasMiniUpdatePacket msg, ServerPlayer pl) {
        ItemStack canvas;
        Entity entityEasel = null;

        if (msg.getEaselId() > -1) {
            entityEasel = pl.level().getEntity(msg.getEaselId());
            if (entityEasel == null) {
                Mod.LOGGER.error("CanvasMiniUpdatePacket: Easel entity not found! easelId: {}", msg.getEaselId());
                return;
            }
            if (!(entityEasel instanceof EntityEasel easel)) {
                Mod.LOGGER.error("CanvasMiniUpdatePacket: Entity found is not an easel! easelId: {}", msg.getEaselId());
                return;
            }
            if (easel.getPainter() == null || !easel.getPainter().getUUID().equals(pl.getUUID())) {
                Mod.LOGGER.warn("CanvasMiniUpdatePacket: Unauthorized paint update. easelId: {} player: {}", msg.getEaselId(), pl.getName().getString());
                return;
            }
            if (pl.distanceToSqr(easel) > 64.0D) {
                Mod.LOGGER.warn("CanvasMiniUpdatePacket: Player too far from easel. easelId: {} player: {}", msg.getEaselId(), pl.getName().getString());
                return;
            }
            canvas = easel.getItem();
            if (!(canvas.getItem() instanceof ItemCanvas)) {
                Mod.LOGGER.error("CanvasMiniUpdatePacket: Canvas not found inside easel!");
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

            Mod.LOGGER.debug("Handling canvas update: Name: {} V: {}", msg.getName(), msg.getVersion());
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        CanvasMiniUpdatePacket packet = CanvasMiniUpdatePacket.decode(buf);
        if (packet != null) {
            server.execute(() -> processMessage(packet, player));
        }
    }
}
