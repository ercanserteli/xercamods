package xerca.xercamod.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.tile_entity.TileEntityFunctionalBookcase;

import javax.annotation.Nullable;
import java.util.Optional;

public class BlockFunctionalBookcase extends Block implements EntityBlock {

    public static final IntegerProperty BOOK_AMOUNT = IntegerProperty.create("books", 0, 6);

    public BlockFunctionalBookcase() {
        super(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(BOOK_AMOUNT, 0));
    }

    // Called when the block is right-clicked
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult rayTraceResult) {
        if (worldIn.isClientSide) return InteractionResult.SUCCESS;

        if (player instanceof ServerPlayer)
        {
            final TileEntityFunctionalBookcase tileEntity = (TileEntityFunctionalBookcase)worldIn.getBlockEntity(pos);
            NetworkHooks.openScreen((ServerPlayer) player, tileEntity, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tent = worldIn.getBlockEntity(pos);
            if(tent != null) {
                IItemHandler inventory = tent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElseThrow(NullPointerException::new);

                for (int i = 0; i < inventory.getSlots(); i++) {
                    if (!inventory.getStackInSlot(i).isEmpty()) {
                        ItemEntity item = new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory.getStackInSlot(i));

                        float multiplier = 0.1f;
                        float motionX = worldIn.random.nextFloat() - 0.5f;
                        float motionY = worldIn.random.nextFloat() - 0.5f;
                        float motionZ = worldIn.random.nextFloat() - 0.5f;

                        item.setDeltaMovement(motionX * multiplier, motionY * multiplier, motionZ * multiplier);

                        worldIn.addFreshEntity(item);
                    }
                }
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BOOK_AMOUNT);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level worldIn, @NotNull BlockPos pos) {
        return calcRedstoneFromTE(worldIn.getBlockEntity(pos));
    }

    public static int calcRedstoneFromTE(BlockEntity te) {
        if(!(te instanceof TileEntityFunctionalBookcase)){
            return 0;
        }
        Optional<IItemHandler> optInv = te.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (optInv.isEmpty()) {
            return 0;
        } else {
            IItemHandler inv = optInv.get();
            int i = 0;

            for(int j = 0; j < inv.getSlots(); ++j) {
                ItemStack itemstack = inv.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    ++i;
                }
            }
            return i;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new TileEntityFunctionalBookcase(blockPos, blockState);
    }
}

