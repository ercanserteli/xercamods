package xerca.xercamusic.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
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
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.SoundEvents;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemMusicSheet extends Item {
    static final private HashMap<ItemInstrument.Pair<String, String>, UUID> convertMap = new HashMap<>();
    static final private int addToOldEnd = 8;

    ItemMusicSheet() {
        super(new Properties().tab(Items.musicTab).stacksTo(1));
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

    public static ArrayList<NoteEvent> convertFromOld(CompoundTag nbt, MinecraftServer server){
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
            ItemInstrument.Pair<String, String> key = new ItemInstrument.Pair<>(author, title);
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

        nbt.putUUID("id", id);
        nbt.putInt("ver", 1);

        nbt.remove("length");
        nbt.remove("pause");
        nbt.remove("music");
        return notes;
    }

    public void verifyTagAfterLoad(CompoundTag nbt) {
//        XercaMusic.LOGGER.info("verifyTagAfterLoad " + nbt);
        if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER){
            if(nbt.contains("music")){
                // Old version
                MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                convertFromOld(nbt, server);
            }
        }
    }

    public CompoundTag getShareTag(ItemStack stack)
    {
//        XercaMusic.LOGGER.info("getShareTag " + stack.getTag());
        return stack.getTag();
    }

    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt)
    {
//        XercaMusic.LOGGER.info("readShareTag " + nbt);
        stack.setTag(nbt);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if(worldIn.isClientSide){
            playerIn.playSound(SoundEvents.OPEN_SCROLL, 1.0f, 0.8f + worldIn.random.nextFloat()*0.4f);
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

    public static float getVolume(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
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
                tooltip.add((new TranslatableComponent("note.generation." + (generation - 1)))
                        .withStyle(generation == 1 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
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


            if(tag.contains("HEDE")) {
                tooltip.add(new TextComponent("HEDE"));
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
