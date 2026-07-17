package xerca.xercatools.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import xerca.xercatools.Mod;
import xerca.xercatools.entity.EntityGrabHook;
import xerca.xercatools.packet.ConfettiParticlePacket;
import xerca.xercatools.packet.ConfettiParticlePacketHandler;

@Environment(EnvType.CLIENT)
public final class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(Mod.HOOK, RenderGrabHook::new);
        EntityRenderers.register(Mod.HEALTH_ORB, RenderHealthOrb::new);
        EntityRenderers.register(Mod.ENTITY_CONFETTI_BALL, new RenderConfettiBallFactory());
        ClientPlayNetworking.registerGlobalReceiver(ConfettiParticlePacket.PACKET_ID, new ConfettiParticlePacketHandler());
        ParticleFactoryRegistry.getInstance().register(Mod.CONFETTI_PARTICLE, ConfettiParticle.Provider::new);
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof EntityGrabHook hook) {
                Minecraft.getInstance().getSoundManager().queueTickingSound(new HookSound(hook));
            }
        });

        // Custom item model properties (Client Items system replaces ItemProperties / model overrides).
        RangeSelectItemModelProperties.ID_MAPPER.put(PullProperty.ID, PullProperty.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(CastProperty.ID, CastProperty.MAP_CODEC);
    }
}
