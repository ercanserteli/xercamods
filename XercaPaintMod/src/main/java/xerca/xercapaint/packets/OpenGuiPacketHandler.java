package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.Mod;
import xerca.xercapaint.client.ModClient;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemPalette;

public class OpenGuiPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(OpenGuiPacket msg) {
        Player player = Minecraft.getInstance().player;
        if(player != null) {
            if (msg.isAllowed()) {
                Entity entity = player.level.getEntity(msg.getEaselId());
                if (entity instanceof EntityEasel easel) {
                    ItemStack itemInHand = player.getItemInHand(msg.getHand());
                    boolean handHoldsPalette = itemInHand.getItem() instanceof ItemPalette;
                    if (msg.isEdit()) {
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
                player.sendMessage(new TranslatableComponent("easel.deny").withStyle(ChatFormatting.RED), Util.NIL_UUID);
            }
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        OpenGuiPacket packet = OpenGuiPacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }
}
