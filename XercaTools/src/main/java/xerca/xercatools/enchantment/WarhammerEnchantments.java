package xerca.xercatools.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import xerca.xercatools.Mod;

public final class WarhammerEnchantments {
    public static final ResourceKey<Enchantment> HEAVY = key("enchantment_heavy");
    public static final ResourceKey<Enchantment> MAIM = key("enchantment_maim");
    public static final ResourceKey<Enchantment> QUICK = key("enchantment_quick");
    public static final ResourceKey<Enchantment> QUAKE = key("enchantment_quake");
    public static final ResourceKey<Enchantment> UPPERCUT = key("enchantment_uppercut");

    private WarhammerEnchantments() {
    }

    private static ResourceKey<Enchantment> key(String path) {
        ResourceLocation id = Mod.id(path);
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }

    public static Holder<Enchantment> heavy(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(HEAVY);
    }

    public static Holder<Enchantment> maim(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(MAIM);
    }

    public static Holder<Enchantment> quick(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(QUICK);
    }

    public static Holder<Enchantment> quake(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(QUAKE);
    }

    public static Holder<Enchantment> uppercut(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UPPERCUT);
    }
}
