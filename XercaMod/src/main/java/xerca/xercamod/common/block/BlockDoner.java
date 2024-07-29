package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.tile_entity.TileEntityDoner;

import javax.annotation.Nullable;

public class BlockDoner extends Block implements EntityBlock {
    public static final IntegerProperty MEAT_AMOUNT = IntegerProperty.create("meat", 1, 4);
    public static final BooleanProperty IS_RAW = BooleanProperty.create("is_raw");

    public void setRenderType(RenderShape renderType) {
        this.renderType = renderType;
    }

    private RenderShape renderType = RenderShape.ENTITYBLOCK_ANIMATED;

    public BlockDoner() {
        super(Block.Properties.of().pushReaction(PushReaction.DESTROY).sound(SoundType.METAL).strength(1).noOcclusion());
        registerDefaultState(this.stateDefinition.any().setValue(MEAT_AMOUNT, 1).setValue(IS_RAW, true));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MEAT_AMOUNT, IS_RAW);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.getItem() == Items.MUTTON) {
            if(state.getValue(IS_RAW) && state.getValue(MEAT_AMOUNT) < 4){
                if (!worldIn.isClientSide) {
                    worldIn.setBlockAndUpdate(pos, state.setValue(MEAT_AMOUNT, state.getValue(MEAT_AMOUNT) + 1));
                    heldItem.shrink(1);
                }
                worldIn.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.8f, 0.9f + worldIn.random.nextFloat()*0.1f);
                return InteractionResult.SUCCESS;
            }
        }
        else if(heldItem.getItem() == xerca.xercamod.common.item.Items.ITEM_KNIFE.get()){
            if(!state.getValue(IS_RAW)){
                if(!worldIn.isClientSide){
                    if(state.getValue(MEAT_AMOUNT) > 1){
                        worldIn.setBlockAndUpdate(pos, state.setValue(MEAT_AMOUNT, state.getValue(MEAT_AMOUNT)-1));
                    }
                    else{
                        worldIn.setBlockAndUpdate(pos, Blocks.IRON_BARS.defaultBlockState());
                    }
                    Vec3 playerPos = new Vec3(player.getX(), player.getY(), player.getZ());
                    Vec3 boost = playerPos.subtract(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
                    boost = boost.normalize().scale(0.15d);

                    ItemEntity donerEntity = new ItemEntity(worldIn, pos.getX() + 0.5f + boost.x*6, pos.getY() + 0.5f, pos.getZ() + 0.5f + boost.x*6, new ItemStack(xerca.xercamod.common.item.Items.DONER_SLICE.get()));
                    donerEntity.setDefaultPickUpDelay();
                    donerEntity.push(boost.x, 0, boost.z);
                    donerEntity.hurtMarked = true;
                    worldIn.addFreshEntity(donerEntity);

                    heldItem.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(hand));
                }
                worldIn.playSound(player, pos, xerca.xercamod.common.SoundEvents.SNEAK_HIT.get(), SoundSource.BLOCKS, 0.4f, 0.9f + worldIn.random.nextFloat()*0.1f);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && newState.getBlock() != Blocks.IRON_BARS) {
            if(!worldIn.isClientSide) {
                ItemEntity barsEntity = new ItemEntity(worldIn, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.IRON_BARS));
                barsEntity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(barsEntity);
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return renderType;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new TileEntityDoner(blockPos, blockState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return (level1, blockPos, blockState1, t) -> {
            if (t instanceof TileEntityDoner) {
                TileEntityDoner.tick(level1, blockPos, blockState1, (TileEntityDoner) t);
            }
        };
    }
}
