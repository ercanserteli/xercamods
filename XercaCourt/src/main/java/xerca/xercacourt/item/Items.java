package xerca.xercacourt.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercacourt.Mod;

public final class Items {
    public static final Item GAVEL = new ItemGavel();
    public static final Item ATTORNEY_BADGE = new ItemBadge();
    public static final Item PROSECUTOR_BADGE = new ItemBadge();

    private Items() {
    }

    public static void register(RegisterEvent.RegisterHelper<Item> helper) {
        helper.register(Mod.id("gavel"), GAVEL);
        helper.register(Mod.id("attorney_badge"), ATTORNEY_BADGE);
        helper.register(Mod.id("prosecutor_badge"), PROSECUTOR_BADGE);
    }
}
