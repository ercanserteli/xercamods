package xerca.xercamusic.common.item;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.util.internal.StringUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;

public class ItemMusicSheet extends Item {
    static final private HashMap<AbstractMap.SimpleImmutableEntry<String, String>, UUID> convertMap = new HashMap<>();
    static final private int addToOldEnd = 8;

    ItemMusicSheet() {
        super(new Properties().group(Items.musicTab).maxStackSize(1));
        this.setRegistryName("music_sheet");
    }

    public static ArrayList<NoteEvent> oldMusicToNotes(byte[] music){
        ArrayList<NoteEvent> notes = new ArrayList<>();
        for(int i=0; i<music.length; i++){
            if(music[i] > 0){
                int nextTime = -1;
                for(int j=i+1; j<music.length; j++){
                    if(music[j] > 0){
                        nextTime = j;
                        break;
                    }
                }
                int l = 1;
                if(nextTime > i && (nextTime - i) < 20){
                    l = nextTime - i;
                }
                else if(i == music.length-1){
                    l = addToOldEnd;
                }

                byte note = (byte)(music[i] + 32);
                notes.add(new NoteEvent(note, (short)i, (byte)64, (byte)l));
            }
        }
        return notes;
    }

    public static ArrayList<NoteEvent> convertFromOld(CompoundNBT nbt, MinecraftServer server){
        int length = nbt.getInt("length");
        byte pause = nbt.getByte("pause");
        byte[] music = nbt.getByteArray("music");

        byte bps = (byte)Math.round(20.f/(float)pause);
        ArrayList<NoteEvent> notes = oldMusicToNotes(music);

        nbt.putInt("l", length + addToOldEnd);
        nbt.putByte("bps", bps);
        UUID id;
        if(nbt.contains("author") && nbt.contains("title")){
            String author = nbt.getString("author");
            String title = nbt.getString("title");
            AbstractMap.SimpleImmutableEntry<String, String> key = new AbstractMap.SimpleImmutableEntry<>(author, title);
            if(convertMap.containsKey(key)){
                id = convertMap.get(key);
            }
            else{
                id = UUID.randomUUID();
                convertMap.put(key, id);
                MusicManager.setMusicData(id, 1, notes, server);
            }
        }
        else {
            id = UUID.randomUUID();
            MusicManager.setMusicData(id, 1, notes, server);
        }

        nbt.putUniqueId("id", id);
        nbt.putInt("ver", 1);

        nbt.remove("length");
        nbt.remove("pause");
        nbt.remove("music");
        return notes;
    }

    @Override
    public boolean updateItemStackNBT(CompoundNBT nbt) {
//        XercaMusic.LOGGER.info("verifyTagAfterLoad " + nbt);
        if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER){
            if(nbt.contains("music")){
                // Old version
                MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                convertFromOld(nbt, server);
                return true;
            }
            else if(nbt.contains("tag") && nbt.getCompound("tag").contains("music")){
                // Old version in tag
                MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                convertFromOld(nbt.getCompound("tag"), server);
                return true;
            }
        }
        return false;
    }

    @Override
    public CompoundNBT getShareTag(ItemStack stack)
    {
//        XercaMusic.LOGGER.info("getShareTag " + stack.getTag());
        return stack.getTag();
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt)
    {
//        XercaMusic.LOGGER.info("readShareTag " + nbt);
        stack.setTag(nbt);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
    	final ItemStack heldItem = playerIn.getHeldItem(hand);
        if(worldIn.isRemote){
            playerIn.playSound(SoundEvents.OPEN_SCROLL, 1.0f, 0.8f + worldIn.rand.nextFloat()*0.4f);
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientStuff::showMusicGui);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag != null){
                String s = tag.getString("title");
                if (!StringUtil.isNullOrEmpty(s)) {
                    return new StringTextComponent(s);
                }
            }
        }
        return super.getDisplayName(stack);
    }

    public static byte[] getMusic(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag != null && tag.contains("music")){
                return tag.getByteArray("music");
            }
        }
        return null;
    }

    public static int getBPS(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag != null && tag.contains("bps")){
                return tag.getByte("bps");
            }
        }
        return -1;
    }

    public static int getPrevInstrument(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag != null && tag.contains("prevIns")){
                return tag.getByte("prevIns");
            }
        }
        return -1;
    }

    public static float getVolume(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag != null && tag.contains("vol")){
                return tag.getFloat("vol");
            }
        }
        return 1.0f;
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getOrCreateTag();
            String s = tag.getString("author");

            if (!StringUtil.isNullOrEmpty(s)) {
                tooltip.add(new TranslationTextComponent("note.byAuthor", s));
            }

            int generation = tag.getInt("generation");
            // generation = 0 means empty, 1 means original, more means copy
            if(generation > 0){
                tooltip.add((new TranslationTextComponent("note.generation." + (generation - 1)))
                        .mergeStyle(generation == 1 ? TextFormatting.GOLD : TextFormatting.GRAY));
            }

            if(tag.contains("l")) {
                int lengthBeats = tag.getInt("l");
                tooltip.add((new TranslationTextComponent("note.length", lengthBeats)).mergeStyle(TextFormatting.GRAY));
            }
            if(tag.contains("bps")) {
                int bps = tag.getInt("bps");
                tooltip.add((new TranslationTextComponent("note.tempo", bps*60)).mergeStyle(TextFormatting.GRAY));
            }
            if(tag.contains("prevIns")){
                byte ins = tag.getByte("prevIns");
                if(ins >= 0 && ins < Items.instruments.length){
                    ITextComponent name = Items.instruments[ins].getDisplayName(new ItemStack(Items.instruments[ins]));
                    tooltip.add((new TranslationTextComponent("note.preview_instrument", name)).mergeStyle(TextFormatting.GRAY));
                }
            }


            if(tag.contains("HEDE")) {
                tooltip.add(new StringTextComponent("HEDE"));
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
