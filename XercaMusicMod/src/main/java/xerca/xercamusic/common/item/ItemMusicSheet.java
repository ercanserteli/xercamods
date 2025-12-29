package xerca.xercamusic.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
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
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.onlyRunOnClient;

public class ItemMusicSheet extends Item {
    private static final HashMap<IItemInstrument.Pair<String, String>, UUID> CONVERT_MAP = new HashMap<>();
    private static final int ADD_TO_OLD_END = 8;
    public static final String KEY_NOTES = "notes";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TITLE = "title";
    public static final String KEY_VERSION = "ver";
    public static final String KEY_GENERATION = "generation";
    public static final String KEY_ID = "id";
    public static final String KEY_LENGTH = "l";
    public static final String KEY_BPS = "bps";
    public static final String KEY_PREV_INSTRUMENT_LOCKED = "piLocked";
    public static final String KEY_PREV_INSTRUMENT = "prevIns";
    public static final String KEY_HIGHLIGHT_INTERVAL = "hl";
    public static final String KEY_VOLUME = "vol";
    public static final String KEY_MUSIC_OLD = "music";
    public static final String KEY_LENGTH_OLD = "length";
    public static final String KEY_PAUSE_OLD = "pause";

    ItemMusicSheet() {
        super(new Properties().stacksTo(1));
    }

    public static List<NoteEvent> oldMusicToNotes(byte[] music) {
        ArrayList<NoteEvent> notes = new ArrayList<>();
        for (int i = 0; i < music.length; i++) {
            if (music[i] > 0) {
                int nextTime = -1;
                for (int j = i + 1; j < music.length; j++) {
                    if (music[j] > 0) {
                        nextTime = j;
                        break;
                    }
                }
                int l = 1;
                if (nextTime > i && (nextTime - i) < 20) {
                    l = nextTime - i;
                } else if (i == music.length - 1) {
                    l = ADD_TO_OLD_END;
                }

                byte note = (byte) (music[i] + 32);
                notes.add(new NoteEvent(note, (short) i, (byte) 64, (byte) l));
            }
        }
        return notes;
    }

    public static List<NoteEvent> convertFromOld(CompoundTag nbt, MinecraftServer server) {
        int length = nbt.getInt(KEY_LENGTH_OLD);
        byte pause = nbt.getByte(KEY_PAUSE_OLD);
        byte[] music = nbt.getByteArray(KEY_MUSIC_OLD);

        byte bps = (byte) Math.round(20.f / pause);
        List<NoteEvent> notes = oldMusicToNotes(music);

        nbt.putInt(KEY_LENGTH, length + ADD_TO_OLD_END);
        nbt.putByte(KEY_BPS, bps);
        UUID id;
        if (nbt.contains(KEY_AUTHOR) && nbt.contains(KEY_TITLE)) {
            String author = nbt.getString(KEY_AUTHOR);
            String title = nbt.getString(KEY_TITLE);
            IItemInstrument.Pair<String, String> key = new IItemInstrument.Pair<>(author, title);
            if (CONVERT_MAP.containsKey(key)) {
                id = CONVERT_MAP.get(key);
            } else {
                id = UUID.randomUUID();
                CONVERT_MAP.put(key, id);
                MusicManager.setMusicData(id, 1, notes, server);
            }
        } else {
            id = UUID.randomUUID();
            MusicManager.setMusicData(id, 1, notes, server);
        }

        nbt.putUUID(KEY_ID, id);
        nbt.putInt(KEY_VERSION, 1);

        nbt.remove(KEY_LENGTH_OLD);
        nbt.remove(KEY_PAUSE_OLD);
        nbt.remove(KEY_MUSIC_OLD);
        return notes;
    }

    public static byte getBPS(@Nonnull ItemStack stack) {
        return stack.getOrDefault(Items.SHEET_BPS, (byte) 0);
    }

    public static int getPrevInstrument(@Nonnull ItemStack stack) {
        Byte prevIns = stack.get(Items.SHEET_PREV_INSTRUMENT);
        if (prevIns != null) {
            return prevIns;
        }
        return -1;
    }

    public static float getVolume(@Nonnull ItemStack stack) {
        return stack.getOrDefault(Items.SHEET_VOLUME, 1.f);
    }

    public static boolean isEmptySheet(@Nonnull ItemStack stack) {
        return stack.get(Items.SHEET_GENERATION) == null && stack.get(Items.SHEET_ID) == null && stack.get(Items.SHEET_VERSION) == null;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if (worldIn.isClientSide) {
            onlyRunOnClient(() -> ClientStuff::showMusicGui);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        String title = stack.get(Items.SHEET_TITLE);
        if (title != null) {
            return Component.literal(title);
        }
        return super.getName(stack);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
        String s = stack.get(Items.SHEET_AUTHOR);

        if (s != null) {
            tooltip.add(Component.translatable("note.byAuthor", s));
        }

        int generation = stack.getOrDefault(Items.SHEET_GENERATION, 0);
        // generation = 0 means empty, 1 means original, more means copy
        if (generation > 0) {
            tooltip.add(Component.translatable("note.generation." + (generation - 1))
                    .withStyle(generation == 1 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        }

        int length = stack.getOrDefault(Items.SHEET_LENGTH, 0);
        if (length > 0) {
            tooltip.add(Component.translatable("note.length", length).withStyle(ChatFormatting.GRAY));
        }
        int bps = getBPS(stack);
        if (bps > 0) {
            tooltip.add(Component.translatable("note.tempo", bps * 60).withStyle(ChatFormatting.GRAY));
        }
        int prevIns = getPrevInstrument(stack);
        if (prevIns >= 0 && prevIns < Items.INSTRUMENTS.size()) {
            Component name = ((Item) Items.INSTRUMENTS.get(prevIns)).getName(new ItemStack((Item) Items.INSTRUMENTS.get(prevIns)));
            tooltip.add(Component.translatable("note.preview_instrument", name).withStyle(ChatFormatting.GRAY));
        }
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockpos);
        if (blockState.getBlock() == Blocks.MUSIC_BOX && !blockState.getValue(BlockMusicBox.HAS_MUSIC)) {
            ItemStack itemstack = context.getItemInHand();
            if (!world.isClientSide && itemstack.get(Items.SHEET_ID) != null) {
                BlockMusicBox.insertMusic(world, blockpos, blockState, itemstack.copy());
                Player player = context.getPlayer();
                if (player != null && !player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(Items.SHEET_GENERATION, 0) > 0;
    }
}
