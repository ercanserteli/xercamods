package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xerca.xercapaint.common.Proxy;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

public class ClientProxy extends Proxy {

    @Override
    public void init() {
    }

    public void showCanvasGui(Player player){
        final ItemStack heldItem = player.getMainHandItem();
        final ItemStack offhandItem = player.getOffhandItem();
        final Minecraft minecraft = Minecraft.getInstance();

        if(heldItem.isEmpty() || (minecraft.player != null && !minecraft.player.getGameProfile().getId().equals(player.getGameProfile().getId()))){
            return;
        }

        if(heldItem.getItem() instanceof ItemCanvas){
            CompoundTag tag = heldItem.getTag();
            if(offhandItem.isEmpty() || (tag != null && tag.getInt("generation") > 0)){
                minecraft.setScreen(new GuiCanvasView(heldItem.getTag(), new TranslatableComponent("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType()));
            }else if(offhandItem.getItem() instanceof ItemPalette){
                minecraft.setScreen(new GuiCanvasEdit(minecraft.player,
                        tag, offhandItem.getTag(), new TranslatableComponent("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType()));
            }
        }
        else if(heldItem.getItem() instanceof ItemPalette){
            if(offhandItem.isEmpty()){
                minecraft.setScreen(new GuiPalette(heldItem.getTag(), new TranslatableComponent("item.xercapaint.item_palette")));
            }else if(offhandItem.getItem() instanceof ItemCanvas){
                CompoundTag tag = offhandItem.getTag();
                if(tag != null && tag.getInt("generation") > 0){
                    minecraft.setScreen(new GuiCanvasView(offhandItem.getTag(), new TranslatableComponent("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType()));
                }
                else{
                    minecraft.setScreen(new GuiCanvasEdit(minecraft.player,
                            tag, heldItem.getTag(), new TranslatableComponent("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType()));
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static public class ModBusSubscriber{
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            ItemPropertyFunction drawn = (itemStack, level, livingEntity, i) -> {
                if(!itemStack.hasTag()) return 0.0f;
                else return 1.0F;
            };
            ItemPropertyFunction colors = (stack, worldIn, entityIn, i) ->
                    ((float)ItemPalette.basicColorCount(stack)) / 16.0F;

            ItemProperties.register(Items.ITEM_CANVAS, new ResourceLocation(XercaPaint.MODID, "drawn"), drawn);
            ItemProperties.register(Items.ITEM_CANVAS_LARGE, new ResourceLocation(XercaPaint.MODID, "drawn"), drawn);
            ItemProperties.register(Items.ITEM_CANVAS_LONG, new ResourceLocation(XercaPaint.MODID, "drawn"), drawn);
            ItemProperties.register(Items.ITEM_CANVAS_TALL, new ResourceLocation(XercaPaint.MODID, "drawn"), drawn);
            ItemProperties.register(Items.ITEM_PALETTE, new ResourceLocation(XercaPaint.MODID, "colors"), colors);
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.CANVAS, new RenderEntityCanvas.RenderEntityCanvasFactory());
        }
    }
}
