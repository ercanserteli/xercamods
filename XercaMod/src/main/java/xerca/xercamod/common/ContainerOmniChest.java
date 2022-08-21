package xerca.xercamod.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.tile_entity.TileEntityOmniChest;

import java.util.HashMap;
import java.util.Map;

public class ContainerOmniChest extends SimpleContainer {
    private final Map<Player, TileEntityOmniChest> playerChests;

    public ContainerOmniChest() {
        super(27);
        this.playerChests = new HashMap<>();
    }

    public void fromTag(@NotNull ListTag tags) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for(int k = 0; k < tags.size(); ++k) {
            CompoundTag compoundtag = tags.getCompound(k);
            int j = compoundtag.getByte("Slot") & 255;
            if (j < this.getContainerSize()) {
                this.setItem(j, ItemStack.of(compoundtag));
            }
        }
    }

    public @NotNull ListTag createTag() {
        ListTag listtag = new ListTag();

        for(int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                itemstack.save(compoundtag);
                listtag.add(compoundtag);
            }
        }

        return listtag;
    }

    public void setActiveChest(TileEntityOmniChest chest, Player player) {
        XercaMod.LOGGER.info(player.getName().getString() + " container setActiveChest");
        playerChests.put(player, chest);
    }

    public boolean stillValid(@NotNull Player player) {
        return playerChests.containsKey(player) && playerChests.get(player).stillValid(player);
    }

    public void startOpen(Player player) {
        XercaMod.LOGGER.info(player.getName().getString() + " container startOpen");
        if(playerChests.containsKey(player)){
            TileEntityOmniChest chest = playerChests.get(player);
            chest.startOpen(player);
            super.startOpen(player);
        }
    }

    public void stopOpen(Player player) {
        XercaMod.LOGGER.info(player.getName().getString() + " container stopOpen");
        if(playerChests.containsKey(player)){
            TileEntityOmniChest chest = playerChests.get(player);
            chest.stopOpen(player);
            super.stopOpen(player);
            playerChests.remove(player);
        }
    }

    public boolean testPlayerChest(Player player, TileEntityOmniChest chest){
        return playerChests.get(player).equals(chest);
    }
}