package xerca.xercamod.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class BlockTerracottaTileStairs extends StairBlock {
    protected BlockTerracottaTileStairs(java.util.function.Supplier<BlockState> state, MaterialColor color) {
        super(state, Block.Properties.of(Material.STONE, color).strength(1.5f).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE));
    }
}
