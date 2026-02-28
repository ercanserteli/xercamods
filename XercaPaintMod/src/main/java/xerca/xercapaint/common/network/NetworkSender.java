package xerca.xercapaint.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercapaint.common.XercaPaint;

import java.util.Objects;

public final class NetworkSender {
    @FunctionalInterface
    public interface Sender {
        void sendToPlayer(ServerPlayer player, Object packet);
    }

    private static final Sender DEFAULT_SENDER = (player, packet) ->
            XercaPaint.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), packet);

    private static Sender sender = DEFAULT_SENDER;

    private NetworkSender() {
    }

    public static void setSender(Sender sender) {
        NetworkSender.sender = Objects.requireNonNull(sender);
    }

    public static void resetSender() {
        sender = DEFAULT_SENDER;
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        try {
            sender.sendToPlayer(player, packet);
        } catch (RuntimeException e) {
            String packetName = packet == null ? "null" : packet.getClass().getSimpleName();
            XercaPaint.LOGGER.warn("Failed to send packet {} to player {} ({})", packetName, player.getName().getString(), player.getUUID(), e);
        }
    }
}
