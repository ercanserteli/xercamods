package xerca.xercaconfetti.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import xerca.xercaconfetti.Mod;
import xerca.xercaconfetti.packet.ConfettiParticlePacket;
import xerca.xercaconfetti.packet.ConfettiParticlePacketHandler;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfettiParticlePacket.PACKET_ID, new ConfettiParticlePacketHandler());
        ParticleFactoryRegistry.getInstance().register(Mod.CONFETTI_PARTICLE, ConfettiParticle.Provider::new);

        // Entity Renderer Registration
        EntityRendererRegistry.register(Mod.ENTITY_CONFETTI_BALL, new RenderConfettiBallFactory());
    }
}
