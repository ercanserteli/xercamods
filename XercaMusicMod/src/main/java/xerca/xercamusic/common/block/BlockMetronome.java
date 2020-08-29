package xerca.xercamusic.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.tile_entity.TileEntityMetronome;

import javax.annotation.Nonnull;

public class BlockMetronome extends Block {
    public static final IntegerProperty BPM = IntegerProperty.create("bpm", 0, 10);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public BlockMetronome() {
        super(Properties.create(Material.WOOD).hardnessAndResistance(2.f, 6.f).sound(SoundType.WOOD));
        this.setDefaultState(this.stateContainer.getBaseState().with(BPM, 0).with(POWERED, false).with(FACING, Direction.NORTH));
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


    public void setBpm(BlockState state, World worldIn, BlockPos pos, int bpm) {
        if (!worldIn.isRemote) {
            if (bpm >= 0 && bpm <= 10) {
                state = state.with(BPM, bpm);
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

            if(!note.isEmpty() && note.hasTag()){
                int pause = note.getTag().getInt("pause");
                setBpm(state, worldIn, pos, ItemMusicSheet.pauseToBPMLevel[pause]);
                return ActionResultType.SUCCESS;
            }
            else{
                state = state.func_235896_a_(BPM); //cycle
                worldIn.setBlockState(pos, state, 3); // flags 1 | 2 (cause block update and send to clients)
                return ActionResultType.SUCCESS;
            }
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BPM, POWERED, FACING);
    }

    // Called when the block is placed or loaded client side to get the tile entity for the block
    // Should return a new instance of the tile entity for the block
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
