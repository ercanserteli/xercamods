package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.packets.IPacket;

import java.util.UUID;

public class MusicSpiritSpawnPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "export_music");
    private boolean messageIsValid;
//    private int entityId;
//    private UUID entityUUID;
//    private int bodyId;
    private EntityMusicSpirit spirit;

    public MusicSpiritSpawnPacket(EntityMusicSpirit spirit) {
        this.spirit = spirit;
//        entityId = spirit.getId();
//        entityUUID = spirit.getUUID();
//        Player body = spirit.getBody();
//        bodyId = body != null ? body.getId() : -1;
//        spirit.blockI
//
//        if(blockInstrument != null && blockInsPos != null){
//            buffer.writeInt(blockInsPos.getX());
//            buffer.writeInt(blockInsPos.getY());
//            buffer.writeInt(blockInsPos.getZ());
//        }
//        else{
//            buffer.writeInt(-1);
//            buffer.writeInt(-1000);
//            buffer.writeInt(-1);
//        }

    }

    public MusicSpiritSpawnPacket() {
        this.messageIsValid = false;
    }

    @Override
    public ResourceLocation getID() {
        return null;
    }

    @Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        spirit.writeSpawnData(buf);
        return buf;
    }
}
