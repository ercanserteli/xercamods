package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class BlockCarvedLog extends HorizontalDirectionalBlock {
    private boolean isNether;

    public BlockCarvedLog() {
        this(false);
    }
    public BlockCarvedLog(boolean isNether) {
        this(isNether ? Block.Properties.of().sound(SoundType.STEM).mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM) :
                Block.Properties.of().sound(SoundType.WOOD).ignitedByLava().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS));
        this.isNether = isNether;
    }
    public BlockCarvedLog(BlockBehaviour.Properties properties) {
        super(properties.strength(2.0F));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return isNether ? 0 : 5;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return !isNether;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return isNether ? 0 : 5;
    }
}
