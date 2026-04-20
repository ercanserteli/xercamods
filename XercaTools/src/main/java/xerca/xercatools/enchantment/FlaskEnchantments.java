package xerca.xercatools.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import xerca.xercatools.Mod;

public final class FlaskEnchantments {
    public static final ResourceKey<Enchantment> CAPACITY = key("enchantment_capacity");
    public static final ResourceKey<Enchantment> RANGE = key("enchantment_range");
    public static final ResourceKey<Enchantment> CHUG = key("enchantment_chug");

    private FlaskEnchantments() {
    }

    private static ResourceKey<Enchantment> key(String path) {
        ResourceLocation id = Mod.id(path);
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }

    public static Holder<Enchantment> capacity(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(CAPACITY);
    }

    public static Holder<Enchantment> range(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(RANGE);
    }

    public static Holder<Enchantment> chug(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(CHUG);
    }
}
