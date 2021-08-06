package xerca.xercapaint.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundNBT;

public abstract class Proxy {
    public abstract void init();
    public abstract void showCanvasGui(Player player);
}
