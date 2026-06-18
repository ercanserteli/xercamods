package xerca.xercatools.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import xerca.xercatools.Mod;

public final class GrabHookEnchantments {
    public static final ResourceKey<Enchantment> GRAPPLING = key("enchantment_grappling");
    public static final ResourceKey<Enchantment> TURBO_GRAB = key("enchantment_turbo_grab");
    public static final ResourceKey<Enchantment> GENTLE_GRAB = key("enchantment_gentle_grab");

    private GrabHookEnchantments() {
    }

    private static ResourceKey<Enchantment> key(String path) {
        ResourceLocation id = Mod.id(path);
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }

    public static Holder<Enchantment> grapplingEnchantment(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(GRAPPLING);
    }

    public static Holder<Enchantment> grapplingEnchantment(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(GRAPPLING);
    }

    public static Holder<Enchantment> turboGrab(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(TURBO_GRAB);
    }

    public static Holder<Enchantment> gentleGrab(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(GENTLE_GRAB);
    }
}
