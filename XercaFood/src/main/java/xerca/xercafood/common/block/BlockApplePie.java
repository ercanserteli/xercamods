package xerca.xercafood.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockApplePie extends CakeBlock {
    public BlockApplePie() {
        super(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CAKE));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, net.minecraft.world.level.Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult ate = super.useWithoutItem(state, worldIn, pos, player, hit);
        if (ate.shouldSwing()) {
            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);
        }
        return ate;
    }
}
