package xerca.xercapaint.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
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
        PlayerEntity player = Minecraft.getInstance().player;
        if(msg.isAllowed()){
            Entity entity = player.level.getEntity(msg.getEaselId());
            if(entity instanceof EntityEasel){
                EntityEasel easel = (EntityEasel)entity;
                ItemStack itemInHand = player.getItemInHand(msg.getHand());
                boolean handHoldsPalette = itemInHand.getItem() instanceof ItemPalette;
                if(msg.isEdit()){
                    if(handHoldsPalette){
                        ClientStuff.showCanvasGui(easel, itemInHand);
                    }
                    else{
                        XercaPaint.LOGGER.error("Could not find palette in hand for editing painting");
                    }
                }else{
                    ClientStuff.showCanvasGui(easel, ItemStack.EMPTY);
                }
            }else{
                XercaPaint.LOGGER.error("Could not find easel");
            }
        }
        else{
            player.sendMessage(new TranslationTextComponent("easel.deny").withStyle(TextFormatting.RED), Util.NIL_UUID);
        }
    }
}
