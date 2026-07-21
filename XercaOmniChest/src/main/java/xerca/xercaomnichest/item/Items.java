package xerca.xercaomnichest.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.Blocks;

public final class Items {
    public static final Item OMNI_CHEST = new BlockItem(Blocks.OMNI_CHEST, new Item.Properties().setId(Mod.itemKey("omni_chest")).useBlockDescriptionPrefix());

    private Items() {
    }
}
