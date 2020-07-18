package xerca.xercamod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockTerracottaTileStairs extends StairsBlock {
    protected BlockTerracottaTileStairs(java.util.function.Supplier<BlockState> state) {
        super(state, Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5f).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE));
    }
}
