package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.Mod;
import xerca.xercapaint.client.ModClient;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemPalette;

public class OpenGuiPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<OpenGuiPacket> {
    private static void processMessage(OpenGuiPacket msg) {
        Player player = Minecraft.getInstance().player;
        if(player != null) {
            if (msg.allowed()) {
                Entity entity = player.level().getEntity(msg.easelId());
                if (entity instanceof EntityEasel easel) {
                    ItemStack itemInHand = player.getItemInHand(msg.hand());
                    boolean handHoldsPalette = itemInHand.getItem() instanceof ItemPalette;
                    if (msg.edit()) {
                        if (handHoldsPalette) {
                            ModClient.showCanvasGui(easel, itemInHand);
                        } else {
                            Mod.LOGGER.error("Could not find palette in hand for editing painting");
                        }
                    } else {
                        ModClient.showCanvasGui(easel, ItemStack.EMPTY);
                    }
                } else {
                    Mod.LOGGER.error("Could not find easel");
                }
            } else {
                player.sendSystemMessage(Component.translatable("easel.deny").withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public void receive(OpenGuiPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(()->processMessage(packet));
    }
}
