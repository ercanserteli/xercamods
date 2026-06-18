package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import xerca.xercapaint.CommandImport;

public class ImportPaintingSendPacketHandler implements ServerPlayNetworking.PlayPayloadHandler<ImportPaintingSendPacket> {

    private static void processMessage(ImportPaintingSendPacket msg, ServerPlayer sender) {
        CompoundTag tag = msg.tag();
        CommandImport.doImport(tag, sender);
    }

    @Override
    public void receive(ImportPaintingSendPacket packet, ServerPlayNetworking.Context context) {
        context.server().execute(() -> processMessage(packet, context.player()));
    }
}
