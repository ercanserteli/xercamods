package xerca.xercamusic.common.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemBlockInstrument extends ItemInstrument{
    private final Block block;
    ItemBlockInstrument(String name, boolean shouldCutOff, int instrumentId, Block block, int minOctave, int maxOctave) {
        super(name, shouldCutOff, instrumentId, minOctave, maxOctave);
        this.block = block;
    }

    /**
     * Called when this item is used when targeting a Block
     */
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        if(blockState.getBlock() == Blocks.MUSIC_BOX && !blockState.getValue(BlockMusicBox.HAS_INSTRUMENT)){
            BlockMusicBox.insertInstrument(context.getLevel(), context.getClickedPos(), blockState, this);
            if(context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild){
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        InteractionResult interactionResult = this.tryPlace(new BlockPlaceContext(context));
        return interactionResult != InteractionResult.SUCCESS && this.isEdible() ? this.use(context.getLevel(), Objects.requireNonNull(context.getPlayer()), context.getHand()).getResult() : interactionResult;
    }

    public InteractionResult tryPlace(BlockPlaceContext context) {
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockItemUseContext = this.getBlockItemUseContext(context);
            if (blockItemUseContext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getStateForPlacement(blockItemUseContext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockItemUseContext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockItemUseContext.getClickedPos();
                    Level world = blockItemUseContext.getLevel();
                    Player player = blockItemUseContext.getPlayer();
                    ItemStack itemstack = blockItemUseContext.getItemInHand();
                    BlockState blockState = world.getBlockState(blockpos);
                    Block block = blockState.getBlock();
                    if (block == blockstate.getBlock()) {
                        blockState = this.updateBlockStateFromTag(blockpos, world, itemstack, blockState);
                        block.setPlacedBy(world, blockpos, blockState, player, itemstack);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
                        }
                    }

                    SoundType soundtype = blockState.getSoundType(world, blockpos, context.getPlayer());
                    world.playSound(player, blockpos, this.getPlaceSound(blockState, world, blockpos, Objects.requireNonNull(context.getPlayer())), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
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

    @Nullable
    protected BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = null;
        if(this.getBlock() != null) {
            blockstate = this.getBlock().getStateForPlacement(context);
        }
        return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
    }

    private BlockState updateBlockStateFromTag(BlockPos p_219985_1_, Level p_219985_2_, ItemStack p_219985_3_, BlockState p_219985_4_) {
        BlockState blockstate = p_219985_4_;
        CompoundTag tag = p_219985_3_.getTag();
        if (tag != null) {
            CompoundTag blockStateTag = tag.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> stateDefinition = p_219985_4_.getBlock().getStateDefinition();

            for(String s : blockStateTag.getAllKeys()) {
                Property<?> property = stateDefinition.getProperty(s);
                if (property != null) {
                    String s1 = Objects.requireNonNull(blockStateTag.get(s)).getAsString();
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
        Player player = context.getPlayer();
        CollisionContext collisionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (!this.checkPosition() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(state, context.getClickedPos(), collisionContext);
    }

    protected boolean checkPosition() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    /**
     * Returns the unlocalized name of this item.
     */
    @Override
    public String getDescriptionId() {
        Block block = this.getBlock();
        if(block != null) {
            return this.getBlock().getDescriptionId();
        }
        return "";
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group) && this.getBlock() != null) {
            this.getBlock().fillItemCategory(group, items);
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if(this.getBlock() != null) {
            this.getBlock().appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    }

    @Nullable
    public Block getBlock() {
        return this.getBlockRaw() == null ? null : this.getBlockRaw().delegate.get();
    }

    @Nullable
    private Block getBlockRaw() {
        return this.block;
    }

    public void addToBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
        blockToItemMap.put(this.getBlock(), itemIn);
    }

}
