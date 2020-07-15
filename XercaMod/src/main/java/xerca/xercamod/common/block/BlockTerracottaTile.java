package xerca.xercamod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;

public class BlockTerracottaTile extends HorizontalBlock {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    protected BlockTerracottaTile() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(1.5f).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.EAST));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite().rotateY());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
