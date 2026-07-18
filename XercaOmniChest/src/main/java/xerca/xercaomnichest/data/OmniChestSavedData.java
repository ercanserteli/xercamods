package xerca.xercaomnichest.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;

public class OmniChestSavedData extends SavedData {
    private record SlotEntry(byte slot, ItemStack stack) {
        static final Codec<SlotEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BYTE.fieldOf("Slot").forGetter(SlotEntry::slot),
                ItemStack.MAP_CODEC.forGetter(SlotEntry::stack)
        ).apply(instance, SlotEntry::new));
    }

    public static final SavedDataType<OmniChestSavedData> TYPE = new SavedDataType<>(
            "omni_chest",
            OmniChestSavedData::new,
            SlotEntry.CODEC.listOf().optionalFieldOf("OmniChest", List.of()).codec()
                    .xmap(OmniChestSavedData::load, OmniChestSavedData::toEntries),
            DataFixTypes.SAVED_DATA_MAP_DATA);

    private final OmniChestInventory inventory;

    public OmniChestSavedData() {
        this.inventory = new OmniChestInventory(this::setDirty);
    }

    private static OmniChestSavedData load(List<SlotEntry> entries) {
        OmniChestSavedData data = new OmniChestSavedData();
        for (SlotEntry entry : entries) {
            int slot = entry.slot() & 255;
            if (slot < data.inventory.getContainerSize()) {
                data.inventory.setItem(slot, entry.stack());
            }
        }
        data.setDirty(false);
        return data;
    }

    private List<SlotEntry> toEntries() {
        List<SlotEntry> entries = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                entries.add(new SlotEntry((byte) i, stack));
            }
        }
        return entries;
    }

    public OmniChestInventory getInventory() {
        return inventory;
    }
}
