package xerca.xercatools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import xerca.xercatools.Mod;
import xerca.xercatools.entity.EntityGrabHook;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public final class ModClient {
    private ModClient() {
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Mod.HOOK, RenderGrabHook::new);
        event.registerEntityRenderer(Mod.HEALTH_ORB, RenderHealthOrb::new);
        event.registerEntityRenderer(Mod.ENTITY_CONFETTI_BALL, new RenderConfettiBallFactory());
    }

    @SubscribeEvent
    static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(Mod.CONFETTI_PARTICLE, ConfettiParticle.Provider::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RangeSelectItemModelProperties.ID_MAPPER.put(PullProperty.ID, PullProperty.MAP_CODEC);
            ConditionalItemModelProperties.ID_MAPPER.put(CastProperty.ID, CastProperty.MAP_CODEC);
        });
    }

    @SubscribeEvent
    static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() && event.getEntity() instanceof EntityGrabHook hook) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(new HookSound(hook));
        }
    }
}
