package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

public class MusicBoxUpdatePacketHandler implements ClientPlayNetworking.PlayPayloadHandler<MusicBoxUpdatePacket> {
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
                tileEntityMusicBox.setInstrument(BuiltInRegistries.ITEM.get(ResourceLocation.parse(msg.instrumentId())));
            } else {
                tileEntityMusicBox.removeInstrument();
            }
        }
    }

    @Override
    public void receive(MusicBoxUpdatePacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> processMessage(packet));
    }
}
