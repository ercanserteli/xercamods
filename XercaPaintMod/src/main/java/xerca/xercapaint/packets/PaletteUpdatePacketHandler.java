package xerca.xercapaint.packets;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;

public final class PaletteUpdatePacketHandler {

    private static void processMessage(PaletteUpdatePacket msg, ServerPlayer pl) {
        ItemStack palette = pl.getMainHandItem();

        if (palette.isEmpty() || palette.getItem() != Items.ITEM_PALETTE) {
            palette = pl.getOffhandItem();
            if (palette.isEmpty() || palette.getItem() != Items.ITEM_PALETTE) {
                return;
            }
        }

        palette.set(Items.PALETTE_CUSTOM_COLORS, new ItemPalette.ComponentCustomColor(msg.paletteColors()));
    }

    public static void handle(PaletteUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet, (ServerPlayer) context.player()));
    }
}
