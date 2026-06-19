package xerca.xercacourt.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import xerca.xercacourt.Mod;

public final class Items {
    public static final Item GAVEL = new ItemGavel(properties("gavel"));
    public static final Item ATTORNEY_BADGE = new ItemBadge(properties("attorney_badge"));
    public static final Item PROSECUTOR_BADGE = new ItemBadge(properties("prosecutor_badge"));

    private Items() {
    }

    private static Item.Properties properties(String path) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Mod.id(path)));
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, Mod.id("gavel"), GAVEL);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("attorney_badge"), ATTORNEY_BADGE);
        Registry.register(BuiltInRegistries.ITEM, Mod.id("prosecutor_badge"), PROSECUTOR_BADGE);
    }
}
