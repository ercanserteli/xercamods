package xerca.xercafood.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockSweetBerryPie extends CakeBlock {
    public BlockSweetBerryPie() {
        super(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CAKE).setId(xerca.xercafood.common.Mod.blockKey("block_sweet_berry_pie")));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult ate = super.useWithoutItem(state, worldIn, pos, player, hit);
        if (ate.consumesAction()) {
            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);
        }
        return ate;
    }
}
