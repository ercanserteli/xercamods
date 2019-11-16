package xerca.xercamod.common.block;


import net.minecraft.block.Block;

public class BlockCushion extends Block {
    public int cushionIndex;

    public BlockCushion(Properties properties, int cushionIndex, String registryName) {
        super(properties);
        this.cushionIndex = cushionIndex;
        this.setRegistryName(registryName);
    }
}
