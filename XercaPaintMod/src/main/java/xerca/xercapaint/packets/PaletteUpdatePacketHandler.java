package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;

public class PaletteUpdatePacketHandler implements ServerPlayNetworking.PlayPayloadHandler<PaletteUpdatePacket> {

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

    @Override
    public void receive(PaletteUpdatePacket packet, ServerPlayNetworking.Context context) {
        context.server().execute(()->processMessage(packet, context.player()));
    }
}
