package xerca.xercatools.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import xerca.xercatools.Mod;

public final class ScytheEnchantments {
    public static final ResourceKey<Enchantment> GUILLOTINE = key("enchantment_guillotine");
    public static final ResourceKey<Enchantment> DEVOUR = key("enchantment_devour");

    private ScytheEnchantments() {
    }

    private static ResourceKey<Enchantment> key(String path) {
        ResourceLocation id = Mod.id(path);
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }

    public static Holder<Enchantment> guillotine(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(GUILLOTINE);
    }

    public static Holder<Enchantment> guillotine(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(GUILLOTINE);
    }

    public static Holder<Enchantment> devour(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(DEVOUR);
    }

    public static Holder<Enchantment> devour(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(DEVOUR);
    }
}
