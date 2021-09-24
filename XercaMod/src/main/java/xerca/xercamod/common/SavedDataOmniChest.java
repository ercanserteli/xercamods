package xerca.xercamod.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

public class SavedDataOmniChest extends SavedData {
    private final ContainerOmniChest omniChestInventory;

    private SavedDataOmniChest(ContainerOmniChest chest){
        this.omniChestInventory = chest;
    }

    public SavedDataOmniChest(){
        this(new ContainerOmniChest());
    }

    public static SavedDataOmniChest load(CompoundTag tag) {
        Tag chestTag = tag.get("OmniChest");
        if(chestTag instanceof ListTag){
            ListTag invData = (ListTag) chestTag;
            ContainerOmniChest chest = new ContainerOmniChest();
            chest.fromTag(invData);
            SavedDataOmniChest chestData = new SavedDataOmniChest(chest);
            return chestData;
        }
        else{
            return new SavedDataOmniChest();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("OmniChest", omniChestInventory.createTag());
        return tag;
    }

    public ContainerOmniChest getInventory(){
        return omniChestInventory;
    }
}
