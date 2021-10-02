package xerca.xercamusic.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemBlockInstrument extends ItemInstrument{
    private final Block block;
    ItemBlockInstrument(String name, boolean shouldCutOff, int instrumentId, Block block) {
        super(name, shouldCutOff, instrumentId);
        this.block = block;
    }

    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        if(blockState.getBlock() == Blocks.MUSIC_BOX && !blockState.getValue(BlockMusicBox.HAS_INSTRUMENT)){
            BlockMusicBox.insertInstrument(context.getLevel(), context.getClickedPos(), blockState, this);
            if(context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild){
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        InteractionResult actionresulttype = this.tryPlace(new BlockPlaceContext(context));
        return actionresulttype != InteractionResult.SUCCESS && this.isEdible() ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : actionresulttype;
    }

    public InteractionResult tryPlace(BlockPlaceContext context) {
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockitemusecontext = this.getBlockItemUseContext(context);
            if (blockitemusecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getStateForPlacement(blockitemusecontext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockitemusecontext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockitemusecontext.getClickedPos();
                    Level world = blockitemusecontext.getLevel();
                    Player playerentity = blockitemusecontext.getPlayer();
                    ItemStack itemstack = blockitemusecontext.getItemInHand();
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    Block block = blockstate1.getBlock();
                    if (block == blockstate.getBlock()) {
                        blockstate1 = this.updateBlockStateFromTag(blockpos, world, itemstack, blockstate1);
                        this.onBlockPlaced(blockpos, world, playerentity, itemstack, blockstate1);
                        block.setPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
                        if (playerentity instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)playerentity, blockpos, itemstack);
                        }
                    }

                    SoundType soundtype = blockstate1.getSoundType(world, blockpos, context.getPlayer());
                    world.playSound(playerentity, blockpos, this.getPlaceSound(blockstate1, world, blockpos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    itemstack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        }
    }

    @Deprecated //Forge: Use more sensitive version {@link BlockItem#getPlaceSound(BlockState, IBlockReader, BlockPos, Entity) }
    protected SoundEvent getPlaceSound(BlockState state) {
        return state.getSoundType().getPlaceSound();
    }

    //Forge: Sensitive version of BlockItem#getPlaceSound
    protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
        return state.getSoundType(world, pos, entity).getPlaceSound();
    }

    @Nullable
    public BlockPlaceContext getBlockItemUseContext(BlockPlaceContext context) {
        return context;
    }

    protected boolean onBlockPlaced(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state) {
        return setTileEntityNBT(worldIn, player, pos, stack);
    }

    @Nullable
    protected BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.getBlock().getStateForPlacement(context);
        return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
    }

    private BlockState updateBlockStateFromTag(BlockPos p_219985_1_, Level p_219985_2_, ItemStack p_219985_3_, BlockState p_219985_4_) {
        BlockState blockstate = p_219985_4_;
        CompoundTag compoundnbt = p_219985_3_.getTag();
        if (compoundnbt != null) {
            CompoundTag compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> statecontainer = p_219985_4_.getBlock().getStateDefinition();

            for(String s : compoundnbt1.getAllKeys()) {
                Property<?> property = statecontainer.getProperty(s);
                if (property != null) {
                    String s1 = compoundnbt1.get(s).getAsString();
                    blockstate = updateState(blockstate, property, s1);
                }
            }
        }

        if (blockstate != p_219985_4_) {
            p_219985_2_.setBlock(p_219985_1_, blockstate, 2);
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState updateState(BlockState p_219988_0_, Property<T> p_219988_1_, String p_219988_2_) {
        return p_219988_1_.getValue(p_219988_2_).map((p_219986_2_) -> p_219988_0_.setValue(p_219988_1_, p_219986_2_)).orElse(p_219988_0_);
    }

    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player playerentity = context.getPlayer();
        CollisionContext iselectioncontext = playerentity == null ? CollisionContext.empty() : CollisionContext.of(playerentity);
        return (!this.checkPosition() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(state, context.getClickedPos(), iselectioncontext);
    }

    protected boolean checkPosition() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    public static boolean setTileEntityNBT(Level worldIn, @Nullable Player player, BlockPos pos, ItemStack stackIn) {
        MinecraftServer minecraftserver = worldIn.getServer();
        if (minecraftserver == null) {
            return false;
        } else {
            CompoundTag compoundnbt = stackIn.getTagElement("BlockEntityTag");
            if (compoundnbt != null) {
                BlockEntity tileentity = worldIn.getBlockEntity(pos);
                if (tileentity != null) {
                    if (!worldIn.isClientSide && tileentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
                        return false;
                    }

                    CompoundTag compoundnbt1 = tileentity.save(new CompoundTag());
                    CompoundTag compoundnbt2 = compoundnbt1.copy();
                    compoundnbt1.merge(compoundnbt);
                    compoundnbt1.putInt("x", pos.getX());
                    compoundnbt1.putInt("y", pos.getY());
                    compoundnbt1.putInt("z", pos.getZ());
                    if (!compoundnbt1.equals(compoundnbt2)) {
//                        tileentity.load(worldIn.getBlockState(pos), compoundnbt1);
                        tileentity.load(compoundnbt1); //todo: don't know if right?
                        tileentity.setChanged();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     * Returns the unlocalized name of this item.
     */
    @Override
    public String getDescriptionId() {
        return this.getBlock().getDescriptionId();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            this.getBlock().fillItemCategory(group, items);
        }

    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        this.getBlock().appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public Block getBlock() {
        return this.getBlockRaw() == null ? null : this.getBlockRaw().delegate.get();
    }

    private Block getBlockRaw() {
        return this.block;
    }

    public void addToBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
        blockToItemMap.put(this.getBlock(), itemIn);
    }

    public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
        blockToItemMap.remove(this.getBlock());
    }
}
