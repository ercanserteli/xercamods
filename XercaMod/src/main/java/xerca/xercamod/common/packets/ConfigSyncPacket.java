package xerca.xercamod.common.packets;

import net.minecraft.network.PacketBuffer;

public class ConfigSyncPacket {
    private boolean messageIsValid;
    public boolean grabHook;
    public boolean warhammer;
    public boolean cushion;
    public boolean tea;
    public boolean food;
    public boolean confetti;
    public boolean enderFlask;
    public boolean courtroom;
    public boolean carvedWood;
    public boolean leatherStraw;
    public boolean bookcase;
    public boolean coins;
    public boolean scythe;
    public boolean spyglass;
    public boolean rope;
    public boolean terracotta_tile;

    public ConfigSyncPacket(boolean grabHook, boolean warhammer, boolean cushion, boolean tea, boolean food,
                            boolean confetti, boolean enderFlask, boolean courtroom, boolean carvedWood,
                            boolean leatherStraw, boolean bookcase, boolean coins, boolean scythe, boolean spyglass,
                            boolean rope, boolean terracotta_tile
    ) {
        this.grabHook = grabHook;
        this.warhammer = warhammer;
        this.cushion = cushion;
        this.tea = tea;
        this.food = food;
        this.confetti = confetti;
        this.enderFlask = enderFlask;
        this.courtroom = courtroom;
        this.carvedWood = carvedWood;
        this.leatherStraw = leatherStraw;
        this.bookcase = bookcase;
        this.coins = coins;
        this.scythe = scythe;
        this.spyglass = spyglass;
        this.rope = rope;
        this.terracotta_tile = terracotta_tile;
    }

    public ConfigSyncPacket() {
        messageIsValid = false;
    }

    public static ConfigSyncPacket decode(PacketBuffer buf) {
        ConfigSyncPacket result = new ConfigSyncPacket();
        try {
            result.grabHook = buf.readBoolean();
            result.warhammer = buf.readBoolean();
            result.cushion = buf.readBoolean();
            result.tea = buf.readBoolean();
            result.food = buf.readBoolean();
            result.confetti = buf.readBoolean();
            result.enderFlask = buf.readBoolean();
            result.courtroom = buf.readBoolean();
            result.carvedWood = buf.readBoolean();
            result.leatherStraw = buf.readBoolean();
            result.bookcase = buf.readBoolean();
            result.coins = buf.readBoolean();
            result.scythe = buf.readBoolean();
            result.spyglass = buf.readBoolean();
            result.rope = buf.readBoolean();
            result.terracotta_tile = buf.readBoolean();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ConfigSyncPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(ConfigSyncPacket pkt, PacketBuffer buf) {
        buf.writeBoolean(pkt.grabHook);
        buf.writeBoolean(pkt.warhammer);
        buf.writeBoolean(pkt.cushion);
        buf.writeBoolean(pkt.tea);
        buf.writeBoolean(pkt.food);
        buf.writeBoolean(pkt.confetti);
        buf.writeBoolean(pkt.enderFlask);
        buf.writeBoolean(pkt.courtroom);
        buf.writeBoolean(pkt.carvedWood);
        buf.writeBoolean(pkt.leatherStraw);
        buf.writeBoolean(pkt.bookcase);
        buf.writeBoolean(pkt.coins);
        buf.writeBoolean(pkt.scythe);
        buf.writeBoolean(pkt.spyglass);
        buf.writeBoolean(pkt.rope);
        buf.writeBoolean(pkt.terracotta_tile);
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
