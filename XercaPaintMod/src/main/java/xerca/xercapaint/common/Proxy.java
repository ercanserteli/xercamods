package xerca.xercapaint.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public abstract class Proxy {
    public abstract void init();
    public abstract void updateCanvas(CompoundNBT data);
    public abstract void showCanvasGui(PlayerEntity player);
}
