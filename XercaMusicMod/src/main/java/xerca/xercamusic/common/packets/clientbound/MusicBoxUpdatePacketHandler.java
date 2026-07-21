package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

public final class MusicBoxUpdatePacketHandler {
    private static void processMessage(MusicBoxUpdatePacket msg) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        int x = SectionPos.blockToSectionCoord(msg.pos().getX());
        int z = SectionPos.blockToSectionCoord(msg.pos().getZ());
        ChunkAccess chunk = world.getChunk(x, z, ChunkStatus.FULL, false);
        if (chunk == null) {
            return;
        }

        BlockEntity te = world.getBlockEntity(msg.pos());
        if (te instanceof TileEntityMusicBox tileEntityMusicBox) {

            if (msg.sheetSent()) {
                if (msg.noSheet()) {
                    tileEntityMusicBox.removeSheetStack();
                } else {
                    ItemStack sheetStack = new ItemStack(Items.MUSIC_SHEET);
                    sheetStack.set(Items.SHEET_ID, msg.sheetId());
                    sheetStack.set(Items.SHEET_VERSION, msg.version());
                    sheetStack.set(Items.SHEET_BPS, msg.bps());
                    sheetStack.set(Items.SHEET_LENGTH, msg.length());
                    sheetStack.set(Items.SHEET_VOLUME, msg.volume());
                    tileEntityMusicBox.setSheetStack(sheetStack, false);
                }
            }

            if (!msg.instrumentId().isEmpty()) {
                tileEntityMusicBox.setInstrument(BuiltInRegistries.ITEM.getValue(Identifier.parse(msg.instrumentId())));
            } else {
                tileEntityMusicBox.removeInstrument();
            }
        }
    }

    public static void handle(MusicBoxUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet));
    }
}
