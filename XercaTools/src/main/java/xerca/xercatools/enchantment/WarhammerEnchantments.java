package xerca.xercatools.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import xerca.xercatools.Mod;

public final class WarhammerEnchantments {
    public static final ResourceKey<Enchantment> MAIM = key("enchantment_maim");
    public static final ResourceKey<Enchantment> QUICK = key("enchantment_quick");
    public static final ResourceKey<Enchantment> QUAKE = key("enchantment_quake");
    public static final ResourceKey<Enchantment> UPPERCUT = key("enchantment_uppercut");
    public static final ResourceKey<Enchantment> DASHING = key("enchantment_dashing");

    private WarhammerEnchantments() {
    }

    private static ResourceKey<Enchantment> key(String path) {
        Identifier id = Mod.id(path);
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }

    public static Holder<Enchantment> maimEnchantment(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(MAIM);
    }

    public static Holder<Enchantment> quickEnchantment(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(QUICK);
    }

    public static Holder<Enchantment> quakeEnchantment(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(QUAKE);
    }

    public static Holder<Enchantment> uppercutEnchantment(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(UPPERCUT);
    }

    public static Holder<Enchantment> dashingEnchantment(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(DASHING);
    }
}
