package xerca.xercamusic.common.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.tile_entity.TileEntityMetronome;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMusicSheet extends Item {
    public static int[] pauseToBPMLevel = new int[21];

    static {
        for(int i = 0; i < TileEntityMetronome.pauseLevels.length; i++){
            pauseToBPMLevel[TileEntityMetronome.pauseLevels[i]] = i;
        }
    }

    ItemMusicSheet() {
        super(new Properties().group(Items.musicTab).maxStackSize(1));
        this.setRegistryName("music_sheet");
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        if(worldIn.isRemote){
            XercaMusic.proxy.showMusicGui();
        }
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT nbttagcompound = stack.getTag();
            if(nbttagcompound != null){
                String s = nbttagcompound.getString("title");
                if (!StringUtils.isNullOrEmpty(s)) {
                    return new StringTextComponent(s);
                }
            }
        }
        return super.getDisplayName(stack);
    }

    public static byte[] getMusic(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT nbttagcompound = stack.getTag();
            if(nbttagcompound != null && nbttagcompound.contains("music")){
                return nbttagcompound.getByteArray("music");
            }
        }
        return null;
    }

    public static int getPause(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT nbttagcompound = stack.getTag();
            if(nbttagcompound != null && nbttagcompound.contains("pause")){
                return nbttagcompound.getByte("pause");
            }
        }
        return -1;
    }

    public static int getPrevInstrument(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT nbttagcompound = stack.getTag();
            if(nbttagcompound != null && nbttagcompound.contains("prevIns")){
                return nbttagcompound.getByte("prevIns");
            }
        }
        return -1;
    }

    /**
     * Gets the generation of the book (how many times it has been cloned)
     */
    public static int getGeneration(ItemStack stack) {
        return stack.getTag().getInt("generation");
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            String s = tag.getString("author");

            if (!StringUtils.isNullOrEmpty(s)) {
                tooltip.add(new TranslationTextComponent("note.byAuthor", s));
            }

            int generation = tag.getInt("generation");
            // generation = 0 means empty, 1 means original, more means copy
            if(generation > 0){
                tooltip.add((new TranslationTextComponent("note.generation." + (generation - 1))).mergeStyle(TextFormatting.GRAY));
            }
        }
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState iblockstate = world.getBlockState(blockpos);
        if (iblockstate.getBlock() == Blocks.MUSIC_BOX && !iblockstate.get(BlockMusicBox.HAS_MUSIC)) {
            ItemStack itemstack = context.getItem();
            if (itemstack.hasTag()) {
                if (!world.isRemote) {
                    BlockMusicBox.insertMusic(world, blockpos, iblockstate, itemstack.copy());

                    if(context.getPlayer() != null && !context.getPlayer().abilities.isCreativeMode){
                        itemstack.shrink(1);
                    }
                }
            }

            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        if(stack.hasTag()){
            CompoundNBT ntc = stack.getTag();
            if(ntc.contains("generation")){
                int generation = ntc.getInt("generation");
                return generation > 0;
            }
        }
        return false;
    }
}
