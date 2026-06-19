package xerca.xercafood.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xerca.xercafood.common.SoundEvents;
import xerca.xercafood.common.item.ItemTeapot;
import xerca.xercafood.common.item.Items;

import java.util.Collections;
import java.util.List;

public class BlockTeapot extends Block {
    public static final IntegerProperty TEA_AMOUNT = IntegerProperty.create("tea", 0, 7);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape centerShape = box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private static final VoxelShape topShape = box(7.0D, 10.0D, 7.0D, 9.0D, 11.0D, 9.0D);

    private static final VoxelShape shapeNorth = Shapes.or(Shapes.or(Shapes.or(centerShape, topShape),
            box(7.0D, 3.0D, 12.0D, 9.0D, 9.0D, 14.0D)), box(7.0D, 8.0D, 3.0D, 9.0D, 9.0D, 4.0D));
    private static final VoxelShape shapeWest = Shapes.or(Shapes.or(Shapes.or(centerShape, topShape),
            box(12.0D, 3.0D, 7.0D, 14.0D, 9.0D, 9.0D)), box(3.0D, 8.0D, 7.0D, 4.0D, 9.0D, 9.0D));
    private static final VoxelShape shapeSouth = Shapes.or(Shapes.or(Shapes.or(centerShape, topShape),
            box(7.0D, 3.0D, 2.0D, 9.0D, 9.0D, 4.0D)), box(7.0D, 8.0D, 12.0D, 9.0D, 9.0D, 13.0D));
    private static final VoxelShape shapeEast = Shapes.or(Shapes.or(Shapes.or(centerShape, topShape),
            box(2.0D, 3.0D, 7.0D, 4.0D, 9.0D, 9.0D)), box(12.0D, 8.0D, 7.0D, 13.0D, 9.0D, 9.0D));

    public BlockTeapot() {
        super(Properties.of().setId(xerca.xercafood.common.Mod.blockKey("block_teapot")).strength(0.0F, 1.0F).sound(SoundType.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(TEA_AMOUNT, 0).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(TEA_AMOUNT, 0);
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource r) {
        int teaAmount = stateIn.getValue(TEA_AMOUNT);
        if (teaAmount > 0 && r.nextDouble() * 5 < teaAmount * 0.5) {
            Direction facing = stateIn.getValue(FACING);
            double smokeX = pos.getX() + 0.5D;
            double smokeZ = pos.getZ() + 0.5D;
            if (facing == Direction.NORTH) smokeZ = pos.getZ() + 0.25D;
            else if (facing == Direction.SOUTH) smokeZ = pos.getZ() + 0.75D;
            else if (facing == Direction.EAST) smokeX = pos.getX() + 0.75D;
            else if (facing == Direction.WEST) smokeX = pos.getX() + 0.25D;
            for (int i = 0; i < r.nextInt(1) + 1; ++i) {
                worldIn.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        smokeX, pos.getY() + 0.6D + r.nextDouble() * 0.5D, smokeZ,
                        0.0D, 0.025D, 0.0D);
            }
        }
    }

    @Override
    public InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (heldItem.getItem() == Items.TEACUP && state.getValue(TEA_AMOUNT) > 0) {
            if (!worldIn.isClientSide) {
                worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TEA_POUR, SoundSource.PLAYERS, 1.0F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                heldItem.shrink(1);
                player.addItem(new ItemStack(Items.FULL_TEACUP_0));
                worldIn.setBlockAndUpdate(pos, state.setValue(TEA_AMOUNT, state.getValue(TEA_AMOUNT) - 1));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        int teaAmount = state.getValue(TEA_AMOUNT);
        ItemStack teapotStack;
        if (teaAmount == 0) {
            teapotStack = new ItemStack(Items.TEAPOT);
        } else {
            teapotStack = new ItemStack(getItemHotTeapot(teaAmount));
        }
        return Collections.singletonList(teapotStack);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader worldReader, BlockPos blockPos) {
        BlockPos supportPos = blockPos.below();
        return worldReader.getBlockState(supportPos).isFaceSturdy(worldReader, supportPos, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickAccess, BlockPos blockPos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return direction == Direction.DOWN && !state.canSurvive(world, blockPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, world, tickAccess, blockPos, direction, neighborPos, neighborState, random);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> shapeSouth;
            case EAST -> shapeEast;
            case WEST -> shapeWest;
            default -> shapeNorth;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEA_AMOUNT, FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public ItemTeapot getItemHotTeapot(int teaAmount) {
        return switch (teaAmount) {
            case 2 -> Items.HOT_TEAPOT_2;
            case 3 -> Items.HOT_TEAPOT_3;
            case 4 -> Items.HOT_TEAPOT_4;
            case 5 -> Items.HOT_TEAPOT_5;
            case 6 -> Items.HOT_TEAPOT_6;
            case 7 -> Items.HOT_TEAPOT_7;
            default -> Items.HOT_TEAPOT_1;
        };
    }
}
