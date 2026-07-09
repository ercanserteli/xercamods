package xerca.xercaomnichest.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;

public class OmniChestSavedData extends SavedData {
    public static final SavedDataType<OmniChestSavedData> TYPE = new SavedDataType<>(
            "omni_chest",
            context -> new OmniChestSavedData(),
            context -> CompoundTag.CODEC.xmap(
                    tag -> load(tag, context.levelOrThrow().registryAccess()),
                    data -> data.save(new CompoundTag(), context.levelOrThrow().registryAccess())),
            DataFixTypes.SAVED_DATA_MAP_DATA);

    private final OmniChestInventory inventory;

    public OmniChestSavedData() {
        this.inventory = new OmniChestInventory(this::setDirty);
    }

    public static OmniChestSavedData load(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        OmniChestSavedData data = new OmniChestSavedData();
        Tag chestTag = tag.get("OmniChest");
        if (chestTag instanceof ListTag listTag) {
            for (int i = 0; i < data.inventory.getContainerSize(); ++i) {
                data.inventory.setItem(i, ItemStack.EMPTY);
            }

            for (Tag entryTag : listTag) {
                if (!(entryTag instanceof CompoundTag entry)) {
                    continue;
                }
                int slot = entry.getByteOr("Slot", (byte) 0) & 255;
                if (slot < data.inventory.getContainerSize()) {
                    data.inventory.setItem(slot, ItemStack.parse(registries, entry).orElse(ItemStack.EMPTY));
                }
            }
        }
        data.setDirty(false);
        return data;
    }

    public CompoundTag save(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag stackTag = (CompoundTag) stack.save(registries, new CompoundTag());
                stackTag.putByte("Slot", (byte) i);
                listTag.add(stackTag);
            }
        }
        tag.put("OmniChest", listTag);
        return tag;
    }

    public OmniChestInventory getInventory() {
        return inventory;
    }
}
