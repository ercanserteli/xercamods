package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BlockVat extends AbstractCauldronBlock {
    public enum VatContent {EMPTY, MILK, CHEESE}
    private static final VoxelShape INSIDE = box(1.0D, 7.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(
            box(0.0D, 0.0D, 4.0D, 16.0D, 6.0D, 12.0D),
            box(4.0D, 0.0D, 0.0D, 12.0D, 6.0D, 16.0D),
            box(1.0D, 0.0D, 1.0D, 15.0D, 6.0D, 15.0D),
            INSIDE), BooleanOp.ONLY_FIRST);


    private final VatContent content;

    public BlockVat(VatContent content) {
        super(Block.Properties.of().mapColor(DyeColor.ORANGE).strength(1.5F).noOcclusion().randomTicks(), Map.of());
        this.content = content;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos) {
        return INSIDE;
    }


    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult blockHitResult) {
        ItemStack itemstack = player.getItemInHand(hand);
        switch (content){
            case EMPTY -> {
                if(itemstack.getItem() == Items.MILK_BUCKET) {
                    if(!player.isCreative()){
                        itemstack.shrink(1);
                        player.addItem(new ItemStack(Items.BUCKET));
                    }

                    level.setBlockAndUpdate(blockPos, Blocks.VAT_MILK.get().defaultBlockState());
                    level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                    return InteractionResult.SUCCESS;
                }
            }
            case MILK -> {
                if(itemstack.getItem() == Items.BUCKET) {
                    if(!player.isCreative()) {
                        itemstack.shrink(1);
                        player.addItem(new ItemStack(Items.MILK_BUCKET));
                    }

                    level.setBlockAndUpdate(blockPos, Blocks.VAT.get().defaultBlockState());
                    level.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
                    return InteractionResult.SUCCESS;
                }
            }
            case CHEESE -> {
                if(!level.isClientSide){
                    Vec3 playerPos = new Vec3(player.getX(), player.getY(), player.getZ());
                    Vec3 boost = playerPos.subtract(new Vec3(blockPos.getX(), blockPos.getY()+1.0, blockPos.getZ()));
                    boost = boost.normalize().scale(0.15);

                    ItemEntity entity = new ItemEntity(level, blockPos.getX(), blockPos.getY()+1.0, blockPos.getZ(),
                            new ItemStack(xerca.xercamod.common.item.Items.CHEESE_WHEEL.get()));
                    entity.setDefaultPickUpDelay();
                    entity.push(boost.x, 0.05, boost.z);
                    entity.hurtMarked = true;
                    level.addFreshEntity(entity);
                    level.setBlockAndUpdate(blockPos, Blocks.VAT.get().defaultBlockState());
                    level.playSound(null, blockPos, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isFull(@NotNull BlockState blockState) {
        return content != VatContent.EMPTY;
    }

    @Override
    public void randomTick(@NotNull BlockState blockState, @NotNull ServerLevel level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        if(content == VatContent.MILK){
//            XercaMod.LOGGER.info("MILKY TICK");
            level.setBlockAndUpdate(blockPos, Blocks.VAT_CHEESE.get().defaultBlockState());
            level.playSound(null, blockPos, SoundEvents.SLIME_BLOCK_STEP, SoundSource.BLOCKS, 0.7F, 0.9F +level.random.nextFloat()*0.2f);
        }
    }
}
