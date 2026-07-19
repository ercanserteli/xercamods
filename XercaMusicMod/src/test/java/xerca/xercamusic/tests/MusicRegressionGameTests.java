package xerca.xercamusic.tests;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.*;
import xerca.xercamusic.common.block.BlockMetronome;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.item.RecipeNoteCloning;
import xerca.xercamusic.common.packets.clientbound.SingleNoteClientPacket;
import xerca.xercamusic.common.packets.clientbound.TripleNoteClientPacket;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;
import xerca.xercamusic.common.tile_entity.TileEntityMetronome;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static xerca.xercamusic.tests.TestAsserts.assertTrue;

@SuppressWarnings({"unused", "PMD.AvoidAccessibilityAlteration"})
public final class MusicRegressionGameTests {
    private static final Field MUSIC_BOX_IS_PLAYING_FIELD;
    private static final Field MUSIC_BOX_BPS_FIELD;
    private static final Field MUSIC_BOX_VOLUME_FIELD;
    private static final Field MUSIC_BOX_WARNED_MISSING_SHEET_ID_FIELD;
    private static final Field MUSIC_BOX_WARNED_MISSING_SHEET_VERSION_FIELD;
    private static final Field METRONOME_AGE_FIELD;
    private static final Field METRONOME_OLD_POWERED_STATE_FIELD;
    private static final Field METRONOME_COUNTDOWN_FIELD;
    private static final String PRE_GLISSANDO_CLIPBOARD_SAMPLE = "MgAAAD8AAAAEKAAAQBApABBAECgAIEAQKwAwQBA=";
    private static final String VERY_OLD_CLIPBOARD_SAMPLE = "JCAiGwAAGyIkIA==";

    static {
        try {
            MUSIC_BOX_IS_PLAYING_FIELD = TileEntityMusicBox.class.getDeclaredField("isPlaying");
            MUSIC_BOX_IS_PLAYING_FIELD.setAccessible(true);
            MUSIC_BOX_BPS_FIELD = TileEntityMusicBox.class.getDeclaredField("bps");
            MUSIC_BOX_BPS_FIELD.setAccessible(true);
            MUSIC_BOX_VOLUME_FIELD = TileEntityMusicBox.class.getDeclaredField("volume");
            MUSIC_BOX_VOLUME_FIELD.setAccessible(true);
            MUSIC_BOX_WARNED_MISSING_SHEET_ID_FIELD = TileEntityMusicBox.class.getDeclaredField("warnedMissingSheetId");
            MUSIC_BOX_WARNED_MISSING_SHEET_ID_FIELD.setAccessible(true);
            MUSIC_BOX_WARNED_MISSING_SHEET_VERSION_FIELD = TileEntityMusicBox.class.getDeclaredField("warnedMissingSheetVersion");
            MUSIC_BOX_WARNED_MISSING_SHEET_VERSION_FIELD.setAccessible(true);
            METRONOME_AGE_FIELD = TileEntityMetronome.class.getDeclaredField("age");
            METRONOME_AGE_FIELD.setAccessible(true);
            METRONOME_OLD_POWERED_STATE_FIELD = TileEntityMetronome.class.getDeclaredField("oldPoweredState");
            METRONOME_OLD_POWERED_STATE_FIELD.setAccessible(true);
            METRONOME_COUNTDOWN_FIELD = TileEntityMetronome.class.getDeclaredField("countDown");
            METRONOME_COUNTDOWN_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static CompoundTag noteTag(int note, int time, int volume, int length) {
        CompoundTag tag = new CompoundTag();
        tag.putByte("n", (byte) note);
        tag.putShort("d", (short) time);
        tag.putByte("v", (byte) volume);
        tag.putByte("l", (byte) length);
        return tag;
    }

    private static TileEntityMusicBox requireMusicBox(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockEntity blockEntity = helper.getLevel().getBlockEntity(absolutePos);
        assertTrue(helper, blockEntity instanceof TileEntityMusicBox,
                "Expected music box block entity at " + relativePos);
        if (blockEntity instanceof TileEntityMusicBox musicBox) {
            return musicBox;
        }
        throw new IllegalStateException("Expected music box block entity at " + relativePos);
    }

    private static TileEntityMetronome requireMetronome(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockEntity blockEntity = helper.getLevel().getBlockEntity(absolutePos);
        assertTrue(helper, blockEntity instanceof TileEntityMetronome,
                "Expected metronome block entity at " + relativePos);
        if (blockEntity instanceof TileEntityMetronome metronome) {
            return metronome;
        }
        throw new IllegalStateException("Expected metronome block entity at " + relativePos);
    }

    private static EntityMusicSpirit requireSingleSpiritNear(GameTestHelper helper, BlockPos relativePos, String message) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(2.5D, 2.5D, 2.5D);
        List<EntityMusicSpirit> spirits = helper.getLevel().getEntitiesOfClass(EntityMusicSpirit.class, searchBox, Entity::isAlive);
        assertTrue(helper, !spirits.isEmpty(), message);
        if (!spirits.isEmpty()) {
            return spirits.getFirst();
        }
        throw new IllegalStateException(message);
    }

    private static ItemEntity requireSingleDroppedItemNear(GameTestHelper helper, BlockPos relativePos, Item item, String message) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(2.5D, 2.5D, 2.5D);
        List<ItemEntity> items = helper.getLevel().getEntitiesOfClass(ItemEntity.class, searchBox, it -> it.getItem().is(item));
        assertTrue(helper, !items.isEmpty(), message);
        if (!items.isEmpty()) {
            return items.getFirst();
        }
        throw new IllegalStateException(message);
    }

    private static int countSpiritsNear(GameTestHelper helper, BlockPos relativePos) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(2.5D, 2.5D, 2.5D);
        return helper.getLevel().getEntitiesOfClass(EntityMusicSpirit.class, searchBox, Entity::isAlive).size();
    }

    private static int countDroppedItemsNear(GameTestHelper helper, BlockPos relativePos, Item item) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(2.5D, 2.5D, 2.5D);
        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, searchBox, it -> it.getItem().is(item)).size();
    }

    private static void placeMusicBox(GameTestHelper helper, BlockPos relativePos, Direction facing) {
        BlockState state = Blocks.MUSIC_BOX.defaultBlockState()
                .setValue(BlockMusicBox.FACING, facing)
                .setValue(BlockMusicBox.POWERED, false)
                .setValue(BlockMusicBox.POWERING, false)
                .setValue(BlockMusicBox.HAS_MUSIC, false)
                .setValue(BlockMusicBox.HAS_INSTRUMENT, false);
        helper.getLevel().setBlockAndUpdate(helper.absolutePos(relativePos), state);
    }

    private static void placeMetronome(GameTestHelper helper, BlockPos relativePos, Direction facing, int bps, boolean powered) {
        BlockState state = Blocks.BLOCK_METRONOME.defaultBlockState()
                .setValue(BlockMetronome.FACING, facing)
                .setValue(BlockMetronome.BPS, bps)
                .setValue(BlockMetronome.POWERED, powered);
        helper.getLevel().setBlockAndUpdate(helper.absolutePos(relativePos), state);
    }

    private static void placePiano(GameTestHelper helper, BlockPos relativePos, Direction facing) {
        BlockState state = Blocks.PIANO.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing);
        helper.getLevel().setBlockAndUpdate(helper.absolutePos(relativePos), state);
    }

    private static UseResult useHeldItemOnBlock(GameTestHelper helper, Player player, ItemStack stack, BlockPos relativePos, Direction face) {
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), face, absolutePos, false);
        stack.useOn(new net.minecraft.world.item.context.UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        return new UseResult(player.getMainHandItem(), helper.getLevel().getBlockState(absolutePos));
    }

    private static InteractionResult useBlockWithItem(GameTestHelper helper, Player player, ItemStack stack, BlockPos relativePos, Direction face) {
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), face, absolutePos, false);
        if (state.getBlock() instanceof BlockMusicBox musicBox) {
            return musicBox.useItemOn(stack, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hit);
        }
        if (state.getBlock() instanceof BlockMetronome metronome) {
            return metronome.useItemOn(stack, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hit);
        }
        if (state.getBlock() instanceof xerca.xercamusic.common.block.BlockInstrument instrumentBlock) {
            return instrumentBlock.useItemOn(stack, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hit);
        }
        assertTrue(helper, false, "Unsupported block for useBlockWithItem: " + state.getBlock());
        return InteractionResult.PASS;
    }

    private static InteractionResult useBlockFace(GameTestHelper helper, Player player, BlockPos relativePos, Direction face) {
        player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), face, absolutePos, false);
        if (state.getBlock() instanceof BlockMusicBox musicBox) {
            return musicBox.useItemOn(ItemStack.EMPTY, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hit);
        }
        if (state.getBlock() instanceof BlockMetronome metronome) {
            return metronome.useItemOn(ItemStack.EMPTY, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hit);
        }
        if (state.getBlock() instanceof xerca.xercamusic.common.block.BlockInstrument instrumentBlock) {
            return instrumentBlock.useItemOn(ItemStack.EMPTY, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hit);
        }
        assertTrue(helper, false, "Unsupported block for useBlockFace: " + state.getBlock());
        return InteractionResult.PASS;
    }

    private static InteractionResult useBlockWithoutItem(GameTestHelper helper, Player player, BlockPos relativePos, Direction face) {
        player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), face, absolutePos, false);
        if (state.getBlock() instanceof BlockMetronome metronome) {
            return metronome.useWithoutItem(state, helper.getLevel(), absolutePos, player, hit);
        }
        if (state.getBlock() instanceof xerca.xercamusic.common.block.BlockInstrument instrumentBlock) {
            return instrumentBlock.useWithoutItem(state, helper.getLevel(), absolutePos, player, hit);
        }
        assertTrue(helper, false, "Unsupported block for useBlockWithoutItem: " + state.getBlock());
        return InteractionResult.PASS;
    }

    /**
     * Mirrors the vanilla server-side resolution order in
     * {@code ServerPlayerGameMode#useItemOn}: try the block's item interaction, fall back to the
     * block's item-less interaction, and only then let the held item place its block. Used to verify
     * that opening an instrument GUI suppresses block placement from a held block instrument.
     */
    private static InteractionResult simulateServerUseItemOn(GameTestHelper helper, Player player, ItemStack stack, BlockPos relativePos, Direction face) {
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), face, absolutePos, false);
        InteractionResult itemResult = state.useItemOn(stack, helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
        if (itemResult.consumesAction()) {
            return itemResult;
        }
        if (itemResult == InteractionResult.TRY_WITH_EMPTY_HAND) {
            InteractionResult blockResult = state.useWithoutItem(helper.getLevel(), player, hit);
            if (blockResult.consumesAction()) {
                return blockResult;
            }
        }
        if (!stack.isEmpty()) {
            return stack.useOn(new net.minecraft.world.item.context.UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        }
        return InteractionResult.PASS;
    }

    private static ItemStack createSheetStack(GameTestHelper helper, int lengthBeats, int bps) {
        UUID id = UUID.randomUUID();
        ArrayList<NoteEvent> notes = new ArrayList<>();
        notes.add(new NoteEvent((byte) 64, (short) 0, (byte) 100, (byte) 1));
        MusicManager.setMusicData(id, 1, notes, null, helper.getLevel().getServer());

        ItemStack stack = new ItemStack(Items.MUSIC_SHEET);
        stack.set(Items.SHEET_ID, id);
        stack.set(Items.SHEET_VERSION, 1);
        stack.set(Items.SHEET_LENGTH, lengthBeats);
        stack.set(Items.SHEET_BPS, (byte) bps);
        stack.set(Items.SHEET_VOLUME, 1.0f);
        stack.set(Items.SHEET_TITLE, "test");
        return stack;
    }

    private static ItemStack createSheetStack(UUID id, int version, int generation, int lengthBeats, int bps, float volume,
                                              @Nullable String title,
                                              @Nullable String author) {
        ItemStack stack = new ItemStack(Items.MUSIC_SHEET);
        stack.set(Items.SHEET_ID, id);
        stack.set(Items.SHEET_VERSION, version);
        stack.set(Items.SHEET_GENERATION, generation);
        stack.set(Items.SHEET_LENGTH, lengthBeats);
        stack.set(Items.SHEET_BPS, (byte) bps);
        stack.set(Items.SHEET_VOLUME, volume);
        if (title != null) {
            stack.set(Items.SHEET_TITLE, title);
        }
        if (author != null) {
            stack.set(Items.SHEET_AUTHOR, author);
        }
        return stack;
    }

    private static Player makeMockSurvivalPlayer(GameTestHelper helper) {
        return helper.makeMockPlayer(GameType.SURVIVAL);
    }

    private static Path exportPath(String filename) {
        return Path.of("music_sheets", filename + ".sheet");
    }

    private static void deleteIfExists(GameTestHelper helper, Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            assertTrue(helper, false, "Failed to delete test export file " + path + ": " + e);
        }
    }

    private static CompoundTag readExportedTag(GameTestHelper helper, Path path) {
        assertTrue(helper, Files.exists(path), "Expected exported music sheet file at " + path);
        try {
            CompoundTag tag = NbtIo.read(path);
            assertTrue(helper, tag != null, "Expected exported music sheet NBT at " + path);
            if (tag != null) {
                return tag;
            }
            throw new IllegalStateException("Expected exported music sheet NBT at " + path);
        } catch (IOException e) {
            assertTrue(helper, false, "Failed to read exported music sheet " + path + ": " + e);
            return new CompoundTag();
        }
    }

    private static UUID requireSheetId(GameTestHelper helper, ItemStack stack, String message) {
        UUID id = stack.get(Items.SHEET_ID);
        assertTrue(helper, id != null, message);
        if (id != null) {
            return id;
        }
        throw new IllegalStateException(message);
    }

    private static MusicManager.MusicData requireMusicData(GameTestHelper helper, UUID id, int version, String message) {
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, helper.getLevel().getServer());
        assertTrue(helper, data != null, message);
        if (data != null) {
            return data;
        }
        throw new IllegalStateException(message);
    }

    private static List<NoteEvent> notesFromTag(CompoundTag tag) {
        ArrayList<NoteEvent> notes = new ArrayList<>();
        NoteEvent.fillArrayFromNBT(notes, tag);
        return notes;
    }

    private static List<VolumeMarker> volumeMarkersFromTag(CompoundTag tag) {
        ArrayList<VolumeMarker> markers = new ArrayList<>();
        VolumeMarker.fillArrayFromNBT(markers, tag);
        return markers;
    }

    private static byte[] copyWrittenBytes(FriendlyByteBuf buf) {
        byte[] bytes = new byte[buf.writerIndex()];
        buf.getBytes(0, bytes);
        return bytes;
    }

    private static ItemStack findImportedSheet(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(Items.MUSIC_SHEET) && stack.get(Items.SHEET_ID) != null) {
                return stack;
            }
        }
        if (player.getMainHandItem().is(Items.MUSIC_SHEET) && player.getMainHandItem().get(Items.SHEET_ID) != null) {
            return player.getMainHandItem();
        }
        if (player.getOffhandItem().is(Items.MUSIC_SHEET) && player.getOffhandItem().get(Items.SHEET_ID) != null) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    private static boolean isMusicBoxPlaying(GameTestHelper helper, TileEntityMusicBox musicBox) {
        try {
            return MUSIC_BOX_IS_PLAYING_FIELD.getBoolean(musicBox);
        } catch (IllegalAccessException e) {
            assertTrue(helper, false, "Failed to read music box isPlaying field: " + e);
            return false;
        }
    }

    private static byte getMusicBoxBps(GameTestHelper helper, TileEntityMusicBox musicBox) {
        try {
            return MUSIC_BOX_BPS_FIELD.getByte(musicBox);
        } catch (IllegalAccessException e) {
            assertTrue(helper, false, "Failed to read music box bps field: " + e);
            return 0;
        }
    }

    private static float getMusicBoxVolume(GameTestHelper helper, TileEntityMusicBox musicBox) {
        try {
            return MUSIC_BOX_VOLUME_FIELD.getFloat(musicBox);
        } catch (IllegalAccessException e) {
            assertTrue(helper, false, "Failed to read music box volume field: " + e);
            return 0.0f;
        }
    }

    private static @Nullable UUID getWarnedMissingSheetId(GameTestHelper helper, TileEntityMusicBox musicBox) {
        try {
            return (UUID) MUSIC_BOX_WARNED_MISSING_SHEET_ID_FIELD.get(musicBox);
        } catch (IllegalAccessException e) {
            assertTrue(helper, false, "Failed to read music box warnedMissingSheetId field: " + e);
            return null;
        }
    }

    private static int getWarnedMissingSheetVersion(GameTestHelper helper, TileEntityMusicBox musicBox) {
        try {
            return MUSIC_BOX_WARNED_MISSING_SHEET_VERSION_FIELD.getInt(musicBox);
        } catch (IllegalAccessException e) {
            assertTrue(helper, false, "Failed to read music box warnedMissingSheetVersion field: " + e);
            return -2;
        }
    }

    private static int getMetronomeAge(GameTestHelper helper, TileEntityMetronome metronome) {
        try {
            return METRONOME_AGE_FIELD.getInt(metronome);
        } catch (IllegalAccessException e) {
            assertTrue(helper, false, "Failed to read metronome age field: " + e);
            return -1;
        }
    }

    private static boolean getMetronomeOldPoweredState(GameTestHelper helper, TileEntityMetronome metronome) {
        try {
            return METRONOME_OLD_POWERED_STATE_FIELD.getBoolean(metronome);
        } catch (IllegalAccessException e) {
            assertTrue(helper, false, "Failed to read metronome oldPoweredState field: " + e);
            return false;
        }
    }

    private static int getMetronomeCountDown(GameTestHelper helper, TileEntityMetronome metronome) {
        try {
            return METRONOME_COUNTDOWN_FIELD.getInt(metronome);
        } catch (IllegalAccessException e) {
            assertTrue(helper, false, "Failed to read metronome countDown field: " + e);
            return -1;
        }
    }

    private static void tickMusicBox(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        TileEntityMusicBox musicBox = requireMusicBox(helper, relativePos);
        TileEntityMusicBox.tick(helper.getLevel(), absolutePos, state, musicBox);
    }

    private static void setMusicBoxPowered(GameTestHelper helper, BlockPos relativePos, boolean powered) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        helper.getLevel().setBlockAndUpdate(absolutePos, state.setValue(BlockMusicBox.POWERED, powered));
    }

    private static void tickMetronome(GameTestHelper helper, BlockPos relativePos) {
        TileEntityMetronome.tick(helper.getLevel(), requireMetronome(helper, relativePos));
    }

    private static void setMetronomePowered(GameTestHelper helper, BlockPos relativePos, boolean powered) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        helper.getLevel().setBlockAndUpdate(absolutePos, state.setValue(BlockMetronome.POWERED, powered));
    }

    private record UseResult(ItemStack handStack, BlockState state) {
    }

    @GameTest
    public void fillArrayFromNbtSortsAndRemovesOverlaps(GameTestHelper helper) {
        CompoundTag sheetTag = new CompoundTag();
        ListTag notesTag = new ListTag();
        notesTag.add(noteTag(64, 5, 100, 4));
        notesTag.add(noteTag(65, 2, 100, 1));
        notesTag.add(noteTag(64, 5, 100, 1));
        notesTag.add(noteTag(10, 3, 100, 1));
        notesTag.add(noteTag(120, 4, 100, 1));
        sheetTag.put(ItemMusicSheet.KEY_NOTES, notesTag);

        ArrayList<NoteEvent> loaded = new ArrayList<>();
        NoteEvent.fillArrayFromNBT(loaded, sheetTag);

        assertTrue(helper, loaded.size() == 2, "Expected only two valid unique notes after load sanitization");
        assertTrue(helper, loaded.get(0).time == 2 && loaded.get(0).note == 65, "Expected earliest note to be first after sorting");
        assertTrue(helper, loaded.get(1).time == 5 && loaded.get(1).note == 64, "Expected duplicate note/time to be removed");
        helper.succeed();
    }

    @GameTest
    public void finishedTempBufferIsConsumedOnce(GameTestHelper helper) {
        UUID id = UUID.randomUUID();
        ArrayList<NoteEvent> part0 = new ArrayList<>();
        ArrayList<NoteEvent> part1 = new ArrayList<>();
        part0.add(new NoteEvent((byte) 64, (short) 1, (byte) 80, (byte) 1));
        part1.add(new NoteEvent((byte) 66, (short) 2, (byte) 90, (byte) 1));

        boolean doneAfterFirst = MusicManager.addNotesPart(new SendNotesPartToServerPacket(id, 2, 0, part0));
        boolean doneAfterSecond = MusicManager.addNotesPart(new SendNotesPartToServerPacket(id, 2, 1, part1));

        assertTrue(helper, !doneAfterFirst, "Expected first notes part to be incomplete");
        assertTrue(helper, doneAfterSecond, "Expected second notes part to complete the buffer");

        List<NoteEvent> joined = MusicManager.getFinishedNotesFromBuffer(id);
        assertTrue(helper, joined.size() == 2, "Expected joined notes from finished temp buffer");

        List<NoteEvent> secondRead = MusicManager.getFinishedNotesFromBuffer(id);
        assertTrue(helper, secondRead.isEmpty(), "Expected finished temp buffer to be consumed and removed");
        helper.succeed();
    }

    @GameTest
    public void musicBoxSheetValuesAreSanitizedOnInsert(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        placeMusicBox(helper, boxPos, Direction.NORTH);
        TileEntityMusicBox musicBox = requireMusicBox(helper, boxPos);

        ItemStack sheet = createSheetStack(helper, 8, 8);
        sheet.set(Items.SHEET_BPS, (byte) 0);
        sheet.set(Items.SHEET_VOLUME, 2.5f);
        musicBox.setSheetStack(sheet, false);

        assertTrue(helper, getMusicBoxBps(helper, musicBox) == 1, "Expected music box bps to clamp to minimum of 1");
        assertTrue(helper, getMusicBoxVolume(helper, musicBox) == 1.0f, "Expected music box volume to clamp to maximum of 1.0");
        helper.succeed();
    }

    @GameTest
    public void musicBoxUnknownSheetWarningStateIsDeduplicatedAndReset(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        placeMusicBox(helper, boxPos, Direction.NORTH);
        TileEntityMusicBox musicBox = requireMusicBox(helper, boxPos);

        UUID missingId = UUID.randomUUID();
        int missingVersion = 7;
        musicBox.setSheetStack(createSheetStack(missingId, missingVersion, 0, 8, 8, 1.0f, "missing", null), false);

        tickMusicBox(helper, boxPos);
        assertTrue(helper, missingId.equals(getWarnedMissingSheetId(helper, musicBox)),
                "Expected music box to remember the missing sheet id after first failed lookup");
        assertTrue(helper, getWarnedMissingSheetVersion(helper, musicBox) == missingVersion,
                "Expected music box to remember the missing sheet version after first failed lookup");

        tickMusicBox(helper, boxPos);
        assertTrue(helper, missingId.equals(getWarnedMissingSheetId(helper, musicBox)),
                "Expected repeated failed lookups for the same sheet to keep the same warning state");
        assertTrue(helper, getWarnedMissingSheetVersion(helper, musicBox) == missingVersion,
                "Expected repeated failed lookups for the same sheet to keep the same warning version");

        musicBox.removeSheetStack();
        assertTrue(helper, getWarnedMissingSheetId(helper, musicBox) == null,
                "Expected removing the sheet to clear the missing-sheet warning state");
        assertTrue(helper, getWarnedMissingSheetVersion(helper, musicBox) == -1,
                "Expected removing the sheet to reset the missing-sheet warning version");
        helper.succeed();
    }

    @GameTest
    public void musicSpiritSpawnDataHandlesInvalidBlockInstrument(GameTestHelper helper) {
        EntityMusicSpirit spirit = new EntityMusicSpirit(helper.getLevel());

        try {
            spirit.buildFromSpawnData(0, -1, 0, -1, -1);
        } catch (Exception e) {
            assertTrue(helper, false, "Expected invalid spawn data to be ignored without crash, but got: " + e);
            return;
        }

        helper.succeed();
    }

    @GameTest
    public void singleNoteClientDecodeKeepsPlayerIdWithoutLevelLookup(GameTestHelper helper) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(64);
        buf.writeInt(0);
        buf.writeInt(12345);
        buf.writeBoolean(false);
        buf.writeFloat(0.8f);

        SingleNoteClientPacket packet = SingleNoteClientPacket.decode(buf);
        assertTrue(helper, packet.playerId() == 12345, "Expected player id to be preserved in decoded packet");
        helper.succeed();
    }

    @GameTest
    public void tripleNoteClientDecodeKeepsEntityIdWithoutLevelLookup(GameTestHelper helper) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(60);
        buf.writeInt(64);
        buf.writeInt(67);
        buf.writeInt(0);
        buf.writeInt(54321);

        TripleNoteClientPacket packet = TripleNoteClientPacket.decode(buf);
        assertTrue(helper, packet.entityId() == 54321, "Expected entity id to be preserved in decoded packet");
        helper.succeed();
    }

    @GameTest
    public void clipboardDecodeAcceptsCurrentCopyFormat(GameTestHelper helper) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeByte(MusicClipboard.COPY_BEGIN_BYTE);
        buf.writeInt(7);
        buf.writeInt(2);

        NoteEvent glissando = new NoteEvent((byte) 64, (short) 0, (byte) 96, (byte) 4);
        glissando.setGlissandoWaypoints(new byte[]{2, 5}, new byte[]{25, 75});
        glissando.encodeToBuffer(buf);
        new NoteEvent((byte) 67, (short) 5, (byte) 110, (byte) 2).encodeToBuffer(buf);

        buf.writeInt(1);
        new VolumeMarker((short) 0, (short) 8, (byte) 32, (byte) 96, (byte) 60, (byte) 72).encodeToBuffer(buf);

        MusicClipboard.ParsedMusic parsed = MusicClipboard.decode(Base64.getEncoder().encodeToString(copyWrittenBytes(buf)));
        assertTrue(helper, parsed != null, "Expected current clipboard payload to parse");
        assert parsed != null;
        assertTrue(helper, parsed.length() == 8, "Expected current clipboard length to be preserved");
        assertTrue(helper, parsed.notes().size() == 2, "Expected current clipboard notes to be preserved");
        assertTrue(helper, parsed.notes().getFirst().hasGlissando(), "Expected current clipboard glissando data to be preserved");
        assertTrue(helper, parsed.volumeMarkers().size() == 1, "Expected current clipboard volume markers to be preserved");
        helper.succeed();
    }

    @GameTest
    public void clipboardDecodeAcceptsPreGlissandoCopyFormat(GameTestHelper helper) {
        MusicClipboard.ParsedMusic parsed = MusicClipboard.decode(PRE_GLISSANDO_CLIPBOARD_SAMPLE);
        assertTrue(helper, parsed != null, "Expected pre-glissando clipboard sample to parse");
        assert parsed != null;
        assertTrue(helper, parsed.length() == 64, "Expected pre-glissando clipboard length to be preserved");
        assertTrue(helper, parsed.notes().size() == 4, "Expected pre-glissando clipboard notes to be preserved");
        assertTrue(helper, parsed.volumeMarkers().isEmpty(), "Expected pre-glissando clipboard sample to have no volume markers");
        assertTrue(helper, MusicClipboard.decode(PRE_GLISSANDO_CLIPBOARD_SAMPLE + "\n") != null,
                "Expected copied sample text with trailing newline to parse");
        helper.succeed();
    }

    @GameTest
    public void clipboardDecodeAcceptsVeryOldMusicFormat(GameTestHelper helper) {
        MusicClipboard.ParsedMusic parsed = MusicClipboard.decode(VERY_OLD_CLIPBOARD_SAMPLE);
        assertTrue(helper, parsed != null, "Expected very old clipboard sample to parse");
        assert parsed != null;
        assertTrue(helper, parsed.length() == 17, "Expected very old clipboard length to be inferred from notes");
        assertTrue(helper, parsed.notes().size() == 8, "Expected very old clipboard notes to be preserved");
        assertTrue(helper, parsed.volumeMarkers().isEmpty(), "Expected very old clipboard sample to have no volume markers");
        helper.succeed();
    }

    @GameTest
    public void clipboardDecodeRejectsNonsenseWithoutThrowing(GameTestHelper helper) {
        assertTrue(helper, MusicClipboard.decode("this is not base64") == null,
                "Expected non-base64 clipboard data to be ignored");
        assertTrue(helper, MusicClipboard.decodeBytes(new byte[0]) == null,
                "Expected empty clipboard data to be ignored");
        assertTrue(helper, MusicClipboard.decodeBytes(new byte[]{MusicClipboard.COPY_BEGIN_BYTE}) == null,
                "Expected truncated current clipboard data to be ignored");

        FriendlyByteBuf truncated = new FriendlyByteBuf(Unpooled.buffer());
        truncated.writeByte(MusicClipboard.COPY_BEGIN_BYTE);
        truncated.writeInt(3);
        truncated.writeInt(1);
        truncated.writeByte(64);
        assertTrue(helper, MusicClipboard.decodeBytes(copyWrittenBytes(truncated)) == null,
                "Expected truncated note data to be ignored");
        helper.succeed();
    }

    @GameTest
    public void signedSheetExportAndImportPreserveIdentityAndMarkers(GameTestHelper helper) {
        String exportName = "signed_sheet_roundtrip";
        Path path = exportPath(exportName);
        deleteIfExists(helper, path);

        UUID id = UUID.randomUUID();
        int version = 4;
        ArrayList<NoteEvent> notes = new ArrayList<>();
        NoteEvent lead = new NoteEvent((byte) 64, (short) 0, (byte) 96, (byte) 4);
        lead.setGlissandoWaypoints(new byte[]{2, 5}, new byte[]{25, 75});
        notes.add(lead);
        notes.add(new NoteEvent((byte) 67, (short) 5, (byte) 110, (byte) 2));
        ArrayList<VolumeMarker> markers = new ArrayList<>();
        markers.add(new VolumeMarker((short) 0, (short) 6, (byte) 40, (byte) 100, (byte) 60, (byte) 72));
        MusicManagerClient.setMusicData(id, version, notes, markers);

        Player exporter = makeMockSurvivalPlayer(helper);
        exporter.setItemSlot(EquipmentSlot.MAINHAND,
                createSheetStack(id, version, 1, 12, 8, 0.75f, "signed_export", "tester"));

        assertTrue(helper, CommandExport.doExport(exporter, exportName),
                "Expected signed music sheet export to find a sheet in hand");

        CompoundTag exportedTag = readExportedTag(helper, path);
        assertTrue(helper, exportedTag.contains(ItemMusicSheet.KEY_ID), "Expected signed export to store the sheet id");
        assertTrue(helper, id.equals(exportedTag.read(ItemMusicSheet.KEY_ID, net.minecraft.core.UUIDUtil.CODEC).orElse(null)),
                "Expected signed export to preserve the original sheet id");
        assertTrue(helper, exportedTag.getIntOr(ItemMusicSheet.KEY_VERSION, -1) == version,
                "Expected signed export to preserve the original sheet version");
        assertTrue(helper, "signed_export".equals(exportedTag.getStringOr(ItemMusicSheet.KEY_TITLE, "")),
                "Expected signed export to include the title");
        assertTrue(helper, "tester".equals(exportedTag.getStringOr(ItemMusicSheet.KEY_AUTHOR, "")),
                "Expected signed export to include the author");

        List<NoteEvent> exportedNotes = notesFromTag(exportedTag);
        List<VolumeMarker> exportedMarkers = volumeMarkersFromTag(exportedTag);
        assertTrue(helper, exportedNotes.size() == notes.size(),
                "Expected signed export to write all sheet notes");
        assertTrue(helper, exportedMarkers.size() == markers.size(),
                "Expected signed export to write all volume markers");

        Player importer = makeMockSurvivalPlayer(helper);
        importer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.MUSIC_SHEET));
        CommandImport.doImport(exportedTag, exportedNotes, id, importer);

        ItemStack importedSheet = findImportedSheet(importer);
        assertTrue(helper, !importedSheet.isEmpty(), "Expected signed import to produce a populated music sheet");
        assertTrue(helper, id.equals(importedSheet.get(Items.SHEET_ID)),
                "Expected signed import to keep the exported sheet id");
        assertTrue(helper, importedSheet.getOrDefault(Items.SHEET_VERSION, -1) == version,
                "Expected signed import to keep the exported sheet version");
        assertTrue(helper, importedSheet.getOrDefault(Items.SHEET_GENERATION, 0) == 2,
                "Expected signed import to advance generation from original to copy");
        assertTrue(helper, "signed_export".equals(importedSheet.get(Items.SHEET_TITLE)),
                "Expected signed import to keep the title");
        assertTrue(helper, "tester".equals(importedSheet.get(Items.SHEET_AUTHOR)),
                "Expected signed import to keep the author");

        MusicManager.MusicData importedData = requireMusicData(helper, id, version,
                "Expected signed import to register music data on the server");
        assertTrue(helper, importedData.notes().size() == notes.size(),
                "Expected signed import to restore the exported note count");
        assertTrue(helper, importedData.volumeMarkers() != null && importedData.volumeMarkers().size() == markers.size(),
                "Expected signed import to restore exported volume markers");

        deleteIfExists(helper, path);
        helper.succeed();
    }

    @GameTest
    public void unsignedMultipartImportFromExportGetsFreshIdentityAndConsumesBuffer(GameTestHelper helper) {
        String exportName = "unsigned_sheet_roundtrip";
        Path path = exportPath(exportName);
        deleteIfExists(helper, path);

        UUID originalId = UUID.randomUUID();
        int originalVersion = 9;
        ArrayList<NoteEvent> notes = new ArrayList<>();
        notes.add(new NoteEvent((byte) 60, (short) 0, (byte) 90, (byte) 1));
        notes.add(new NoteEvent((byte) 64, (short) 3, (byte) 100, (byte) 2));
        notes.add(new NoteEvent((byte) 67, (short) 7, (byte) 110, (byte) 1));
        ArrayList<VolumeMarker> markers = new ArrayList<>();
        markers.add(new VolumeMarker((short) 0, (short) 8, (byte) 32, (byte) 96, (byte) 58, (byte) 69));
        MusicManagerClient.setMusicData(originalId, originalVersion, notes, markers);

        Player exporter = makeMockSurvivalPlayer(helper);
        exporter.setItemSlot(EquipmentSlot.MAINHAND,
                createSheetStack(originalId, originalVersion, 0, 16, 12, 1.0f, null, null));

        assertTrue(helper, CommandExport.doExport(exporter, exportName),
                "Expected unsigned music sheet export to find a sheet in hand");

        CompoundTag exportedTag = readExportedTag(helper, path);
        assertTrue(helper, !exportedTag.contains(ItemMusicSheet.KEY_TITLE),
                "Expected unsigned export to omit the title");
        assertTrue(helper, !exportedTag.contains(ItemMusicSheet.KEY_AUTHOR),
                "Expected unsigned export to omit the author");

        List<NoteEvent> exportedNotes = notesFromTag(exportedTag);
        List<VolumeMarker> exportedMarkers = volumeMarkersFromTag(exportedTag);
        assertTrue(helper, exportedNotes.size() == notes.size(),
                "Expected unsigned export to write the sheet notes");
        assertTrue(helper, exportedMarkers.size() == markers.size(),
                "Expected unsigned export to write the sheet volume markers");

        assertTrue(helper, !MusicManager.addNotesPart(new SendNotesPartToServerPacket(originalId, 2, 0,
                        new ArrayList<>(exportedNotes.subList(0, 1)))),
                "Expected the first multipart note payload to leave the import buffer incomplete");
        assertTrue(helper, MusicManager.addNotesPart(new SendNotesPartToServerPacket(originalId, 2, 1,
                        new ArrayList<>(exportedNotes.subList(1, exportedNotes.size())))),
                "Expected the second multipart note payload to complete the import buffer");

        Player importer = makeMockSurvivalPlayer(helper);
        importer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.MUSIC_SHEET));
        CommandImport.doImport(exportedTag, null, originalId, importer);

        ItemStack importedSheet = findImportedSheet(importer);
        assertTrue(helper, !importedSheet.isEmpty(), "Expected unsigned import to produce a populated music sheet");
        UUID importedId = requireSheetId(helper, importedSheet,
                "Expected unsigned import to assign a new sheet id");
        assertTrue(helper, !originalId.equals(importedId),
                "Expected unsigned import to regenerate the sheet id instead of reusing the exported one");
        assertTrue(helper, importedSheet.getOrDefault(Items.SHEET_VERSION, -1) == 1,
                "Expected unsigned import to reset the version to 1");
        assertTrue(helper, importedSheet.getOrDefault(Items.SHEET_GENERATION, -1) == 0,
                "Expected unsigned import to stay unsigned after import");
        assertTrue(helper, importedSheet.get(Items.SHEET_TITLE) == null,
                "Expected unsigned import to keep the sheet title empty");
        assertTrue(helper, importedSheet.get(Items.SHEET_AUTHOR) == null,
                "Expected unsigned import to keep the sheet author empty");

        MusicManager.MusicData importedData = requireMusicData(helper, importedId, 1,
                "Expected unsigned import to store music data under the new id");
        assertTrue(helper, importedData.notes().size() == notes.size(),
                "Expected unsigned import to restore the multipart note payload");
        assertTrue(helper, importedData.volumeMarkers() != null && importedData.volumeMarkers().size() == markers.size(),
                "Expected unsigned import to restore exported volume markers");
        assertTrue(helper, MusicManager.getFinishedNotesFromBuffer(originalId).isEmpty(),
                "Expected unsigned import to consume and clear the multipart note buffer");

        deleteIfExists(helper, path);
        helper.succeed();
    }

    @GameTest
    public void musicBoxInsertsAndEjectsMusicSheet(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMusicBox(helper, boxPos, Direction.NORTH);

        ItemStack sheet = createSheetStack(helper, 8, 8);
        UseResult insert = useHeldItemOnBlock(helper, player, sheet, boxPos, Direction.UP);
        assertTrue(helper, insert.handStack().isEmpty(), "Expected music sheet stack to be consumed on insert");
        assertTrue(helper, insert.state().getValue(BlockMusicBox.HAS_MUSIC), "Expected music box to report inserted music sheet");
        assertTrue(helper, !requireMusicBox(helper, boxPos).getSheetStack().isEmpty(), "Expected tile entity to store inserted music sheet");

        InteractionResult ejectResult = useBlockFace(helper, player, boxPos, Direction.UP);
        assertTrue(helper, ejectResult.consumesAction(), "Expected top-face ejection to consume interaction");
        BlockState ejectedState = helper.getLevel().getBlockState(helper.absolutePos(boxPos));
        assertTrue(helper, !ejectedState.getValue(BlockMusicBox.HAS_MUSIC), "Expected music sheet slot to be empty after ejection");
        assertTrue(helper, requireMusicBox(helper, boxPos).getSheetStack().isEmpty(), "Expected tile entity music sheet to be cleared after ejection");
        assertTrue(helper, requireSingleDroppedItemNear(helper, boxPos, Items.MUSIC_SHEET, "Expected ejected music sheet item").getItem().get(Items.SHEET_ID) != null,
                "Expected dropped music sheet to keep its music data components");
        helper.succeed();
    }

    @GameTest
    public void musicBoxInsertsAndEjectsInstrument(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMusicBox(helper, boxPos, Direction.NORTH);

        ItemStack guitar = new ItemStack(Items.GUITAR);
        UseResult insert = useHeldItemOnBlock(helper, player, guitar, boxPos, Direction.SOUTH);
        assertTrue(helper, insert.handStack().isEmpty(), "Expected instrument stack to be consumed on insert");
        assertTrue(helper, insert.state().getValue(BlockMusicBox.HAS_INSTRUMENT), "Expected music box to report inserted instrument");
        assertTrue(helper, requireMusicBox(helper, boxPos).getInstrument() == Items.GUITAR, "Expected tile entity to store inserted instrument");

        InteractionResult ejectResult = useBlockFace(helper, player, boxPos, Direction.SOUTH);
        assertTrue(helper, ejectResult.consumesAction(), "Expected back-face instrument ejection to consume interaction");
        BlockState ejectedState = helper.getLevel().getBlockState(helper.absolutePos(boxPos));
        assertTrue(helper, !ejectedState.getValue(BlockMusicBox.HAS_INSTRUMENT), "Expected instrument slot to be empty after ejection");
        assertTrue(helper, requireMusicBox(helper, boxPos).getInstrument() == null, "Expected tile entity instrument to be cleared after ejection");
        requireSingleDroppedItemNear(helper, boxPos, Items.GUITAR, "Expected ejected instrument item");
        helper.succeed();
    }

    @GameTest
    public void musicBoxStartsAndStopsOnRedstonePulses(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        placeMusicBox(helper, boxPos, Direction.NORTH);
        TileEntityMusicBox musicBox = requireMusicBox(helper, boxPos);
        musicBox.setInstrument(Items.GUITAR);
        musicBox.setSheetStack(createSheetStack(helper, 20, 1), false);

        BlockPos absolutePos = helper.absolutePos(boxPos);
        helper.getLevel().setBlockAndUpdate(absolutePos, helper.getLevel().getBlockState(absolutePos)
                .setValue(BlockMusicBox.HAS_INSTRUMENT, true)
                .setValue(BlockMusicBox.HAS_MUSIC, true));

        tickMusicBox(helper, boxPos);
        setMusicBoxPowered(helper, boxPos, true);
        tickMusicBox(helper, boxPos);
        assertTrue(helper, isMusicBoxPlaying(helper, musicBox), "Expected music box to start playing on first redstone pulse");
        assertTrue(helper, !helper.getLevel().getBlockState(absolutePos).getValue(BlockMusicBox.POWERING),
                "Expected music box to not emit redstone while only playing");

        setMusicBoxPowered(helper, boxPos, false);
        tickMusicBox(helper, boxPos);
        setMusicBoxPowered(helper, boxPos, true);
        tickMusicBox(helper, boxPos);
        assertTrue(helper, !isMusicBoxPlaying(helper, musicBox), "Expected second redstone pulse to stop playback");
        assertTrue(helper, !helper.getLevel().getBlockState(absolutePos).getValue(BlockMusicBox.POWERING),
                "Expected stopped music box to not emit redstone");
        helper.succeed();
    }

    @GameTest
    public void musicBoxEndsPlaybackAndEmitsTimedRedstone(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        placeMusicBox(helper, boxPos, Direction.NORTH);
        TileEntityMusicBox musicBox = requireMusicBox(helper, boxPos);
        musicBox.setInstrument(Items.GUITAR);
        musicBox.setSheetStack(createSheetStack(helper, 3, 20), false);

        BlockPos absolutePos = helper.absolutePos(boxPos);
        helper.getLevel().setBlockAndUpdate(absolutePos, helper.getLevel().getBlockState(absolutePos)
                .setValue(BlockMusicBox.HAS_INSTRUMENT, true)
                .setValue(BlockMusicBox.HAS_MUSIC, true));

        tickMusicBox(helper, boxPos);
        setMusicBoxPowered(helper, boxPos, true);
        tickMusicBox(helper, boxPos);
        setMusicBoxPowered(helper, boxPos, false);

        tickMusicBox(helper, boxPos);
        tickMusicBox(helper, boxPos);

        BlockState endedState = helper.getLevel().getBlockState(absolutePos);
        assertTrue(helper, endedState.getValue(BlockMusicBox.POWERING), "Expected music box to emit redstone when playback ends");
        assertTrue(helper, !isMusicBoxPlaying(helper, musicBox), "Expected music box playback to end");
        assertTrue(helper, endedState.getSignal(helper.getLevel(), absolutePos, Direction.WEST) == 15,
                "Expected full redstone output on the music box output side");
        assertTrue(helper, endedState.getSignal(helper.getLevel(), absolutePos, Direction.EAST) == 0,
                "Expected no redstone output on non-output sides");

        for (int i = 0; i < 11; i++) {
            tickMusicBox(helper, boxPos);
        }

        BlockState finalState = helper.getLevel().getBlockState(absolutePos);
        assertTrue(helper, !finalState.getValue(BlockMusicBox.POWERING), "Expected timed redstone output to clear after 10 ticks");
        assertTrue(helper, finalState.getSignal(helper.getLevel(), absolutePos, Direction.WEST) == 0,
                "Expected redstone output to drop to zero after the pulse window");
        helper.succeed();
    }

    @GameTest
    public void musicBoxDoesNotStartWithoutSheet(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        placeMusicBox(helper, boxPos, Direction.NORTH);
        TileEntityMusicBox musicBox = requireMusicBox(helper, boxPos);
        musicBox.setInstrument(Items.GUITAR);

        BlockPos absolutePos = helper.absolutePos(boxPos);
        helper.getLevel().setBlockAndUpdate(absolutePos, helper.getLevel().getBlockState(absolutePos)
                .setValue(BlockMusicBox.HAS_INSTRUMENT, true)
                .setValue(BlockMusicBox.HAS_MUSIC, false));

        setMusicBoxPowered(helper, boxPos, true);
        tickMusicBox(helper, boxPos);
        tickMusicBox(helper, boxPos);

        BlockState state = helper.getLevel().getBlockState(absolutePos);
        assertTrue(helper, !isMusicBoxPlaying(helper, musicBox), "Expected music box to stay stopped when no sheet is inserted");
        assertTrue(helper, !state.getValue(BlockMusicBox.POWERING), "Expected no redstone output when no sheet is inserted");
        helper.succeed();
    }

    @GameTest
    public void musicBoxDoesNotStartWithoutInstrument(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        placeMusicBox(helper, boxPos, Direction.NORTH);
        TileEntityMusicBox musicBox = requireMusicBox(helper, boxPos);
        musicBox.setSheetStack(createSheetStack(helper, 8, 8), false);

        BlockPos absolutePos = helper.absolutePos(boxPos);
        helper.getLevel().setBlockAndUpdate(absolutePos, helper.getLevel().getBlockState(absolutePos)
                .setValue(BlockMusicBox.HAS_INSTRUMENT, false)
                .setValue(BlockMusicBox.HAS_MUSIC, true));

        setMusicBoxPowered(helper, boxPos, true);
        tickMusicBox(helper, boxPos);
        tickMusicBox(helper, boxPos);

        BlockState state = helper.getLevel().getBlockState(absolutePos);
        assertTrue(helper, !isMusicBoxPlaying(helper, musicBox), "Expected music box to stay stopped when no instrument is inserted");
        assertTrue(helper, !state.getValue(BlockMusicBox.POWERING), "Expected no redstone output when no instrument is inserted");
        helper.succeed();
    }

    @GameTest
    public void musicBoxEmptySlotsDoNotEjectAnything(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMusicBox(helper, boxPos, Direction.NORTH);

        InteractionResult topResult = useBlockFace(helper, player, boxPos, Direction.UP);
        InteractionResult backResult = useBlockFace(helper, player, boxPos, Direction.SOUTH);

        BlockState state = helper.getLevel().getBlockState(helper.absolutePos(boxPos));
        assertTrue(helper, !topResult.consumesAction(), "Expected top interaction on empty slot to be ignored");
        assertTrue(helper, !backResult.consumesAction(), "Expected back interaction on empty slot to be ignored");
        assertTrue(helper, !state.getValue(BlockMusicBox.HAS_MUSIC), "Expected top interaction on empty music slot to leave box unchanged");
        assertTrue(helper, !state.getValue(BlockMusicBox.HAS_INSTRUMENT), "Expected back interaction on empty instrument slot to leave box unchanged");
        assertTrue(helper, countDroppedItemsNear(helper, boxPos, Items.MUSIC_SHEET) == 0, "Expected no sheet drops from empty music slot");
        assertTrue(helper, countDroppedItemsNear(helper, boxPos, Items.GUITAR) == 0, "Expected no instrument drops from empty instrument slot");
        helper.succeed();
    }

    @GameTest
    public void metronomeCyclesBpsAndCopiesTempoFromSheet(GameTestHelper helper) {
        BlockPos metronomePos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMetronome(helper, metronomePos, Direction.NORTH, 6, false);

        InteractionResult cycleResult = useBlockWithoutItem(helper, player, metronomePos, Direction.UP);
        BlockState cycled = helper.getLevel().getBlockState(helper.absolutePos(metronomePos));
        assertTrue(helper, cycleResult.consumesAction(), "Expected metronome to consume empty-hand use");
        assertTrue(helper, cycled.getValue(BlockMetronome.BPS) == 7, "Expected metronome to cycle tempo when used without a sheet");

        ItemStack sheet = createSheetStack(helper, 8, 12);
        InteractionResult copyResult = useBlockWithItem(helper, player, sheet, metronomePos, Direction.UP);
        BlockState copied = helper.getLevel().getBlockState(helper.absolutePos(metronomePos));
        assertTrue(helper, copyResult.consumesAction(), "Expected metronome to consume sheet tempo copy use");
        assertTrue(helper, copied.getValue(BlockMetronome.BPS) == 12, "Expected metronome to copy tempo from held music sheet");
        helper.succeed();
    }

    @GameTest
    public void metronomePoweredTicksAdvanceCountdownAndResetOnRepower(GameTestHelper helper) {
        BlockPos metronomePos = new BlockPos(1, 2, 1);
        placeMetronome(helper, metronomePos, Direction.NORTH, 20, false);
        TileEntityMetronome metronome = requireMetronome(helper, metronomePos);

        assertTrue(helper, getMetronomeAge(helper, metronome) == 0, "Expected new metronome age to start at zero");
        assertTrue(helper, getMetronomeCountDown(helper, metronome) == 0, "Expected new metronome countdown to start at zero");
        assertTrue(helper, !getMetronomeOldPoweredState(helper, metronome), "Expected new metronome to start unpowered");

        setMetronomePowered(helper, metronomePos, true);
        tickMetronome(helper, metronomePos);
        assertTrue(helper, getMetronomeAge(helper, metronome) == 1, "Expected first powered tick to advance metronome age");
        assertTrue(helper, getMetronomeCountDown(helper, metronome) == 1, "Expected first powered tick to start countdown");
        assertTrue(helper, getMetronomeOldPoweredState(helper, metronome), "Expected powered tick to latch prior power state");

        tickMetronome(helper, metronomePos);
        assertTrue(helper, getMetronomeAge(helper, metronome) == 2, "Expected second powered tick to keep advancing age");
        assertTrue(helper, getMetronomeCountDown(helper, metronome) == 1, "Expected countdown to wait for the next pause interval at 20 bps");

        tickMetronome(helper, metronomePos);
        assertTrue(helper, getMetronomeAge(helper, metronome) == 3, "Expected third powered tick to keep advancing age");
        assertTrue(helper, getMetronomeCountDown(helper, metronome) == 2, "Expected countdown to increment again every two ticks at 20 bps");

        setMetronomePowered(helper, metronomePos, false);
        tickMetronome(helper, metronomePos);
        assertTrue(helper, !getMetronomeOldPoweredState(helper, metronome), "Expected unpowered tick to clear metronome power latch");

        setMetronomePowered(helper, metronomePos, true);
        tickMetronome(helper, metronomePos);
        assertTrue(helper, getMetronomeAge(helper, metronome) == 1, "Expected repowering to reset metronome age before ticking");
        assertTrue(helper, getMetronomeCountDown(helper, metronome) == 1, "Expected repowering to reset countdown before the next beat");
        helper.succeed();
    }

    @GameTest
    public void metronomeSheetWithoutTempoFallsBackToCycle(GameTestHelper helper) {
        BlockPos metronomePos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMetronome(helper, metronomePos, Direction.NORTH, 9, false);

        ItemStack sheet = new ItemStack(Items.MUSIC_SHEET);
        sheet.set(Items.SHEET_TITLE, "missing_bps");
        InteractionResult itemResult = useBlockWithItem(helper, player, sheet, metronomePos, Direction.UP);
        assertTrue(helper, itemResult == InteractionResult.TRY_WITH_EMPTY_HAND,
                "Expected sheet without tempo to continue to the empty-hand interaction");
        InteractionResult fallbackResult = simulateServerUseItemOn(helper, player, sheet, metronomePos, Direction.UP);
        BlockState updated = helper.getLevel().getBlockState(helper.absolutePos(metronomePos));
        assertTrue(helper, fallbackResult.consumesAction(), "Expected fallback block interaction to cycle the metronome");
        assertTrue(helper, updated.getValue(BlockMetronome.BPS) == 10, "Expected sheet without tempo tag to fall back to normal metronome cycling");
        helper.succeed();
    }

    @GameTest
    public void metronomeUseWithHeldItemCyclesTempoThroughVanillaDispatch(GameTestHelper helper) {
        BlockPos metronomePos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMetronome(helper, metronomePos, Direction.NORTH, 9, false);

        ItemStack heldItem = new ItemStack(net.minecraft.world.item.Items.STICK);
        InteractionResult itemResult = useBlockWithItem(helper, player, heldItem, metronomePos, Direction.UP);
        assertTrue(helper, itemResult == InteractionResult.TRY_WITH_EMPTY_HAND,
                "Expected held non-sheet items to continue to metronome tempo adjustment");

        InteractionResult result = simulateServerUseItemOn(helper, player, heldItem, metronomePos, Direction.UP);
        BlockState updated = helper.getLevel().getBlockState(helper.absolutePos(metronomePos));
        assertTrue(helper, result.consumesAction(), "Expected metronome tempo adjustment to consume the interaction");
        assertTrue(helper, updated.getValue(BlockMetronome.BPS) == 10,
                "Expected right-clicking a metronome with a held item to increment its tempo");
        helper.succeed();
    }

    @GameTest
    public void pianoUseWithSheetStartsAndStopsBlockInstrumentPlayback(GameTestHelper helper) {
        BlockPos pianoPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placePiano(helper, pianoPos, Direction.NORTH);

        BlockPos absolutePos = helper.absolutePos(pianoPos);
        player.snapTo(Vec3.atCenterOf(absolutePos).add(1.0D, 0.0D, 0.0D));
        ItemStack sheet = createSheetStack(helper, 8, 8);
        InteractionResult firstUse = useBlockWithItem(helper, player, sheet, pianoPos, Direction.UP);
        assertTrue(helper, firstUse.consumesAction(), "Expected piano use with sheet to consume interaction");
        EntityMusicSpirit spirit = requireSingleSpiritNear(helper, pianoPos, "Expected piano use with sheet to spawn music spirit");

        InteractionResult secondUse = useBlockWithItem(helper, player, player.getMainHandItem(), pianoPos, Direction.UP);
        assertTrue(helper, secondUse.consumesAction(), "Expected second piano use with sheet to consume interaction");
        spirit.tick();

        AABB searchBox = new AABB(absolutePos).inflate(2.5D, 2.5D, 2.5D);
        List<EntityMusicSpirit> spirits = helper.getLevel().getEntitiesOfClass(EntityMusicSpirit.class, searchBox, Entity::isAlive);
        assertTrue(helper, spirits.isEmpty(), "Expected second piano use with sheet to stop existing playback spirit");
        helper.succeed();
    }

    @GameTest
    public void pianoUseWithoutSheetDoesNotStartPlayback(GameTestHelper helper) {
        BlockPos pianoPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placePiano(helper, pianoPos, Direction.NORTH);

        BlockPos absolutePos = helper.absolutePos(pianoPos);
        player.snapTo(Vec3.atCenterOf(absolutePos).add(1.0D, 0.0D, 0.0D));
        player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        InteractionResult result = useBlockWithoutItem(helper, player, pianoPos, Direction.UP);
        assertTrue(helper, result.consumesAction(),
                "Expected empty-hand piano use to consume the interaction on the server so a held block item cannot also place a block");
        assertTrue(helper, countSpiritsNear(helper, pianoPos) == 0, "Expected piano use without a sheet to not start playback");
        helper.succeed();
    }

    @GameTest
    public void pianoUseWithBlockInstrumentOpensGuiWithoutPlacingBlock(GameTestHelper helper) {
        BlockPos pianoPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placePiano(helper, pianoPos, Direction.NORTH);

        BlockPos absolutePos = helper.absolutePos(pianoPos);
        player.snapTo(Vec3.atCenterOf(absolutePos).add(1.0D, 0.0D, 0.0D));

        ItemStack blockInstrument = new ItemStack(Items.PIANO);
        int initialCount = blockInstrument.getCount();
        InteractionResult itemResult = useBlockWithItem(helper, player, blockInstrument, pianoPos, Direction.UP);
        assertTrue(helper, itemResult == InteractionResult.TRY_WITH_EMPTY_HAND,
                "Expected held non-sheet items to continue to the instrument's empty-hand interaction");
        InteractionResult result = simulateServerUseItemOn(helper, player, blockInstrument, pianoPos, Direction.UP);

        assertTrue(helper, result.consumesAction(),
                "Expected using a block instrument on an existing instrument to be consumed by the GUI interaction");
        assertTrue(helper, helper.getLevel().getBlockState(helper.absolutePos(pianoPos.above())).isAir(),
                "Expected no instrument block to be placed on top of the existing instrument");
        assertTrue(helper, player.getMainHandItem().getCount() == initialCount,
                "Expected the held block instrument to not be consumed by a block placement");
        helper.succeed();
    }

    @GameTest
    public void signedMusicSheetsStackToSixteenBySameGeneration(GameTestHelper helper) {
        RecipeNoteCloning recipe = RecipeNoteCloning.INSTANCE;
        UUID id = UUID.randomUUID();
        ItemStack original = createSheetStack(id, 1, 1, 8, 8, 1.0f, "song", "composer");

        List<ItemStack> items = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
        items.set(0, original.copy());
        items.set(1, new ItemStack(Items.MUSIC_SHEET));
        CraftingInput grid = CraftingInput.of(3, 3, items);

        ItemStack clone = recipe.assemble(grid);
        assertTrue(helper, !clone.isEmpty(), "Expected the clone recipe to produce a signed sheet");
        assertTrue(helper, clone.getMaxStackSize() == ItemMusicSheet.SIGNED_STACK_SIZE,
                "Expected a cloned (signed) music sheet to stack up to 16");

        ItemStack cloneAgain = recipe.assemble(grid);
        assertTrue(helper, ItemStack.isSameItemSameComponents(clone, cloneAgain),
                "Expected two identical signed sheets of the same generation to be stackable");

        ItemStack signedOriginal = createSheetStack(id, 1, 1, 8, 8, 1.0f, "song", "composer");
        ItemMusicSheet.updateStackSize(signedOriginal);
        assertTrue(helper, signedOriginal.getMaxStackSize() == ItemMusicSheet.SIGNED_STACK_SIZE,
                "Expected a signed original sheet to stack up to 16");
        assertTrue(helper, !ItemStack.isSameItemSameComponents(clone, signedOriginal),
                "Expected signed sheets of different generations to not stack together");

        ItemStack empty1 = new ItemStack(Items.MUSIC_SHEET);
        ItemStack empty2 = new ItemStack(Items.MUSIC_SHEET);
        assertTrue(helper, empty1.getMaxStackSize() == 1, "Expected an empty music sheet to keep stack size 1");
        assertTrue(helper, ItemStack.isSameItemSameComponents(empty1, empty2),
                "Expected empty music sheets to remain stackable with each other");
        helper.succeed();
    }

    @GameTest
    public void pianoUseOutOfRangeDoesNotStartPlayback(GameTestHelper helper) {
        BlockPos pianoPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placePiano(helper, pianoPos, Direction.NORTH);

        BlockPos absolutePos = helper.absolutePos(pianoPos);
        player.snapTo(Vec3.atCenterOf(absolutePos).add(5.0D, 0.0D, 0.0D));
        ItemStack sheet = createSheetStack(helper, 8, 8);
        InteractionResult result = useBlockWithItem(helper, player, sheet, pianoPos, Direction.UP);

        assertTrue(helper, !result.consumesAction(), "Expected distant piano use to be ignored");
        assertTrue(helper, countSpiritsNear(helper, pianoPos) == 0, "Expected distant piano use to not start playback");
        helper.succeed();
    }
}
