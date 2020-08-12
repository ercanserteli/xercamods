package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xerca.xercapaint.common.Proxy;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;

public class ClientProxy extends Proxy {

    @Override
    public void init() {
        RenderingRegistry.registerEntityRenderingHandler(Entities.CANVAS, new RenderEntityCanvas.RenderEntityCanvasFactory());
    }

    public void showCanvasGui(PlayerEntity player){
        final ItemStack heldItem = player.getHeldItemMainhand();
        final ItemStack offhandItem = player.getHeldItemOffhand();
        final Minecraft minecraft = Minecraft.getInstance();

        if(heldItem.isEmpty() || (minecraft.player != null && !minecraft.player.getGameProfile().getId().equals(player.getGameProfile().getId()))){
            return;
        }

        if(heldItem.getItem() instanceof ItemCanvas){
            CompoundNBT tag = heldItem.getTag();
            if(offhandItem.isEmpty() || (tag != null && tag.getInt("generation") > 0)){
                minecraft.displayGuiScreen(new GuiCanvasView(heldItem.getTag(), new TranslationTextComponent("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType()));
            }else if(offhandItem.getItem() instanceof ItemPalette){
                minecraft.displayGuiScreen(new GuiCanvasEdit(minecraft.player,
                        tag, offhandItem.getTag(), new TranslationTextComponent("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType()));
            }
        }
        else if(heldItem.getItem() instanceof ItemPalette){
            if(offhandItem.isEmpty()){
                minecraft.displayGuiScreen(new GuiPalette(heldItem.getTag(), new TranslationTextComponent("item.xercapaint.item_palette")));
            }else if(offhandItem.getItem() instanceof ItemCanvas){
                CompoundNBT tag = offhandItem.getTag();
                if(tag != null && tag.getInt("generation") > 0){
                    minecraft.displayGuiScreen(new GuiCanvasView(offhandItem.getTag(), new TranslationTextComponent("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType()));
                }
                else{
                    minecraft.displayGuiScreen(new GuiCanvasEdit(minecraft.player,
                            tag, heldItem.getTag(), new TranslationTextComponent("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType()));
                }
            }
        }
    }


    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusSubscriber{
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
        }
    }
}
