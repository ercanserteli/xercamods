package xerca.xercapaint.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercapaint.CommandImport;

public final class ImportPaintingSendPacketHandler {
    private ImportPaintingSendPacketHandler() {
    }

    private static void processMessage(ImportPaintingSendPacket msg, ServerPlayer sender) {
        CompoundTag tag = msg.tag();
        CommandImport.doImport(tag, sender);
    }

    public static void handle(ImportPaintingSendPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet, (ServerPlayer) context.player()));
    }
}
