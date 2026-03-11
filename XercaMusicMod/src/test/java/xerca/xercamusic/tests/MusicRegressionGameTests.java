package xerca.xercamusic.tests;

import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.packets.SendNotesPartToServerPacket;
import xerca.xercamusic.common.packets.SingleNoteClientPacket;
import xerca.xercamusic.common.packets.TripleNoteClientPacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.MODID;

@GameTestHolder(MODID)
public class MusicRegressionGameTests {
    private static CompoundTag noteTag(int note, int time, int volume, int length) {
        CompoundTag tag = new CompoundTag();
        tag.putByte("n", (byte) note);
        tag.putShort("d", (short) time);
        tag.putByte("v", (byte) volume);
        tag.putByte("l", (byte) length);
        return tag;
    }

    @GameTest(template = "basic_test", batch = "music_regressions")
    @PrefixGameTestTemplate(false)
    public static void fillArrayFromNbtSortsAndRemovesOverlaps(GameTestHelper helper) {
        CompoundTag sheetTag = new CompoundTag();
        ListTag notesTag = new ListTag();
        notesTag.add(noteTag(64, 5, 100, 4));
        notesTag.add(noteTag(65, 2, 100, 1));
        notesTag.add(noteTag(64, 5, 100, 1)); // duplicate note/time with different length
        notesTag.add(noteTag(10, 3, 100, 1)); // invalid note
        notesTag.add(noteTag(120, 4, 100, 1)); // invalid note
        sheetTag.put("notes", notesTag);

        ArrayList<NoteEvent> loaded = new ArrayList<>();
        NoteEvent.fillArrayFromNBT(loaded, sheetTag);

        helper.assertTrue(loaded.size() == 2, "Expected only two valid unique notes after load sanitization");
        helper.assertTrue(loaded.get(0).time == 2 && loaded.get(0).note == 65, "Expected earliest note to be first after sorting");
        helper.assertTrue(loaded.get(1).time == 5 && loaded.get(1).note == 64, "Expected duplicate note/time to be removed");
        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "music_regressions")
    @PrefixGameTestTemplate(false)
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

        ArrayList<NoteEvent> joined = MusicManager.getFinishedNotesFromBuffer(id);
        helper.assertTrue(joined != null && joined.size() == 2, "Expected joined notes from finished temp buffer");

        ArrayList<NoteEvent> secondRead = MusicManager.getFinishedNotesFromBuffer(id);
        helper.assertTrue(secondRead == null, "Expected finished temp buffer to be consumed and removed");
        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "music_regressions")
    @PrefixGameTestTemplate(false)
    public static void musicSpiritSpawnDataHandlesInvalidBlockInstrument(GameTestHelper helper) {
        EntityMusicSpirit spirit = new EntityMusicSpirit(helper.getLevel());

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(-1); // invalid body entity id
        buf.writeInt(0);
        buf.writeInt(0);
        buf.writeInt(0);
        buf.writeInt(-1); // invalid block instrument id

        try {
            spirit.readSpawnData(buf);
        } catch (Throwable t) {
            helper.assertTrue(false, "Expected invalid spawn data to be ignored without crash, but got: " + t);
            return;
        }

        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "music_regressions")
    @PrefixGameTestTemplate(false)
    public static void singleNoteClientDecodeKeepsPlayerIdWithoutLevelLookup(GameTestHelper helper) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(64);      // note
        buf.writeInt(0);       // instrument id
        buf.writeInt(12345);   // player id
        buf.writeBoolean(false);
        buf.writeFloat(0.8f);

        SingleNoteClientPacket packet = SingleNoteClientPacket.decode(buf);
        helper.assertTrue(packet != null && packet.isMessageValid(), "Expected packet decode to succeed without client level lookup");
        helper.assertTrue(packet.getPlayerId() == 12345, "Expected player id to be preserved in decoded packet");
        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "music_regressions")
    @PrefixGameTestTemplate(false)
    public static void tripleNoteClientDecodeKeepsEntityIdWithoutLevelLookup(GameTestHelper helper) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(60);
        buf.writeInt(64);
        buf.writeInt(67);
        buf.writeInt(0);       // instrument id
        buf.writeInt(54321);   // entity id

        TripleNoteClientPacket packet = TripleNoteClientPacket.decode(buf);
        helper.assertTrue(packet != null && packet.isMessageValid(), "Expected packet decode to succeed without client level lookup");
        helper.assertTrue(packet.getEntityId() == 54321, "Expected entity id to be preserved in decoded packet");
        helper.succeed();
    }
}
