package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
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
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;

import java.util.Objects;

public class ClientStuff {
    public static final ModelLayerLocation EASEL_MAIN_LAYER = new ModelLayerLocation(new ResourceLocation(XercaPaint.MODID, "easel"), "main");
    public static final ModelLayerLocation EASEL_CANVAS_LAYER = new ModelLayerLocation(new ResourceLocation(XercaPaint.MODID, "easel"), "canvas");

    public static void showCanvasGui(EntityEasel easel, ItemStack palette){
        showCanvasGui(easel, palette, Minecraft.getInstance());
    }

    public static void showCanvasGui(EntityEasel easel, ItemStack palette, Minecraft minecraft){
        ItemStack canvas = easel.getItem();
        CompoundTag tag = canvas.getTag();
        if((tag != null && tag.getInt("generation") > 0) || palette.isEmpty()){
            minecraft.setScreen(new GuiCanvasView(canvas.getTag(),
                    new TranslatableComponent("item.xercapaint.item_canvas"),
                    ((ItemCanvas)canvas.getItem()).getCanvasType(), easel));
        }
        else{
            minecraft.setScreen(new GuiCanvasEdit(minecraft.player, canvas.getTag(), palette.getTag(),
                    new TranslatableComponent("item.xercapaint.item_canvas"),
                    ((ItemCanvas)canvas.getItem()).getCanvasType(), easel));
        }
    }

    public static void showCanvasGui(Player player){
        final ItemStack heldItem = player.getMainHandItem();
        final ItemStack offhandItem = player.getOffhandItem();
        final Minecraft minecraft = Minecraft.getInstance();

        if(heldItem.isEmpty() || (minecraft.player != null && !minecraft.player.getGameProfile().getId().equals(player.getGameProfile().getId()))){
            return;
        }

        if(heldItem.getItem() instanceof ItemCanvas){
            CompoundTag tag = heldItem.getTag();
            if(offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemPalette) || (tag != null && tag.getInt("generation") > 0)){
                minecraft.setScreen(new GuiCanvasView(heldItem.getTag(), new TranslatableComponent("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType(), null));
            }
            else{
                minecraft.setScreen(new GuiCanvasEdit(minecraft.player,
                        tag, offhandItem.getTag(), new TranslatableComponent("item.xercapaint.item_canvas"), ((ItemCanvas)heldItem.getItem()).getCanvasType(), null));
            }
        }
        else if(heldItem.getItem() instanceof ItemPalette){
            if(offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemCanvas)){
                minecraft.setScreen(new GuiPalette(heldItem.getTag(), new TranslatableComponent("item.xercapaint.item_palette")));
            }
            else{
                CompoundTag tag = offhandItem.getTag();
                if(tag != null && tag.getInt("generation") > 0){
                    minecraft.setScreen(new GuiCanvasView(offhandItem.getTag(), new TranslatableComponent("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType(), null));
                }
                else{
                    minecraft.setScreen(new GuiCanvasEdit(minecraft.player,
                            tag, heldItem.getTag(), new TranslatableComponent("item.xercapaint.item_canvas"), ((ItemCanvas)offhandItem.getItem()).getCanvasType(), null));
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static public class ModBusSubscriber{
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemPropertyFunction drawn = (itemStack, level, livingEntity, i) -> {
                    if(!itemStack.hasTag()) return 0.0f;
                    else return 1.0F;
                };
                ItemPropertyFunction colors = (stack, worldIn, entityIn, i) ->
                        ((float)ItemPalette.basicColorCount(stack)) / 16.0F;
                ItemProperties.register(Objects.requireNonNull(Items.ITEM_CANVAS), new ResourceLocation(XercaPaint.MODID, "drawn"), drawn);
                ItemProperties.register(Objects.requireNonNull(Items.ITEM_CANVAS_LARGE), new ResourceLocation(XercaPaint.MODID, "drawn"), drawn);
                ItemProperties.register(Objects.requireNonNull(Items.ITEM_CANVAS_LONG), new ResourceLocation(XercaPaint.MODID, "drawn"), drawn);
                ItemProperties.register(Objects.requireNonNull(Items.ITEM_CANVAS_TALL), new ResourceLocation(XercaPaint.MODID, "drawn"), drawn);
                ItemProperties.register(Objects.requireNonNull(Items.ITEM_PALETTE), new ResourceLocation(XercaPaint.MODID, "colors"), colors);
            });
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.CANVAS, new RenderEntityCanvas.RenderEntityCanvasFactory());
            event.registerEntityRenderer(Entities.EASEL, new RenderEntityEasel.RenderEntityEaselFactory());
        }

        @SubscribeEvent
        public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            event.registerLayerDefinition(EASEL_MAIN_LAYER, EaselModel::createBodyLayer);
            event.registerLayerDefinition(EASEL_CANVAS_LAYER, EaselModel::createBodyLayer);
        }
    }
}
