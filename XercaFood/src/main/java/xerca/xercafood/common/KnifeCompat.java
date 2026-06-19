package xerca.xercafood.common;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercafood.common.item.Items;

public final class KnifeCompat {
    private static final ResourceLocation XERCATOOLS_KNIFE_ID = ResourceLocation.fromNamespaceAndPath("xercatools", "knife");

    private KnifeCompat() {
    }

    public static boolean useToolsKnife() {
        return FabricLoader.getInstance().isModLoaded("xercatools") && BuiltInRegistries.ITEM.containsKey(XERCATOOLS_KNIFE_ID);
    }

    public static Item getKnifeItem() {
        if (useToolsKnife()) {
            return BuiltInRegistries.ITEM.getValue(XERCATOOLS_KNIFE_ID);
        }
        return Items.requireLocalKnife();
    }

    public static boolean isKnife(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == getKnifeItem();
    }
}
