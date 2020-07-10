package xerca.xercamod.common.block;

import com.sun.javafx.geom.Vec3f;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.tile_entity.TileEntityDoner;

import javax.annotation.Nonnull;

public class BlockDoner extends Block {
    public static final IntegerProperty MEAT_AMOUNT = IntegerProperty.create("meat", 1, 4);
    public static final BooleanProperty IS_RAW = BooleanProperty.create("is_raw");

    public void setRenderType(BlockRenderType renderType) {
        this.renderType = renderType;
    }

    private BlockRenderType renderType = BlockRenderType.ENTITYBLOCK_ANIMATED;

    public BlockDoner() {
        super(Block.Properties.create(Material.CAKE).sound(SoundType.SLIME).hardnessAndResistance(0.0F, 1.0F).notSolid());
        setRegistryName("block_doner");
        setDefaultState(this.stateContainer.getBaseState().with(MEAT_AMOUNT, 1).with(IS_RAW, true));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    }

    @Override
    public boolean hasTileEntity(final BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return new TileEntityDoner();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(MEAT_AMOUNT, IS_RAW);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.getItem() == Items.MUTTON) {
            if(state.get(IS_RAW) && state.get(MEAT_AMOUNT) < 4){
                worldIn.setBlockState(pos, state.with(MEAT_AMOUNT, state.get(MEAT_AMOUNT)+1));
                return ActionResultType.SUCCESS;
            }
        }
        else if(heldItem.getItem() == xerca.xercamod.common.item.Items.ITEM_KNIFE){
            if(!state.get(IS_RAW)){
                if(state.get(MEAT_AMOUNT) > 1){
                    worldIn.setBlockState(pos, state.with(MEAT_AMOUNT, state.get(MEAT_AMOUNT)-1));
                }
                else{
                    worldIn.setBlockState(pos, Blocks.IRON_BARS.getDefaultState());
                }
                if(!worldIn.isRemote){
                    Vec3d playerPos = new Vec3d(player.getPosX(), player.getPosY(), player.getPosZ());
                    Vec3d boost = playerPos.subtract(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
                    boost = boost.normalize().scale(0.15d);

                    ItemEntity itementity = new ItemEntity(worldIn, pos.getX() + 0.5f + boost.x*6, pos.getY() + 0.5f, pos.getZ() + 0.5f + boost.x*6, new ItemStack(xerca.xercamod.common.item.Items.DONER_SLICE));
                    itementity.setDefaultPickupDelay();
                    itementity.addVelocity(boost.x, 0, boost.z);
                    itementity.velocityChanged = true;
                    worldIn.addEntity(itementity);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return renderType;
    }
}
