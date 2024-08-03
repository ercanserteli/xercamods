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
import xerca.xercapaint.item.Items;

import static xerca.xercapaint.PaletteUtil.writeCustomColorArrayToNBT;

public class CanvasUpdatePacketHandler implements ServerPlayNetworking.PlayChannelHandler {

    public static void processMessage(CanvasUpdatePacket msg, ServerPlayer pl) {
        ItemStack canvas;
        ItemStack palette;
        Entity entityEasel = null;

        if(msg.getEaselId() > -1){
            entityEasel = pl.level().getEntity(msg.getEaselId());
            if(entityEasel == null){
                Mod.LOGGER.error("CanvasUpdatePacketHandler: Easel entity not found! easelId: {}", msg.getEaselId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel easel)){
                Mod.LOGGER.error("CanvasUpdatePacketHandler: Entity found is not an easel! easelId: {}", msg.getEaselId());
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

            if(entityEasel instanceof EntityEasel easel){
                easel.setItem(canvas, false);
                easel.setPainter(null);
            }

            Mod.LOGGER.debug("Handling canvas update: Name: {} V: {}", msg.getName(), msg.getVersion());
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        CanvasUpdatePacket packet = CanvasUpdatePacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
