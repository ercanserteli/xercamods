package xerca.xercamod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.animation.TileEntityRendererAnimation;
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
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.tile_entity.TileEntityDoner;
import xerca.xercamod.common.tile_entity.XercaTileEntities;

public class ClientProxy extends Proxy {
    @Mod.EventBusSubscriber(modid = XercaMod.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusSubscriber{
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            ScreenManager.registerFactory(XercaTileEntities.CONTAINER_FUNCTIONAL_BOOKCASE, GuiFunctionalBookcase::new);

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
        }

        @SubscribeEvent
        public static void handleItemColors(ColorHandlerEvent.Item event) {
            if(Items.FLASK != null){
                event.getItemColors().register(
                        (itemStack, colorIndex) -> colorIndex > 0 ? -1 : PotionUtils.getColor(itemStack), Items.FLASK
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
                Minecraft.getInstance().getSoundHandler().play(new HookSound(ent, true));
            }
        }

        @SubscribeEvent
        public static void entityConstEvent(EntityEvent.EntityConstructing ev) {
            Entity ent = ev.getEntity();
            if (ent instanceof EntityHook) {
                EntityHook hook = (EntityHook) ent;
                if (ent.world.isRemote) {
                    Minecraft.getInstance().getSoundHandler().play(new HookSound(hook, false));
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            XercaMod.LOGGER.debug("ClientLoggedOut Event");
            Config.bakeConfig();
        }
    }
}
