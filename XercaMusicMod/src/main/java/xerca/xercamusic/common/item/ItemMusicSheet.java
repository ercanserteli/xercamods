package xerca.xercamusic.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercamusic.client.ModClient;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

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

    ItemMusicSheet(Properties properties) {
        super(properties.stacksTo(1));
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
        int length = nbt.getIntOr(KEY_LENGTH_OLD, 0);
        byte pause = nbt.getByteOr(KEY_PAUSE_OLD, (byte) 0);
        byte[] music = nbt.getByteArray(KEY_MUSIC_OLD).orElse(new byte[0]);

        int safePause = Math.max(1, pause);
        byte bps = (byte) Math.clamp(Math.round(20.f / safePause), 1, 50);
        List<NoteEvent> notes = oldMusicToNotes(music);

        nbt.putInt(KEY_LENGTH, length + ADD_TO_OLD_END);
        nbt.putByte(KEY_BPS, bps);
        UUID id;
        if (nbt.contains(KEY_AUTHOR) && nbt.contains(KEY_TITLE)) {
            String author = nbt.getStringOr(KEY_AUTHOR, "");
            String title = nbt.getStringOr(KEY_TITLE, "");
            IItemInstrument.Pair<String, String> key = new IItemInstrument.Pair<>(author, title);
            if (CONVERT_MAP.containsKey(key)) {
                id = CONVERT_MAP.get(key);
            } else {
                id = UUID.randomUUID();
                CONVERT_MAP.put(key, id);
                MusicManager.setMusicData(id, 1, notes, null, server);
            }
        } else {
            id = UUID.randomUUID();
            MusicManager.setMusicData(id, 1, notes, null, server);
        }

        nbt.store(KEY_ID, UUIDUtil.CODEC, id);
        nbt.putInt(KEY_VERSION, 1);

        nbt.remove(KEY_LENGTH_OLD);
        nbt.remove(KEY_PAUSE_OLD);
        nbt.remove(KEY_MUSIC_OLD);
        return notes;
    }

    public static byte getBPS(ItemStack stack) {
        return stack.getOrDefault(Items.SHEET_BPS, (byte) 0);
    }

    public static int getPrevInstrument(ItemStack stack) {
        Byte prevIns = stack.get(Items.SHEET_PREV_INSTRUMENT);
        if (prevIns != null) {
            return prevIns;
        }
        return -1;
    }

    public static float getVolume(ItemStack stack) {
        return stack.getOrDefault(Items.SHEET_VOLUME, 1.f);
    }

    public static boolean isEmptySheet(ItemStack stack) {
        return stack.get(Items.SHEET_GENERATION) == null && stack.get(Items.SHEET_ID) == null && stack.get(Items.SHEET_VERSION) == null;
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        if (worldIn.isClientSide()) {
            onlyRunOnClient(() -> ModClient::showMusicGui);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getName(ItemStack stack) {
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        String s = stack.get(Items.SHEET_AUTHOR);

        if (tooltipDisplay.shows(Items.SHEET_AUTHOR) && s != null) {
            tooltip.accept(Component.translatable("note.byAuthor", s));
        }

        int generation = stack.getOrDefault(Items.SHEET_GENERATION, 0);
        // generation = 0=empty, 1=original, 2=copy of org, 3=copy of copy
        if (tooltipDisplay.shows(Items.SHEET_GENERATION) && generation > 0) {
            tooltip.accept(Component.translatable("note.generation." + (generation - 1))
                    .withStyle(generation == 1 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        }

        int length = stack.getOrDefault(Items.SHEET_LENGTH, 0);
        if (tooltipDisplay.shows(Items.SHEET_LENGTH) && length > 0) {
            tooltip.accept(Component.translatable("note.length", length).withStyle(ChatFormatting.GRAY));
        }
        int bps = getBPS(stack);
        if (tooltipDisplay.shows(Items.SHEET_BPS) && bps > 0) {
            tooltip.accept(Component.translatable("note.tempo", bps * 60).withStyle(ChatFormatting.GRAY));
        }
        int prevIns = getPrevInstrument(stack);
        if (tooltipDisplay.shows(Items.SHEET_PREV_INSTRUMENT) && prevIns >= 0 && prevIns < Items.INSTRUMENTS.size()) {
            Component name = ((Item) Items.INSTRUMENTS.get(prevIns)).getName(new ItemStack((Item) Items.INSTRUMENTS.get(prevIns)));
            tooltip.accept(Component.translatable("note.preview_instrument", name).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockpos);
        if (blockState.getBlock() == Blocks.MUSIC_BOX &&
                blockState.hasProperty(BlockMusicBox.HAS_MUSIC) &&
                !blockState.getValue(BlockMusicBox.HAS_MUSIC)) {
            ItemStack itemstack = context.getItemInHand();
            if (!world.isClientSide() && itemstack.get(Items.SHEET_ID) != null) {
                BlockMusicBox.insertMusic(world, blockpos, blockState, itemstack.copyWithCount(1));
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

    public static final int SIGNED_STACK_SIZE = 16;

    /**
     * Signed sheets that are the same can be stacked
     */
    public static void updateStackSize(ItemStack stack) {
        if (stack.getOrDefault(Items.SHEET_GENERATION, 0) > 0) {
            stack.set(DataComponents.MAX_STACK_SIZE, SIGNED_STACK_SIZE);
        } else if (stack.getOrDefault(DataComponents.MAX_STACK_SIZE, 1) > 1) {
            stack.remove(DataComponents.MAX_STACK_SIZE);
        }
    }

}
