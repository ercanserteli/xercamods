package xerca.xercamusic.common.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMetronome;

public class BlockMetronome extends HorizontalBlock {
    public static final IntegerProperty BPS = IntegerProperty.create("bps", 1, 50);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = HORIZONTAL_FACING;

    public BlockMetronome() {
        super(Properties.create(Material.WOOD).hardnessAndResistance(2.f, 6.f).sound(SoundType.WOOD));
        this.setDefaultState(this.stateContainer.getBaseState().with(BPS, 6).with(POWERED, false).with(FACING, Direction.NORTH));
        this.setRegistryName("block_metronome");
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(POWERED, context.getWorld().isBlockPowered(context.getPos()));
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean flag = worldIn.isBlockPowered(pos);
        if (flag != state.get(POWERED)) {
            if (flag) {
                // unpowered to powered
            }

            worldIn.setBlockState(pos, state.with(POWERED, flag), 3);
        }

    }


    public void setBps(BlockState state, World worldIn, BlockPos pos, int bps) {
        if (!worldIn.isRemote) {
            if (bps >= 1 && bps <= 50) {
                state = state.with(BPS, bps);
                worldIn.setBlockState(pos, state, 3); // flags 1 | 2 (cause block update and send to clients)
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.METRONOME_SET, SoundCategory.BLOCKS, 1.0f, 1.0f);
            ItemStack note = ItemStack.EMPTY;
            if(player.getHeldItem(hand).getItem() == Items.MUSIC_SHEET){
                note = player.getHeldItem(hand);
            } else if(player.getHeldItemOffhand().getItem() == Items.MUSIC_SHEET){
                note = player.getHeldItemOffhand();
            }

            if(!note.isEmpty() && note.getTag() != null && note.getTag().contains("bps")){
                int bps = note.getTag().getInt("bps");
                setBps(state, worldIn, pos, bps);
                return ActionResultType.SUCCESS;
            }
            else{
                state = state.func_235896_a_(BPS); //cycle
                worldIn.setBlockState(pos, state, 3); // flags 1 | 2 (cause block update and send to clients)
                return ActionResultType.SUCCESS;
            }
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BPS, POWERED, FACING);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return new TileEntityMetronome();
    }
    
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

}
