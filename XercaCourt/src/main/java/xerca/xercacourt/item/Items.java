package xerca.xercacourt.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import xerca.xercacourt.Mod;

public final class Items {
    public static final Item GAVEL = new ItemGavel();
    public static final Item ATTORNEY_BADGE = new ItemBadge();
    public static final Item PROSECUTOR_BADGE = new ItemBadge();

    private Items() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, Mod.id("gavel"), GAVEL);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("attorney_badge"), ATTORNEY_BADGE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("prosecutor_badge"), PROSECUTOR_BADGE);
    }
}
