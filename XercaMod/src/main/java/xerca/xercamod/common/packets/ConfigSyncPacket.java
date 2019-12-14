package xerca.xercamod.common.packets;

import net.minecraft.network.PacketBuffer;

public class ConfigSyncPacket {
    private boolean messageIsValid;
    private boolean grabHook;
    private boolean warhammer;

    public ConfigSyncPacket(boolean grabHook, boolean warhammer) {
        this.grabHook = grabHook;
        this.warhammer = warhammer;
    }

    public ConfigSyncPacket() {
        messageIsValid = false;
    }

    public static ConfigSyncPacket decode(PacketBuffer buf) {
        ConfigSyncPacket result = new ConfigSyncPacket();
        try {
            result.grabHook = buf.readBoolean();
            result.warhammer = buf.readBoolean();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ConfigSyncPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(ConfigSyncPacket pkt, PacketBuffer buf) {
        buf.writeBoolean(pkt.isGrabHook());
        buf.writeBoolean(pkt.isWarhammer());
    }

    public boolean isGrabHook() {
        return grabHook;
    }

    public boolean isWarhammer() {
        return warhammer;
    }

    public boolean isMessageIsValid() {
        return messageIsValid;
    }
}
