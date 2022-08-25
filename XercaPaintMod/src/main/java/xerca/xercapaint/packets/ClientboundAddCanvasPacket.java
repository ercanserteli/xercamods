package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.entity.EntityCanvas;

public class ClientboundAddCanvasPacket extends ClientboundAddEntityPacket {
    private String canvasName;
    private int canvasVersion;
    private int directionVal;
    private byte canvasTypeVal;
    private BlockPos pos;
    private byte rotation;

    public ClientboundAddCanvasPacket(EntityCanvas canvas) {
        super(canvas);
        this.canvasName = canvas.getCanvasName();
        this.canvasVersion = canvas.getCanvasVersion();
        this.directionVal = canvas.getDirection().get3DDataValue();
        this.canvasTypeVal = (byte)canvas.getCanvasType().ordinal();
        this.pos = canvas.getPos();
        this.rotation = (byte)canvas.getRotation();
    }

    public ClientboundAddCanvasPacket(FriendlyByteBuf buf) {
        super(buf);
        this.canvasName = buf.readUtf();
        this.canvasVersion = buf.readInt();
        this.directionVal = buf.readInt();
        this.canvasTypeVal = buf.readByte();
        this.pos = buf.readBlockPos();
        this.rotation = buf.readByte();
    }

    @Override
    public void write(@NotNull FriendlyByteBuf friendlyByteBuf) {
        super.write(friendlyByteBuf);
        friendlyByteBuf.writeUtf(canvasName);
        friendlyByteBuf.writeInt(canvasVersion);
        friendlyByteBuf.writeInt(directionVal);
        friendlyByteBuf.writeByte(canvasTypeVal);
        friendlyByteBuf.writeBlockPos(pos); // this has to be written, otherwise pos gets broken
        friendlyByteBuf.writeByte(rotation);
    }

    public String getCanvasName() {
        return canvasName;
    }

    public int getCanvasVersion() {
        return canvasVersion;
    }

    public int getDirectionVal() {
        return directionVal;
    }

    public byte getCanvasTypeVal() {
        return canvasTypeVal;
    }

    public BlockPos getPos() {
        return pos;
    }

    public byte getRotation() {
        return rotation;
    }
}
