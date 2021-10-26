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
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemMusicSheet extends Item {
    ItemMusicSheet() {
        super(new Properties().tab(Items.musicTab).stacksTo(1));
        this.setRegistryName("music_sheet");
    }

    public void verifyTagAfterLoad(CompoundTag nbt) {
        XercaMusic.LOGGER.warn("verifyTagAfterLoad " + nbt);
        if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT){ //this doesn't work at all
//        if(FMLEnvironment.dist == Dist.CLIENT){ this doesn't work at all either
            XercaMusic.LOGGER.warn("verifyTagAfterLoad on client");
            if(nbt.contains("id") && nbt.contains("ver")){
                UUID id = nbt.getUUID("id");
                int version = nbt.getInt("ver");
                MusicManagerClient.checkMusicData(id, version);
            }
        }
    }

    public CompoundTag getShareTag(ItemStack stack)
    {
        XercaMusic.LOGGER.warn("getShareTag " + stack.getTag());
        return stack.getTag();
    }

    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt)
    {
        XercaMusic.LOGGER.warn("readShareTag " + nbt);
        stack.setTag(nbt);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if(worldIn.isClientSide){
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientStuff::showMusicGui);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null){
                String s = tag.getString("title");
                if (!StringUtil.isNullOrEmpty(s)) {
                    return new TextComponent(s);
                }
            }
        }
        return super.getName(stack);
    }

    public static byte[] getMusic(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null && tag.contains("music")){
                return tag.getByteArray("music");
            }
        }
        return null;
    }

    public static ArrayList<NoteEvent> getNotes(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null && tag.contains("notes")){
                ArrayList<NoteEvent> notes = new ArrayList<>();
                NoteEvent.fillArrayFromNBT(notes, tag);
                return notes;
            }
        }
        return null;
    }

    public static int getBPS(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null && tag.contains("bps")){
                return tag.getByte("bps");
            }
        }
        return -1;
    }

    public static int getPrevInstrument(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null && tag.contains("prevIns")){
                return tag.getByte("prevIns");
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getOrCreateTag();
            String s = tag.getString("author");

            if (!StringUtil.isNullOrEmpty(s)) {
                tooltip.add(new TranslatableComponent("note.byAuthor", s));
            }

            int generation = tag.getInt("generation");
            // generation = 0 means empty, 1 means original, more means copy
            if(generation > 0){
                tooltip.add((new TranslatableComponent("note.generation." + (generation - 1))).withStyle(ChatFormatting.GRAY));
            }

            if(tag.contains("l")) {
                int lengthBeats = tag.getInt("l");
                tooltip.add((new TranslatableComponent("note.length", lengthBeats)).withStyle(ChatFormatting.GRAY));
            }
            if(tag.contains("bps")) {
                int bps = tag.getInt("bps");
                tooltip.add((new TranslatableComponent("note.tempo", bps*60)).withStyle(ChatFormatting.GRAY));
            }
            if(tag.contains("prevIns")){
                byte ins = tag.getByte("prevIns");
                if(ins >= 0 && ins < Items.instruments.length){
                    Component name = Items.instruments[ins].getName(new ItemStack(Items.instruments[ins]));
                    tooltip.add((new TranslatableComponent("note.preview_instrument", name)).withStyle(ChatFormatting.GRAY));
                }
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
