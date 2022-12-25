package xerca.xercaconfetti.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ParticlePacket{
    protected int count;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected boolean messageIsValid;

    public ParticlePacket(int count, double posX, double posY, double posZ) {
        this.count = count;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public ParticlePacket() {
        this.messageIsValid = false;
    }

    public void read(FriendlyByteBuf buf) {
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
        count = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(getPosX());
        buf.writeDouble(getPosY());
        buf.writeDouble(getPosZ());
        buf.writeInt(getCount());
    }

    public int getCount() {
        return count;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
