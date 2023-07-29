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
        ItemStack palette;
        Entity entityEasel = null;

        if(msg.getEaselId() > -1){
            entityEasel = pl.level().getEntity(msg.getEaselId());
            if(entityEasel == null){
                Mod.LOGGER.error("CanvasMiniUpdatePacket: Easel entity not found! easelId: " + msg.getEaselId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel easel)){
                Mod.LOGGER.error("CanvasMiniUpdatePacket: Entity found is not an easel! easelId: " + msg.getEaselId());
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
            CompoundTag comp = canvas.getOrCreateTag();

            comp.putIntArray("pixels", msg.getPixels());
            comp.putString("name", msg.getName());
            comp.putInt("v", msg.getVersion());
            comp.putInt("generation", 0);

            if(entityEasel instanceof EntityEasel easel){
                easel.setItem(canvas, false);
            }

            Mod.LOGGER.debug("Handling canvas update: Name: " + msg.getName() + " V: " + msg.getVersion());
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        CanvasMiniUpdatePacket packet = CanvasMiniUpdatePacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
