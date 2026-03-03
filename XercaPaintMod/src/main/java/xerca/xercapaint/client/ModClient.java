package xerca.xercapaint.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.*;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    public static final ModelLayerLocation EASEL_MAIN_LAYER = new ModelLayerLocation(new ResourceLocation(Mod.MOD_ID, "easel"), "main");
    public static final ModelLayerLocation EASEL_CANVAS_LAYER = new ModelLayerLocation(new ResourceLocation(Mod.MOD_ID, "easel"), "canvas");
    private static final String ITEM_CANVAS_TRANSLATION_KEY = "item.xercapaint.item_canvas";
    private static CanvasItemRenderer canvasItemRenderer;

    public static void showCanvasGui(EntityEasel easel, ItemStack palette) {
        showCanvasGui(easel, palette, Minecraft.getInstance());
    }

    public static void showCanvasGui(EntityEasel easel, ItemStack paletteStack, Minecraft minecraft) {
        ItemStack canvasStack = easel.getItem();
        CompoundTag tag = canvasStack.getTag();
        if ((tag != null && tag.getInt("generation") > 0) || paletteStack.isEmpty()) {
            minecraft.setScreen(new GuiCanvasView(canvasStack.getTag(),
                    Component.translatable(ITEM_CANVAS_TRANSLATION_KEY),
                    ((ItemCanvas) canvasStack.getItem()).getCanvasType(), easel));
        } else {
            minecraft.setScreen(new GuiCanvasEdit(minecraft.player, canvasStack.getTag(), paletteStack.getTag(),
                    Component.translatable(ITEM_CANVAS_TRANSLATION_KEY),
                    ((ItemCanvas) canvasStack.getItem()).getCanvasType(), easel));
        }
    }

    public static void showCanvasGui(Player player) {
        final ItemStack heldItem = player.getMainHandItem();
        final ItemStack offhandItem = player.getOffhandItem();
        final Minecraft minecraft = Minecraft.getInstance();

        if (heldItem.isEmpty() || (minecraft.player != null && !minecraft.player.getGameProfile().getId().equals(player.getGameProfile().getId()))) {
            return;
        }

        if (heldItem.getItem() instanceof ItemCanvas itemCanvas) {
            CompoundTag tag = heldItem.getTag();
            if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemPalette) || (tag != null && tag.getInt("generation") > 0)) {
                minecraft.setScreen(new GuiCanvasView(heldItem.getTag(), Component.translatable("item.xercapaint.item_canvas"), itemCanvas.getCanvasType(), null));
            } else {
                minecraft.setScreen(new GuiCanvasEdit(minecraft.player,
                        tag, offhandItem.getTag(), Component.translatable("item.xercapaint.item_canvas"), itemCanvas.getCanvasType(), null));
            }
        } else if (heldItem.getItem() instanceof ItemPalette) {
            if (offhandItem.isEmpty() || !(offhandItem.getItem() instanceof ItemCanvas)) {
                minecraft.setScreen(new GuiPalette(heldItem.getTag(), Component.translatable("item.xercapaint.item_palette")));
            } else {
                CompoundTag tag = offhandItem.getTag();
                if (tag != null && tag.getInt("generation") > 0) {
                    minecraft.setScreen(new GuiCanvasView(offhandItem.getTag(), Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas) offhandItem.getItem()).getCanvasType(), null));
                } else {
                    minecraft.setScreen(new GuiCanvasEdit(minecraft.player,
                            tag, heldItem.getTag(), Component.translatable("item.xercapaint.item_canvas"), ((ItemCanvas) offhandItem.getItem()).getCanvasType(), null));
                }
            }
        }
    }

    static CanvasItemRenderer getCanvasItemRenderer() {
        return canvasItemRenderer;
    }

    @Override
    public void onInitializeClient() {
        canvasItemRenderer = new CanvasItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_LARGE, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_LONG, canvasItemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(Items.ITEM_CANVAS_TALL, canvasItemRenderer);

        EntityRendererRegistry.register(Entities.EASEL, new RenderEntityEasel.RenderEntityEaselFactory());
        EntityRendererRegistry.register(Entities.CANVAS, new RenderEntityCanvas.RenderEntityCanvasFactory());
        EntityModelLayerRegistry.registerModelLayer(EASEL_MAIN_LAYER, EaselModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(EASEL_CANVAS_LAYER, EaselModel::createBodyLayer);

        ClampedItemPropertyFunction drawn = (itemStack, level, livingEntity, i) -> {
            if (!itemStack.hasTag()) return 0.0f;
            else return 1.0F;
        };
        ClampedItemPropertyFunction colors = (stack, worldIn, entityIn, i) ->
                (ItemPalette.basicColorCount(stack)) / 16.0F;
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS, new ResourceLocation(Mod.MOD_ID, "drawn"), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_LARGE, new ResourceLocation(Mod.MOD_ID, "drawn"), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_LONG, new ResourceLocation(Mod.MOD_ID, "drawn"), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_CANVAS_TALL, new ResourceLocation(Mod.MOD_ID, "drawn"), drawn);
        FabricModelPredicateProviderRegistry.register(Items.ITEM_PALETTE, new ResourceLocation(Mod.MOD_ID, "colors"), colors);

        ClientPlayNetworking.registerGlobalReceiver(Mod.CLOSE_GUI_PACKET_ID, new CloseGuiPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.EXPORT_PAINTING_PACKET_ID, new ExportPaintingPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.IMPORT_PAINTING_PACKET_ID, new ImportPaintingPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.OPEN_GUI_PACKET_ID, new OpenGuiPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.PICTURE_SEND_PACKET_ID, new PictureSendPacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(Mod.ADD_CANVAS_PACKET_ID, new ClientboundAddCanvasPacketHandler());
    }
}
