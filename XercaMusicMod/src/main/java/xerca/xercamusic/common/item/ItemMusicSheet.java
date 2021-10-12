package xerca.xercamusic.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import xerca.xercamusic.client.ClientStuff;
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
        super(new Properties().tab(Items.musicTab).stacksTo(1));
        this.setRegistryName("music_sheet");
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if(worldIn.isClientSide){
//            XercaMusic.proxy.showMusicGui();
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientStuff::showMusicGui);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbttagcompound = stack.getTag();
            if(nbttagcompound != null){
                String s = nbttagcompound.getString("title");
                if (!StringUtil.isNullOrEmpty(s)) {
                    return new TextComponent(s);
                }
            }
        }
        return super.getName(stack);
    }

    public static byte[] getMusic(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbttagcompound = stack.getTag();
            if(nbttagcompound != null && nbttagcompound.contains("music")){
                return nbttagcompound.getByteArray("music");
            }
        }
        return null;
    }

//    public static int getPause(@Nonnull ItemStack stack) {
//        if (stack.hasTag()) {
//            CompoundTag nbttagcompound = stack.getTag();
//            if(nbttagcompound != null && nbttagcompound.contains("pause")){
//                return nbttagcompound.getByte("pause");
//            }
//        }
//        return -1;
//    }

    public static int getBPS(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbttagcompound = stack.getTag();
            if(nbttagcompound != null && nbttagcompound.contains("bps")){
                return nbttagcompound.getByte("bps");
            }
        }
        return -1;
    }

    public static int getPrevInstrument(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag nbttagcompound = stack.getTag();
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            String s = tag.getString("author");

            if (!StringUtil.isNullOrEmpty(s)) {
                tooltip.add(new TranslatableComponent("note.byAuthor", s));
            }

            int generation = tag.getInt("generation");
            // generation = 0 means empty, 1 means original, more means copy
            if(generation > 0){
                tooltip.add((new TranslatableComponent("note.generation." + (generation - 1))).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState iblockstate = world.getBlockState(blockpos);
        if (iblockstate.getBlock() == Blocks.MUSIC_BOX && !iblockstate.getValue(BlockMusicBox.HAS_MUSIC)) {
            ItemStack itemstack = context.getItemInHand();
            if (itemstack.hasTag()) {
                if (!world.isClientSide) {
                    BlockMusicBox.insertMusic(world, blockpos, iblockstate, itemstack.copy());

                    if(context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild){
                        itemstack.shrink(1);
                    }
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack stack) {
        if(stack.hasTag()){
            CompoundTag ntc = stack.getTag();
            if(ntc.contains("generation")){
                int generation = ntc.getInt("generation");
                return generation > 0;
            }
        }
        return false;
    }
}
