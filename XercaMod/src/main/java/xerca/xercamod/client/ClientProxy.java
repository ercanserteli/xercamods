package xerca.xercamod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xerca.xercamod.common.HookReturningEvent;
import xerca.xercamod.common.Proxy;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.entity.EntityConfettiBall;
import xerca.xercamod.common.entity.EntityCushion;
import xerca.xercamod.common.entity.EntityHook;
import xerca.xercamod.common.entity.EntityTomato;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.tile_entity.XercaTileEntities;

public class ClientProxy extends Proxy {
    @Mod.EventBusSubscriber(modid = XercaMod.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusSubscriber{
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            ScreenManager.registerFactory(XercaTileEntities.CONTAINER_FUNCTIONAL_BOOKCASE, GuiFunctionalBookcase::new);

            RenderingRegistry.registerEntityRenderingHandler(EntityTomato.class, new RenderTomatoFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityConfettiBall.class, new RenderConfettiBallFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityHook.class, new RenderHookFactory());
            RenderingRegistry.registerEntityRenderingHandler(EntityCushion.class, new RenderCushionFactory());
        }

        @SubscribeEvent
        public static void handleItemColors(ColorHandlerEvent.Item event) {
            event.getItemColors().register(
                    (itemStack, colorIndex) -> colorIndex > 0 ? -1 : PotionUtils.getColor(itemStack), Items.FLASK
            );
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
    }
}
