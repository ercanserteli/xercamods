package xerca.xercapaint.common.packets;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercapaint.client.ClientStuff;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemPalette;

import java.util.function.Supplier;

public class OpenGuiPacketHandler {
    public static void handle(final OpenGuiPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
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
                            ClientStuff.showCanvasGui(easel, itemInHand);
                        } else {
                            XercaPaint.LOGGER.error("Could not find palette in hand for editing painting");
                        }
                    } else {
                        ClientStuff.showCanvasGui(easel, ItemStack.EMPTY);
                    }
                } else {
                    XercaPaint.LOGGER.error("Could not find easel");
                }
            } else {
                player.sendMessage(new TranslatableComponent("easel.deny").withStyle(ChatFormatting.RED), Util.NIL_UUID);
            }
        }
    }
}
