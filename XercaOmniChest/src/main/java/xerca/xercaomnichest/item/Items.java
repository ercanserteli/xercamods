package xerca.xercaomnichest.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.Blocks;

public final class Items {
    public static final Item OMNI_CHEST = new BlockItem(Blocks.OMNI_CHEST, new Item.Properties());

    private Items() {
    }

    public static void register(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register(Mod.id("omni_chest"), OMNI_CHEST);
    }
}
