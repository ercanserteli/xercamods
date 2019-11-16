package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xerca.xercapaint.common.Proxy;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityCanvas;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

public class ClientProxy extends Proxy {
    private static CanvasRenderer canvasRenderer;

    @Override
    public void init() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCanvas.class, new RenderEntityCanvas.RenderEntityCanvasFactory());
    }

    @Override
    public void updateCanvas(CompoundNBT data) {
        canvasRenderer.updateMapTexture(data);
    }

    public void showCanvasGui(PlayerEntity player){
        final ItemStack heldItem = player.getHeldItemMainhand();
        final ItemStack offhandItem = player.getHeldItemOffhand();

        if(heldItem.isEmpty()){
            return;
        }

        if(heldItem.getItem() instanceof ItemCanvas){
            if(offhandItem.isEmpty()){
                Minecraft.getInstance().displayGuiScreen(new GuiCanvasView(heldItem.getTag(), new TranslationTextComponent("item.xercapaint.item_canvas")));
            }else if(offhandItem.getItem() instanceof ItemPalette){
                Minecraft.getInstance().displayGuiScreen(new GuiCanvasEdit(Minecraft.getInstance().player,
                        heldItem.getTag(), offhandItem.getTag(), new TranslationTextComponent("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType()));
            }
        }
        else if(heldItem.getItem() instanceof ItemPalette){
            if(offhandItem.isEmpty()){
                Minecraft.getInstance().displayGuiScreen(new GuiPalette(heldItem.getTag(), new TranslationTextComponent("item.xercapaint.item_palette")));
            }else if(offhandItem.getItem() instanceof ItemCanvas){
                Minecraft.getInstance().displayGuiScreen(new GuiCanvasEdit(Minecraft.getInstance().player,
                        offhandItem.getTag(), heldItem.getTag(), new TranslationTextComponent("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType()));
            }
        }
    }


    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusSubscriber{
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
        }
    }

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    static class ForgeBusSubscriber {
        @SubscribeEvent
        public static void renderItemInFrameEvent(RenderItemInFrameEvent ev) {
            if(ev.getItem().getItem() == Items.ITEM_CANVAS){
                if(canvasRenderer == null){
                    // Can't put this in setup handler because it needs to be in the main thread
                    canvasRenderer = new CanvasRenderer(Minecraft.getInstance().textureManager);
                }

                ev.setCanceled(true);

                canvasRenderer.renderCanvas(ev.getItem().getTag());
            }
        }
    }
}
