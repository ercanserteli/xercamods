package xerca.xercaomnichest.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import xerca.xercaomnichest.Mod;

public final class Blocks {
    public static final Block OMNI_CHEST = new BlockOmniChest();

    private Blocks() {
    }

    public static void registerBlocks() {
        Registry.register(BuiltInRegistries.BLOCK, Mod.id("omni_chest"), OMNI_CHEST);
    }
}
