package xerca.xercamod.common.block;


import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockCushion extends Block {
    public final int cushionIndex;

    public BlockCushion(Properties properties, int cushionIndex) {
        super(properties);
        this.cushionIndex = cushionIndex;
    }
}
