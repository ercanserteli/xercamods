package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Random;

public class BlockVat extends AbstractCauldronBlock {
    public enum VatContent {EMPTY, MILK, CHEESE}

    private final VatContent content;

    public BlockVat(VatContent content) {
        super(Block.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE).strength(1.5F).noOcclusion().randomTicks(), Map.of());
        this.content = content;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        ItemStack itemstack = player.getItemInHand(hand);
        switch (content){
            case EMPTY -> {
                if(itemstack.getItem() == Items.MILK_BUCKET) {
                    if(!player.isCreative()){
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    }

                    level.setBlockAndUpdate(blockPos, Blocks.VAT_MILK.defaultBlockState());
                    level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PLACE, blockPos);
                    return InteractionResult.SUCCESS;
                }
            }
            case MILK -> {
                if(itemstack.getItem() == Items.BUCKET) {
                    if(!player.isCreative()) {
                        player.setItemInHand(hand, new ItemStack(Items.MILK_BUCKET));
                    }

                    level.setBlockAndUpdate(blockPos, Blocks.VAT.defaultBlockState());
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
                            new ItemStack(xerca.xercamod.common.item.Items.CHEESE_WHEEL));
                    entity.setDefaultPickUpDelay();
                    entity.push(boost.x, 0.05, boost.z);
                    entity.hurtMarked = true;
                    level.addFreshEntity(entity);
                    level.setBlockAndUpdate(blockPos, Blocks.VAT.defaultBlockState());
                    level.playSound(null, blockPos, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isFull(BlockState blockState) {
        return content != VatContent.EMPTY;
    }

//    @Override
//    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
//        if(content == VatContent.MILK){
//            XercaMod.LOGGER.info("MILKY TICK");
//        }
//    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos blockPos, Random random) {
        if(content == VatContent.MILK){
//            XercaMod.LOGGER.info("MILKY TICK");
            level.setBlockAndUpdate(blockPos, Blocks.VAT_CHEESE.defaultBlockState());
            level.playSound(null, blockPos, SoundEvents.SLIME_BLOCK_STEP, SoundSource.BLOCKS, 0.7F, 0.9F +level.random.nextFloat()*0.2f);
        }
    }
}
