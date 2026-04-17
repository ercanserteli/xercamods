package xerca.xercamusic.tests;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.CommandExport;
import xerca.xercamusic.common.CommandImport;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.block.BlockMetronome;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.clientbound.SingleNoteClientPacket;
import xerca.xercamusic.common.packets.clientbound.TripleNoteClientPacket;
import xerca.xercamusic.common.packets.serverbound.SendNotesPartToServerPacket;
import xerca.xercamusic.common.tile_entity.TileEntityMetronome;
import xerca.xercamusic.common.tile_entity.TileEntityMusicBox;

import java.lang.reflect.Field;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"PMD.AvoidAccessibilityAlteration", "removal"})
public final class MusicRegressionGameTests {
    private static final String BASIC_TEMPLATE = Mod.MODID + ":basic_test";
    private static final Field MUSIC_BOX_IS_PLAYING_FIELD;
    private static final Field MUSIC_BOX_BPS_FIELD;
    private static final Field MUSIC_BOX_VOLUME_FIELD;
    private static final Field METRONOME_AGE_FIELD;
    private static final Field METRONOME_OLD_POWERED_STATE_FIELD;
    private static final Field METRONOME_COUNTDOWN_FIELD;

    static {
        try {
            MUSIC_BOX_IS_PLAYING_FIELD = TileEntityMusicBox.class.getDeclaredField("isPlaying");
            MUSIC_BOX_IS_PLAYING_FIELD.setAccessible(true);
            MUSIC_BOX_BPS_FIELD = TileEntityMusicBox.class.getDeclaredField("bps");
            MUSIC_BOX_BPS_FIELD.setAccessible(true);
            MUSIC_BOX_VOLUME_FIELD = TileEntityMusicBox.class.getDeclaredField("volume");
            MUSIC_BOX_VOLUME_FIELD.setAccessible(true);
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
        helper.assertTrue(helper.getLevel().getBlockEntity(absolutePos) instanceof TileEntityMusicBox,
                "Expected music box block entity at " + relativePos);
        return (TileEntityMusicBox) helper.getLevel().getBlockEntity(absolutePos);
    }

    private static TileEntityMetronome requireMetronome(GameTestHelper helper, BlockPos relativePos) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        helper.assertTrue(helper.getLevel().getBlockEntity(absolutePos) instanceof TileEntityMetronome,
                "Expected metronome block entity at " + relativePos);
        return (TileEntityMetronome) helper.getLevel().getBlockEntity(absolutePos);
    }

    private static EntityMusicSpirit requireSingleSpiritNear(GameTestHelper helper, BlockPos relativePos, String message) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(2.5D, 2.5D, 2.5D);
        List<EntityMusicSpirit> spirits = helper.getLevel().getEntitiesOfClass(EntityMusicSpirit.class, searchBox, Entity::isAlive);
        helper.assertTrue(!spirits.isEmpty(), message);
        return spirits.get(0);
    }

    private static ItemEntity requireSingleDroppedItemNear(GameTestHelper helper, BlockPos relativePos, Item item, String message) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(2.5D, 2.5D, 2.5D);
        List<ItemEntity> items = helper.getLevel().getEntitiesOfClass(ItemEntity.class, searchBox, it -> it.getItem().is(item));
        helper.assertTrue(!items.isEmpty(), message);
        return items.get(0);
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

    private static ItemInteractionResult useBlockWithItem(GameTestHelper helper, Player player, ItemStack stack, BlockPos relativePos, Direction face) {
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
        helper.assertTrue(false, "Unsupported block for useBlockWithItem: " + state.getBlock());
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static ItemInteractionResult useBlockFace(GameTestHelper helper, Player player, BlockPos relativePos, Direction face) {
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
        helper.assertTrue(false, "Unsupported block for useBlockFace: " + state.getBlock());
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
        helper.assertTrue(false, "Unsupported block for useBlockWithoutItem: " + state.getBlock());
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
                                              String title, String author) {
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

    private static ServerPlayer makeMockServerPlayer(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }

    private static Path exportPath(String filename) {
        return Path.of("music_sheets", filename + ".sheet");
    }

    private static void deleteIfExists(GameTestHelper helper, Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            helper.assertTrue(false, "Failed to delete test export file " + path + ": " + e);
        }
    }

    private static CompoundTag readExportedTag(GameTestHelper helper, Path path) {
        helper.assertTrue(Files.exists(path), "Expected exported music sheet file at " + path);
        try {
            CompoundTag tag = NbtIo.read(path);
            helper.assertTrue(tag != null, "Expected exported music sheet NBT at " + path);
            return tag == null ? new CompoundTag() : tag;
        } catch (IOException e) {
            helper.assertTrue(false, "Failed to read exported music sheet " + path + ": " + e);
            return new CompoundTag();
        }
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
            helper.assertTrue(false, "Failed to read music box isPlaying field: " + e);
            return false;
        }
    }

    private static byte getMusicBoxBps(GameTestHelper helper, TileEntityMusicBox musicBox) {
        try {
            return MUSIC_BOX_BPS_FIELD.getByte(musicBox);
        } catch (IllegalAccessException e) {
            helper.assertTrue(false, "Failed to read music box bps field: " + e);
            return 0;
        }
    }

    private static float getMusicBoxVolume(GameTestHelper helper, TileEntityMusicBox musicBox) {
        try {
            return MUSIC_BOX_VOLUME_FIELD.getFloat(musicBox);
        } catch (IllegalAccessException e) {
            helper.assertTrue(false, "Failed to read music box volume field: " + e);
            return 0.0f;
        }
    }

    private static int getMetronomeAge(GameTestHelper helper, TileEntityMetronome metronome) {
        try {
            return METRONOME_AGE_FIELD.getInt(metronome);
        } catch (IllegalAccessException e) {
            helper.assertTrue(false, "Failed to read metronome age field: " + e);
            return -1;
        }
    }

    private static boolean getMetronomeOldPoweredState(GameTestHelper helper, TileEntityMetronome metronome) {
        try {
            return METRONOME_OLD_POWERED_STATE_FIELD.getBoolean(metronome);
        } catch (IllegalAccessException e) {
            helper.assertTrue(false, "Failed to read metronome oldPoweredState field: " + e);
            return false;
        }
    }

    private static int getMetronomeCountDown(GameTestHelper helper, TileEntityMetronome metronome) {
        try {
            return METRONOME_COUNTDOWN_FIELD.getInt(metronome);
        } catch (IllegalAccessException e) {
            helper.assertTrue(false, "Failed to read metronome countDown field: " + e);
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

    @GameTest(template = BASIC_TEMPLATE, batch = "music_regressions")
    public static void fillArrayFromNbtSortsAndRemovesOverlaps(GameTestHelper helper) {
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

        helper.assertTrue(loaded.size() == 2, "Expected only two valid unique notes after load sanitization");
        helper.assertTrue(loaded.get(0).time == 2 && loaded.get(0).note == 65, "Expected earliest note to be first after sorting");
        helper.assertTrue(loaded.get(1).time == 5 && loaded.get(1).note == 64, "Expected duplicate note/time to be removed");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_regressions")
    public static void finishedTempBufferIsConsumedOnce(GameTestHelper helper) {
        UUID id = UUID.randomUUID();
        ArrayList<NoteEvent> part0 = new ArrayList<>();
        ArrayList<NoteEvent> part1 = new ArrayList<>();
        part0.add(new NoteEvent((byte) 64, (short) 1, (byte) 80, (byte) 1));
        part1.add(new NoteEvent((byte) 66, (short) 2, (byte) 90, (byte) 1));

        boolean doneAfterFirst = MusicManager.addNotesPart(new SendNotesPartToServerPacket(id, 2, 0, part0));
        boolean doneAfterSecond = MusicManager.addNotesPart(new SendNotesPartToServerPacket(id, 2, 1, part1));

        helper.assertTrue(!doneAfterFirst, "Expected first notes part to be incomplete");
        helper.assertTrue(doneAfterSecond, "Expected second notes part to complete the buffer");

        List<NoteEvent> joined = MusicManager.getFinishedNotesFromBuffer(id);
        helper.assertTrue(joined.size() == 2, "Expected joined notes from finished temp buffer");

        List<NoteEvent> secondRead = MusicManager.getFinishedNotesFromBuffer(id);
        helper.assertTrue(secondRead.isEmpty(), "Expected finished temp buffer to be consumed and removed");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_regressions")
    public static void musicBoxSheetValuesAreSanitizedOnInsert(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        placeMusicBox(helper, boxPos, Direction.NORTH);
        TileEntityMusicBox musicBox = requireMusicBox(helper, boxPos);

        ItemStack sheet = createSheetStack(helper, 8, 8);
        sheet.set(Items.SHEET_BPS, (byte) 0);
        sheet.set(Items.SHEET_VOLUME, 2.5f);
        musicBox.setSheetStack(sheet, false);

        helper.assertTrue(getMusicBoxBps(helper, musicBox) == 1, "Expected music box bps to clamp to minimum of 1");
        helper.assertTrue(getMusicBoxVolume(helper, musicBox) == 1.0f, "Expected music box volume to clamp to maximum of 1.0");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_regressions")
    public static void musicSpiritSpawnDataHandlesInvalidBlockInstrument(GameTestHelper helper) {
        EntityMusicSpirit spirit = new EntityMusicSpirit(helper.getLevel());

        try {
            spirit.buildFromSpawnData(0, -1, 0, -1, -1);
        } catch (Exception e) {
            helper.assertTrue(false, "Expected invalid spawn data to be ignored without crash, but got: " + e);
            return;
        }

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_regressions")
    public static void singleNoteClientDecodeKeepsPlayerIdWithoutLevelLookup(GameTestHelper helper) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(64);
        buf.writeInt(0);
        buf.writeInt(12345);
        buf.writeBoolean(false);
        buf.writeFloat(0.8f);

        SingleNoteClientPacket packet = SingleNoteClientPacket.decode(buf);
        helper.assertTrue(packet.playerId() == 12345, "Expected player id to be preserved in decoded packet");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_regressions")
    public static void tripleNoteClientDecodeKeepsEntityIdWithoutLevelLookup(GameTestHelper helper) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(60);
        buf.writeInt(64);
        buf.writeInt(67);
        buf.writeInt(0);
        buf.writeInt(54321);

        TripleNoteClientPacket packet = TripleNoteClientPacket.decode(buf);
        helper.assertTrue(packet.entityId() == 54321, "Expected entity id to be preserved in decoded packet");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_import_export")
    public static void signedSheetExportAndImportPreserveIdentityAndMarkers(GameTestHelper helper) {
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

        ServerPlayer exporter = makeMockServerPlayer(helper);
        exporter.setItemSlot(EquipmentSlot.MAINHAND,
                createSheetStack(id, version, 1, 12, 8, 0.75f, "signed_export", "tester"));

        helper.assertTrue(CommandExport.doExport(exporter, exportName),
                "Expected signed music sheet export to find a sheet in hand");

        CompoundTag exportedTag = readExportedTag(helper, path);
        helper.assertTrue(exportedTag.contains(ItemMusicSheet.KEY_ID), "Expected signed export to store the sheet id");
        helper.assertTrue(exportedTag.getUUID(ItemMusicSheet.KEY_ID).equals(id),
                "Expected signed export to preserve the original sheet id");
        helper.assertTrue(exportedTag.getInt(ItemMusicSheet.KEY_VERSION) == version,
                "Expected signed export to preserve the original sheet version");
        helper.assertTrue("signed_export".equals(exportedTag.getString(ItemMusicSheet.KEY_TITLE)),
                "Expected signed export to include the title");
        helper.assertTrue("tester".equals(exportedTag.getString(ItemMusicSheet.KEY_AUTHOR)),
                "Expected signed export to include the author");

        List<NoteEvent> exportedNotes = notesFromTag(exportedTag);
        List<VolumeMarker> exportedMarkers = volumeMarkersFromTag(exportedTag);
        helper.assertTrue(exportedNotes.size() == notes.size(),
                "Expected signed export to write all sheet notes");
        helper.assertTrue(exportedMarkers.size() == markers.size(),
                "Expected signed export to write all volume markers");

        ServerPlayer importer = makeMockServerPlayer(helper);
        importer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.MUSIC_SHEET));
        CommandImport.doImport(exportedTag, exportedNotes, id, importer);

        ItemStack importedSheet = findImportedSheet(importer);
        helper.assertTrue(!importedSheet.isEmpty(), "Expected signed import to produce a populated music sheet");
        helper.assertTrue(id.equals(importedSheet.get(Items.SHEET_ID)),
                "Expected signed import to keep the exported sheet id");
        helper.assertTrue(importedSheet.getOrDefault(Items.SHEET_VERSION, -1) == version,
                "Expected signed import to keep the exported sheet version");
        helper.assertTrue(importedSheet.getOrDefault(Items.SHEET_GENERATION, 0) == 2,
                "Expected signed import to advance generation from original to copy");
        helper.assertTrue("signed_export".equals(importedSheet.get(Items.SHEET_TITLE)),
                "Expected signed import to keep the title");
        helper.assertTrue("tester".equals(importedSheet.get(Items.SHEET_AUTHOR)),
                "Expected signed import to keep the author");

        MusicManager.MusicData importedData = MusicManager.getMusicData(id, version, helper.getLevel().getServer());
        helper.assertTrue(importedData != null, "Expected signed import to register music data on the server");
        helper.assertTrue(importedData.notes().size() == notes.size(),
                "Expected signed import to restore the exported note count");
        helper.assertTrue(importedData.volumeMarkers() != null && importedData.volumeMarkers().size() == markers.size(),
                "Expected signed import to restore exported volume markers");

        deleteIfExists(helper, path);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_import_export")
    public static void unsignedMultipartImportFromExportGetsFreshIdentityAndConsumesBuffer(GameTestHelper helper) {
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

        ServerPlayer exporter = makeMockServerPlayer(helper);
        exporter.setItemSlot(EquipmentSlot.MAINHAND,
                createSheetStack(originalId, originalVersion, 0, 16, 12, 1.0f, null, null));

        helper.assertTrue(CommandExport.doExport(exporter, exportName),
                "Expected unsigned music sheet export to find a sheet in hand");

        CompoundTag exportedTag = readExportedTag(helper, path);
        helper.assertTrue(!exportedTag.contains(ItemMusicSheet.KEY_TITLE),
                "Expected unsigned export to omit the title");
        helper.assertTrue(!exportedTag.contains(ItemMusicSheet.KEY_AUTHOR),
                "Expected unsigned export to omit the author");

        List<NoteEvent> exportedNotes = notesFromTag(exportedTag);
        List<VolumeMarker> exportedMarkers = volumeMarkersFromTag(exportedTag);
        helper.assertTrue(exportedNotes.size() == notes.size(),
                "Expected unsigned export to write the sheet notes");
        helper.assertTrue(exportedMarkers.size() == markers.size(),
                "Expected unsigned export to write the sheet volume markers");

        helper.assertTrue(!MusicManager.addNotesPart(new SendNotesPartToServerPacket(originalId, 2, 0,
                        new ArrayList<>(exportedNotes.subList(0, 1)))),
                "Expected the first multipart note payload to leave the import buffer incomplete");
        helper.assertTrue(MusicManager.addNotesPart(new SendNotesPartToServerPacket(originalId, 2, 1,
                        new ArrayList<>(exportedNotes.subList(1, exportedNotes.size())))),
                "Expected the second multipart note payload to complete the import buffer");

        ServerPlayer importer = makeMockServerPlayer(helper);
        importer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.MUSIC_SHEET));
        CommandImport.doImport(exportedTag, null, originalId, importer);

        ItemStack importedSheet = findImportedSheet(importer);
        helper.assertTrue(!importedSheet.isEmpty(), "Expected unsigned import to produce a populated music sheet");
        UUID importedId = importedSheet.get(Items.SHEET_ID);
        helper.assertTrue(importedId != null, "Expected unsigned import to assign a new sheet id");
        helper.assertTrue(!originalId.equals(importedId),
                "Expected unsigned import to regenerate the sheet id instead of reusing the exported one");
        helper.assertTrue(importedSheet.getOrDefault(Items.SHEET_VERSION, -1) == 1,
                "Expected unsigned import to reset the version to 1");
        helper.assertTrue(importedSheet.getOrDefault(Items.SHEET_GENERATION, -1) == 0,
                "Expected unsigned import to stay unsigned after import");
        helper.assertTrue(importedSheet.get(Items.SHEET_TITLE) == null,
                "Expected unsigned import to keep the sheet title empty");
        helper.assertTrue(importedSheet.get(Items.SHEET_AUTHOR) == null,
                "Expected unsigned import to keep the sheet author empty");

        MusicManager.MusicData importedData = MusicManager.getMusicData(importedId, 1, helper.getLevel().getServer());
        helper.assertTrue(importedData != null, "Expected unsigned import to store music data under the new id");
        helper.assertTrue(importedData.notes().size() == notes.size(),
                "Expected unsigned import to restore the multipart note payload");
        helper.assertTrue(importedData.volumeMarkers() != null && importedData.volumeMarkers().size() == markers.size(),
                "Expected unsigned import to restore exported volume markers");
        helper.assertTrue(MusicManager.getFinishedNotesFromBuffer(originalId).isEmpty(),
                "Expected unsigned import to consume and clear the multipart note buffer");

        deleteIfExists(helper, path);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_box")
    public static void musicBoxInsertsAndEjectsMusicSheet(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMusicBox(helper, boxPos, Direction.NORTH);

        ItemStack sheet = createSheetStack(helper, 8, 8);
        UseResult insert = useHeldItemOnBlock(helper, player, sheet, boxPos, Direction.UP);
        helper.assertTrue(insert.handStack().isEmpty(), "Expected music sheet stack to be consumed on insert");
        helper.assertTrue(insert.state().getValue(BlockMusicBox.HAS_MUSIC), "Expected music box to report inserted music sheet");
        helper.assertTrue(!requireMusicBox(helper, boxPos).getSheetStack().isEmpty(), "Expected tile entity to store inserted music sheet");

        ItemInteractionResult ejectResult = useBlockFace(helper, player, boxPos, Direction.UP);
        helper.assertTrue(ejectResult.consumesAction(), "Expected top-face ejection to consume interaction");
        BlockState ejectedState = helper.getLevel().getBlockState(helper.absolutePos(boxPos));
        helper.assertTrue(!ejectedState.getValue(BlockMusicBox.HAS_MUSIC), "Expected music sheet slot to be empty after ejection");
        helper.assertTrue(requireMusicBox(helper, boxPos).getSheetStack().isEmpty(), "Expected tile entity music sheet to be cleared after ejection");
        helper.assertTrue(requireSingleDroppedItemNear(helper, boxPos, Items.MUSIC_SHEET, "Expected ejected music sheet item").getItem().get(Items.SHEET_ID) != null,
                "Expected dropped music sheet to keep its music data components");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_box")
    public static void musicBoxInsertsAndEjectsInstrument(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMusicBox(helper, boxPos, Direction.NORTH);

        ItemStack guitar = new ItemStack(Items.GUITAR);
        UseResult insert = useHeldItemOnBlock(helper, player, guitar, boxPos, Direction.SOUTH);
        helper.assertTrue(insert.handStack().isEmpty(), "Expected instrument stack to be consumed on insert");
        helper.assertTrue(insert.state().getValue(BlockMusicBox.HAS_INSTRUMENT), "Expected music box to report inserted instrument");
        helper.assertTrue(requireMusicBox(helper, boxPos).getInstrument() == Items.GUITAR, "Expected tile entity to store inserted instrument");

        ItemInteractionResult ejectResult = useBlockFace(helper, player, boxPos, Direction.SOUTH);
        helper.assertTrue(ejectResult.consumesAction(), "Expected back-face instrument ejection to consume interaction");
        BlockState ejectedState = helper.getLevel().getBlockState(helper.absolutePos(boxPos));
        helper.assertTrue(!ejectedState.getValue(BlockMusicBox.HAS_INSTRUMENT), "Expected instrument slot to be empty after ejection");
        helper.assertTrue(requireMusicBox(helper, boxPos).getInstrument() == null, "Expected tile entity instrument to be cleared after ejection");
        requireSingleDroppedItemNear(helper, boxPos, Items.GUITAR, "Expected ejected instrument item");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_box")
    public static void musicBoxStartsAndStopsOnRedstonePulses(GameTestHelper helper) {
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
        helper.assertTrue(isMusicBoxPlaying(helper, musicBox), "Expected music box to start playing on first redstone pulse");
        helper.assertTrue(!helper.getLevel().getBlockState(absolutePos).getValue(BlockMusicBox.POWERING),
                "Expected music box to not emit redstone while only playing");

        setMusicBoxPowered(helper, boxPos, false);
        tickMusicBox(helper, boxPos);
        setMusicBoxPowered(helper, boxPos, true);
        tickMusicBox(helper, boxPos);
        helper.assertTrue(!isMusicBoxPlaying(helper, musicBox), "Expected second redstone pulse to stop playback");
        helper.assertTrue(!helper.getLevel().getBlockState(absolutePos).getValue(BlockMusicBox.POWERING),
                "Expected stopped music box to not emit redstone");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_box")
    public static void musicBoxEndsPlaybackAndEmitsTimedRedstone(GameTestHelper helper) {
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
        helper.assertTrue(endedState.getValue(BlockMusicBox.POWERING), "Expected music box to emit redstone when playback ends");
        helper.assertTrue(!isMusicBoxPlaying(helper, musicBox), "Expected music box playback to end");
        helper.assertTrue(endedState.getSignal(helper.getLevel(), absolutePos, Direction.WEST) == 15,
                "Expected full redstone output on the music box output side");
        helper.assertTrue(endedState.getSignal(helper.getLevel(), absolutePos, Direction.EAST) == 0,
                "Expected no redstone output on non-output sides");

        for (int i = 0; i < 11; i++) {
            tickMusicBox(helper, boxPos);
        }

        BlockState finalState = helper.getLevel().getBlockState(absolutePos);
        helper.assertTrue(!finalState.getValue(BlockMusicBox.POWERING), "Expected timed redstone output to clear after 10 ticks");
        helper.assertTrue(finalState.getSignal(helper.getLevel(), absolutePos, Direction.WEST) == 0,
                "Expected redstone output to drop to zero after the pulse window");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_box")
    public static void musicBoxDoesNotStartWithoutSheet(GameTestHelper helper) {
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
        helper.assertTrue(!isMusicBoxPlaying(helper, musicBox), "Expected music box to stay stopped when no sheet is inserted");
        helper.assertTrue(!state.getValue(BlockMusicBox.POWERING), "Expected no redstone output when no sheet is inserted");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_box")
    public static void musicBoxDoesNotStartWithoutInstrument(GameTestHelper helper) {
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
        helper.assertTrue(!isMusicBoxPlaying(helper, musicBox), "Expected music box to stay stopped when no instrument is inserted");
        helper.assertTrue(!state.getValue(BlockMusicBox.POWERING), "Expected no redstone output when no instrument is inserted");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "music_box")
    public static void musicBoxEmptySlotsDoNotEjectAnything(GameTestHelper helper) {
        BlockPos boxPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMusicBox(helper, boxPos, Direction.NORTH);

        ItemInteractionResult topResult = useBlockFace(helper, player, boxPos, Direction.UP);
        ItemInteractionResult backResult = useBlockFace(helper, player, boxPos, Direction.SOUTH);

        BlockState state = helper.getLevel().getBlockState(helper.absolutePos(boxPos));
        helper.assertTrue(!topResult.consumesAction(), "Expected top interaction on empty slot to be ignored");
        helper.assertTrue(!backResult.consumesAction(), "Expected back interaction on empty slot to be ignored");
        helper.assertTrue(!state.getValue(BlockMusicBox.HAS_MUSIC), "Expected top interaction on empty music slot to leave box unchanged");
        helper.assertTrue(!state.getValue(BlockMusicBox.HAS_INSTRUMENT), "Expected back interaction on empty instrument slot to leave box unchanged");
        helper.assertTrue(countDroppedItemsNear(helper, boxPos, Items.MUSIC_SHEET) == 0, "Expected no sheet drops from empty music slot");
        helper.assertTrue(countDroppedItemsNear(helper, boxPos, Items.GUITAR) == 0, "Expected no instrument drops from empty instrument slot");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "metronome")
    public static void metronomeCyclesBpsAndCopiesTempoFromSheet(GameTestHelper helper) {
        BlockPos metronomePos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMetronome(helper, metronomePos, Direction.NORTH, 6, false);

        InteractionResult cycleResult = useBlockWithoutItem(helper, player, metronomePos, Direction.UP);
        BlockState cycled = helper.getLevel().getBlockState(helper.absolutePos(metronomePos));
        helper.assertTrue(cycleResult.consumesAction(), "Expected metronome to consume empty-hand use");
        helper.assertTrue(cycled.getValue(BlockMetronome.BPS) == 7, "Expected metronome to cycle tempo when used without a sheet");

        ItemStack sheet = createSheetStack(helper, 8, 12);
        ItemInteractionResult copyResult = useBlockWithItem(helper, player, sheet, metronomePos, Direction.UP);
        BlockState copied = helper.getLevel().getBlockState(helper.absolutePos(metronomePos));
        helper.assertTrue(copyResult.consumesAction(), "Expected metronome to consume sheet tempo copy use");
        helper.assertTrue(copied.getValue(BlockMetronome.BPS) == 12, "Expected metronome to copy tempo from held music sheet");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "metronome")
    public static void metronomePoweredTicksAdvanceCountdownAndResetOnRepower(GameTestHelper helper) {
        BlockPos metronomePos = new BlockPos(1, 2, 1);
        placeMetronome(helper, metronomePos, Direction.NORTH, 20, false);
        TileEntityMetronome metronome = requireMetronome(helper, metronomePos);

        helper.assertTrue(getMetronomeAge(helper, metronome) == 0, "Expected new metronome age to start at zero");
        helper.assertTrue(getMetronomeCountDown(helper, metronome) == 0, "Expected new metronome countdown to start at zero");
        helper.assertTrue(!getMetronomeOldPoweredState(helper, metronome), "Expected new metronome to start unpowered");

        setMetronomePowered(helper, metronomePos, true);
        tickMetronome(helper, metronomePos);
        helper.assertTrue(getMetronomeAge(helper, metronome) == 1, "Expected first powered tick to advance metronome age");
        helper.assertTrue(getMetronomeCountDown(helper, metronome) == 1, "Expected first powered tick to start countdown");
        helper.assertTrue(getMetronomeOldPoweredState(helper, metronome), "Expected powered tick to latch prior power state");

        tickMetronome(helper, metronomePos);
        helper.assertTrue(getMetronomeAge(helper, metronome) == 2, "Expected second powered tick to keep advancing age");
        helper.assertTrue(getMetronomeCountDown(helper, metronome) == 1, "Expected countdown to wait for the next pause interval at 20 bps");

        tickMetronome(helper, metronomePos);
        helper.assertTrue(getMetronomeAge(helper, metronome) == 3, "Expected third powered tick to keep advancing age");
        helper.assertTrue(getMetronomeCountDown(helper, metronome) == 2, "Expected countdown to increment again every two ticks at 20 bps");

        setMetronomePowered(helper, metronomePos, false);
        tickMetronome(helper, metronomePos);
        helper.assertTrue(!getMetronomeOldPoweredState(helper, metronome), "Expected unpowered tick to clear metronome power latch");

        setMetronomePowered(helper, metronomePos, true);
        tickMetronome(helper, metronomePos);
        helper.assertTrue(getMetronomeAge(helper, metronome) == 1, "Expected repowering to reset metronome age before ticking");
        helper.assertTrue(getMetronomeCountDown(helper, metronome) == 1, "Expected repowering to reset countdown before the next beat");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "metronome")
    public static void metronomeSheetWithoutTempoFallsBackToCycle(GameTestHelper helper) {
        BlockPos metronomePos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placeMetronome(helper, metronomePos, Direction.NORTH, 9, false);

        ItemStack sheet = new ItemStack(Items.MUSIC_SHEET);
        sheet.set(Items.SHEET_TITLE, "missing_bps");
        ItemInteractionResult result = useBlockWithItem(helper, player, sheet, metronomePos, Direction.UP);
        helper.assertTrue(!result.consumesAction(), "Expected sheet without tempo to fall through to default interaction");

        InteractionResult fallbackResult = useBlockWithoutItem(helper, player, metronomePos, Direction.UP);
        BlockState updated = helper.getLevel().getBlockState(helper.absolutePos(metronomePos));
        helper.assertTrue(fallbackResult.consumesAction(), "Expected fallback block interaction to cycle the metronome");
        helper.assertTrue(updated.getValue(BlockMetronome.BPS) == 10, "Expected sheet without tempo tag to fall back to normal metronome cycling");
        helper.succeed();
    }
    @GameTest(template = BASIC_TEMPLATE, batch = "piano")
    public static void pianoUseWithSheetStartsAndStopsBlockInstrumentPlayback(GameTestHelper helper) {
        BlockPos pianoPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placePiano(helper, pianoPos, Direction.NORTH);

        BlockPos absolutePos = helper.absolutePos(pianoPos);
        player.moveTo(Vec3.atCenterOf(absolutePos).add(1.0D, 0.0D, 0.0D));
        ItemStack sheet = createSheetStack(helper, 8, 8);
        ItemInteractionResult firstUse = useBlockWithItem(helper, player, sheet, pianoPos, Direction.UP);
        helper.assertTrue(firstUse.consumesAction(), "Expected piano use with sheet to consume interaction");
        EntityMusicSpirit spirit = requireSingleSpiritNear(helper, pianoPos, "Expected piano use with sheet to spawn music spirit");

        ItemInteractionResult secondUse = useBlockWithItem(helper, player, player.getMainHandItem(), pianoPos, Direction.UP);
        helper.assertTrue(secondUse.consumesAction(), "Expected second piano use with sheet to consume interaction");
        spirit.tick();

        AABB searchBox = new AABB(absolutePos).inflate(2.5D, 2.5D, 2.5D);
        List<EntityMusicSpirit> spirits = helper.getLevel().getEntitiesOfClass(EntityMusicSpirit.class, searchBox, Entity::isAlive);
        helper.assertTrue(spirits.isEmpty(), "Expected second piano use with sheet to stop existing playback spirit");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "piano")
    public static void pianoUseWithoutSheetDoesNotStartPlayback(GameTestHelper helper) {
        BlockPos pianoPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placePiano(helper, pianoPos, Direction.NORTH);

        BlockPos absolutePos = helper.absolutePos(pianoPos);
        player.moveTo(Vec3.atCenterOf(absolutePos).add(1.0D, 0.0D, 0.0D));
        player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        InteractionResult result = useBlockWithoutItem(helper, player, pianoPos, Direction.UP);
        helper.assertTrue(result == InteractionResult.PASS, "Expected empty-hand piano use to fall through on the server");
        helper.assertTrue(countSpiritsNear(helper, pianoPos) == 0, "Expected piano use without a sheet to not start playback");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "piano")
    public static void pianoUseOutOfRangeDoesNotStartPlayback(GameTestHelper helper) {
        BlockPos pianoPos = new BlockPos(1, 2, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        placePiano(helper, pianoPos, Direction.NORTH);

        BlockPos absolutePos = helper.absolutePos(pianoPos);
        player.moveTo(Vec3.atCenterOf(absolutePos).add(5.0D, 0.0D, 0.0D));
        ItemStack sheet = createSheetStack(helper, 8, 8);
        ItemInteractionResult result = useBlockWithItem(helper, player, sheet, pianoPos, Direction.UP);

        helper.assertTrue(!result.consumesAction(), "Expected distant piano use to be ignored");
        helper.assertTrue(countSpiritsNear(helper, pianoPos) == 0, "Expected distant piano use to not start playback");
        helper.succeed();
    }
}
