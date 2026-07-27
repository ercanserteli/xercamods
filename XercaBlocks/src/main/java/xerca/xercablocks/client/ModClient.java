package xerca.xercablocks.client;

import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import xerca.xercablocks.Mod;
import xerca.xercablocks.block.Blocks;
import xerca.xercablocks.menu.Menus;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public final class ModClient {
    private ModClient() {
    }

    @SubscribeEvent
    static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(Menus.CARVING_STATION, StonecutterScreen::new);
        event.register(Menus.BOOKCASE, BookcaseScreen::new);
    }

    @SubscribeEvent
    static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(Mod.id("emissive_overlay"), EmissiveOverlayGeometry.Loader.INSTANCE);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(Blocks.ROPE, RenderType.cutoutMipped());
            Blocks.carvedWoods().values().forEach(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped()));
        });
    }
}
