package xerca.xercamod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.HookReturningEvent;
import xerca.xercamod.common.Proxy;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.entity.*;
import xerca.xercamod.common.item.ItemGrabHook;
import xerca.xercamod.common.item.ItemScythe;
import xerca.xercamod.common.item.ItemWarhammer;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.tile_entity.XercaTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientProxy extends Proxy {
    private static final ResourceLocation spyglassBlurTexture = new ResourceLocation(XercaMod.MODID, "textures/misc/spyglass_blur.png");
    private static final ResourceLocation blackTexture = new ResourceLocation(XercaMod.MODID, "textures/misc/black.png");
    private static Minecraft mc;

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusSubscriber{
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            ScreenManager.registerFactory(XercaTileEntities.CONTAINER_FUNCTIONAL_BOOKCASE, GuiFunctionalBookcase::new);
            ScreenManager.registerFactory(XercaTileEntities.CONTAINER_CARVING_STATION, CarvingStationScreen::new);

            RenderingRegistry.registerEntityRenderingHandler(Entities.TOMATO, new RenderTomatoFactory());
            RenderingRegistry.registerEntityRenderingHandler(Entities.CONFETTI_BALL, new RenderConfettiBallFactory());
            RenderingRegistry.registerEntityRenderingHandler(Entities.HOOK, new RenderHookFactory());
            RenderingRegistry.registerEntityRenderingHandler(Entities.CUSHION, new RenderCushionFactory());

//            RenderTypeLookup.setRenderLayer(Blocks.BLOCK_DONER, RenderType.getCutoutMipped());
            ClientRegistry.bindTileEntityRenderer(XercaTileEntities.DONER, DonerTileEntityRenderer::new);

            RenderTypeLookup.setRenderLayer(Blocks.BLOCK_TEA_PLANT, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(Blocks.BLOCK_TOMATO_PLANT, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(Blocks.BLOCK_RICE_PLANT, RenderType.getCutout());
            RenderTypeLookup.setRenderLayer(Blocks.CARVED_ACACIA_1, RenderType.getCutoutMipped());
            RenderTypeLookup.setRenderLayer(Blocks.CARVED_ACACIA_2, RenderType.getCutoutMipped());
            RenderTypeLookup.setRenderLayer(Blocks.CARVED_ACACIA_3, RenderType.getCutoutMipped());
            RenderTypeLookup.setRenderLayer(Blocks.CARVED_ACACIA_4, RenderType.getCutoutMipped());
            RenderTypeLookup.setRenderLayer(Blocks.CARVED_ACACIA_5, RenderType.getCutoutMipped());
            RenderTypeLookup.setRenderLayer(Blocks.CARVED_ACACIA_6, RenderType.getCutoutMipped());
            RenderTypeLookup.setRenderLayer(Blocks.CARVED_ACACIA_7, RenderType.getCutoutMipped());
            RenderTypeLookup.setRenderLayer(Blocks.CARVED_ACACIA_8, RenderType.getCutoutMipped());

            registerItemModelsProperties();

            mc = Minecraft.getInstance();
        }

        static private void registerItemModelsProperties(){
            IItemPropertyGetter warhammerPull = (stack, worldIn, entityIn) -> {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    return ((entityIn.getActiveItemStack().getItem() instanceof ItemWarhammer)) ? (stack.getUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
                }
            };
            IItemPropertyGetter grabHookPull = (stack, worldIn, entityIn) -> {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    return ((entityIn.getActiveItemStack().getItem() instanceof ItemGrabHook)) ? (stack.getUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
                }
            };
            IItemPropertyGetter scythePull = (stack, worldIn, entityIn) -> {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    return ((entityIn.getActiveItemStack().getItem() instanceof ItemScythe)) ? (stack.getUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
                }
            };
            IItemPropertyGetter pulling = (stack, worldIn, entityIn) -> {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            };

            ItemModelsProperties.func_239418_a_(Items.STONE_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemModelsProperties.func_239418_a_(Items.STONE_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemModelsProperties.func_239418_a_(Items.WOODEN_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemModelsProperties.func_239418_a_(Items.WOODEN_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemModelsProperties.func_239418_a_(Items.IRON_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemModelsProperties.func_239418_a_(Items.IRON_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemModelsProperties.func_239418_a_(Items.GOLDEN_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemModelsProperties.func_239418_a_(Items.GOLDEN_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemModelsProperties.func_239418_a_(Items.DIAMOND_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemModelsProperties.func_239418_a_(Items.DIAMOND_SCYTHE, new ResourceLocation("pulling"), pulling);

            ItemModelsProperties.func_239418_a_(Items.ITEM_GRAB_HOOK, new ResourceLocation("pull"), grabHookPull);
            ItemModelsProperties.func_239418_a_(Items.ITEM_GRAB_HOOK, new ResourceLocation("pulling"), pulling);
            ItemModelsProperties.func_239418_a_(Items.ITEM_GRAB_HOOK, new ResourceLocation("cast"), (stack, worldIn, entityIn) -> {
                    if(!stack.hasTag()) return 0.0f;
                    CompoundNBT tag = stack.getTag();
                    if(!tag.contains("cast")) return 0.0f;
                    return tag.getBoolean("cast") ? 1.0F : 0.0F;
                }
            );

            ItemModelsProperties.func_239418_a_(Items.ITEM_STONE_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemModelsProperties.func_239418_a_(Items.ITEM_STONE_WARHAMMER, new ResourceLocation("pulling"), pulling);
            ItemModelsProperties.func_239418_a_(Items.ITEM_IRON_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemModelsProperties.func_239418_a_(Items.ITEM_IRON_WARHAMMER, new ResourceLocation("pulling"), pulling);
            ItemModelsProperties.func_239418_a_(Items.ITEM_GOLD_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemModelsProperties.func_239418_a_(Items.ITEM_GOLD_WARHAMMER, new ResourceLocation("pulling"), pulling);
            ItemModelsProperties.func_239418_a_(Items.ITEM_DIAMOND_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemModelsProperties.func_239418_a_(Items.ITEM_DIAMOND_WARHAMMER, new ResourceLocation("pulling"), pulling);

        }

        @SubscribeEvent
        public static void handleItemColors(ColorHandlerEvent.Item event) {
            if(Items.FLASK != null){
                event.getItemColors().register(
                        (itemStack, colorIndex) -> colorIndex > 0 ? -1 : PotionUtils.getColor(itemStack), Items.FLASK
                );
            }
            if(Items.ENDER_BOW != null){
                event.getItemColors().register(
                        (itemStack, colorIndex) -> colorIndex > 0 ? -1 : PotionUtils.getColor(itemStack), Items.ENDER_BOW
                );
            }
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    static class ForgeBusSubscriber {
        @SubscribeEvent
        public static void hookReturningEvent(HookReturningEvent ev) {
            EntityHook ent = (EntityHook) ev.getEntity();
            if (ent.world.isRemote) {
                mc.getSoundHandler().play(new HookSound(ent, true));
            }
        }

        @SubscribeEvent
        public static void entityConstEvent(EntityEvent.EntityConstructing ev) {
            Entity ent = ev.getEntity();
            if (ent instanceof EntityHook) {
                EntityHook hook = (EntityHook) ent;
                if (ent.world.isRemote) {
                    mc.getSoundHandler().play(new HookSound(hook, false));
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            XercaMod.LOGGER.debug("ClientLoggedOut Event");
            Config.bakeConfig();
        }

        @SubscribeEvent
        public static void gameOverlayEvent(RenderGameOverlayEvent.Post event) {
            if(event.getType() == RenderGameOverlayEvent.ElementType.ALL){
                PlayerEntity player = mc.player;
                if(player.getHeldItemMainhand().getItem() == Items.SPYGLASS && player.getItemInUseCount() > 0 && mc.gameSettings.func_243230_g() == PointOfView.FIRST_PERSON){
                    renderSpyglass(event.getWindow());
                }
            }
        }

        @SubscribeEvent
        public static void fovEvent(FOVUpdateEvent updateEvent) {
            PlayerEntity player = updateEvent.getEntity();
            if(player.getHeldItemMainhand().getItem() == Items.SPYGLASS && player.getItemInUseCount() > 0 && mc.gameSettings.func_243230_g() == PointOfView.FIRST_PERSON){
                updateEvent.setNewfov(updateEvent.getNewfov()/8);
            }
        }
    }

    private static void renderSpyglass(MainWindow window){
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();
        int x1, y1, x2, y2;
        int x1l, y1l, x2l, y2l;
        int x1r, y1r, x2r, y2r;

        x1l = 0;
        y1l = 0;
        x2r = width;
        y2r = height;

        if(width > height){
            y1 = 0;
            y2 = height;
            x1 = (width - height)/2;
            x2 = width - x1;
            x2l = x1;
            y2l = height;
            x1r = x1 + height;
            y1r = 0;
        }
        else{
            x1 = 0;
            x2 = width;
            y1 = (height - width)/2;
            y2 = height - y1;
            x2l = width;
            y2l = y1;
            x1r = 0;
            y1r = y1 + width;
        }

//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();

        mc.getTextureManager().bindTexture(spyglassBlurTexture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x1, y2, -90.0D).tex(0.0F, 1.0F).endVertex();
        bufferbuilder.pos(x2, y2, -90.0D).tex(1.0F, 1.0F).endVertex();
        bufferbuilder.pos(x2, y1, -90.0D).tex(1.0F, 0.0F).endVertex();
        bufferbuilder.pos(x1, y1, -90.0D).tex(0.0F, 0.0F).endVertex();
        tessellator.draw();
        bufferbuilder.reset();

        mc.getTextureManager().bindTexture(blackTexture);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x1l, y2l, -90.0D).tex(0.0F, 1.0F).endVertex();
        bufferbuilder.pos(x2l, y2l, -90.0D).tex(1.0F, 1.0F).endVertex();
        bufferbuilder.pos(x2l, y1l, -90.0D).tex(1.0F, 0.0F).endVertex();
        bufferbuilder.pos(x1l, y1l, -90.0D).tex(0.0F, 0.0F).endVertex();
        tessellator.draw();
        bufferbuilder.reset();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x1r, y2r, -90.0D).tex(0.0F, 1.0F).endVertex();
        bufferbuilder.pos(x2r, y2r, -90.0D).tex(1.0F, 1.0F).endVertex();
        bufferbuilder.pos(x2r, y1r, -90.0D).tex(1.0F, 0.0F).endVertex();
        bufferbuilder.pos(x1r, y1r, -90.0D).tex(0.0F, 0.0F).endVertex();
        tessellator.draw();

//        RenderSystem.depthMask(true);
//        RenderSystem.enableDepthTest();
//        RenderSystem.enableAlphaTest();
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
    }
}
