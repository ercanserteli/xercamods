package xerca.xercaconfetti.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import xerca.xercaconfetti.Mod;
import xerca.xercaconfetti.entity.EntityConfettiBall;
import xerca.xercaconfetti.packet.ConfettiParticlePacket;
import xerca.xercaconfetti.packet.ConfettiParticlePacketHandler;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfettiParticlePacket.ID, new ConfettiParticlePacketHandler());
        ClientPlayNetworking.registerGlobalReceiver(EntityConfettiBall.spawnPacketId, (client, handler, buf, responseSender) -> {
            EntityConfettiBall confettiBall = new EntityConfettiBall(Mod.ENTITY_CONFETTI_BALL, client.level);
            ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(buf);
            confettiBall.recreateFromPacket(packet);
            client.execute(() -> {
                if(client.level != null) {
                    client.level.putNonPlayerEntity(confettiBall.getId(), confettiBall);
                }
            });
        });

        // Entity Renderer Registration
        EntityRendererRegistry.register(Mod.ENTITY_CONFETTI_BALL, new RenderConfettiBallFactory());
    }
}
