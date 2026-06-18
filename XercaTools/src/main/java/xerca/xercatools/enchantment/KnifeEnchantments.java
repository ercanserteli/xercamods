package xerca.xercatools.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import xerca.xercatools.Mod;

public final class KnifeEnchantments {
    public static final ResourceKey<Enchantment> POISON = key("enchantment_poison");
    public static final ResourceKey<Enchantment> STEALTH = key("enchantment_stealth");

    private KnifeEnchantments() {
    }

    private static ResourceKey<Enchantment> key(String path) {
        ResourceLocation id = Mod.id(path);
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }

    public static Holder<Enchantment> poisonEnchantment(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(POISON);
    }

    public static Holder<Enchantment> stealthEnchantment(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(STEALTH);
    }

}
