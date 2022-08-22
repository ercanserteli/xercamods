package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockCarvedAcacia extends BlockCarvedLog {
    public BlockCarvedAcacia() {
        super(Properties.of(Material.WOOD, MaterialColor.WOOD).noOcclusion());
    }

    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState){
        return true;
    }

    @ParametersAreNonnullByDefault
    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() instanceof BlockCarvedAcacia;
    }
}
