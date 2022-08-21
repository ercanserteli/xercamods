package xerca.xercamod.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

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
        if(chestTag instanceof ListTag invData){
            ContainerOmniChest chest = new ContainerOmniChest();
            chest.fromTag(invData);
            return new SavedDataOmniChest(chest);
        }
        else{
            return new SavedDataOmniChest();
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        tag.put("OmniChest", omniChestInventory.createTag());
        return tag;
    }

    public ContainerOmniChest getInventory(){
        return omniChestInventory;
    }
}
