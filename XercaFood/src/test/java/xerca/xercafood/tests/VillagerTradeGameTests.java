package xerca.xercafood.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.List;

public final class VillagerTradeGameTests {
    private static final List<String> FARMER_TRADES = List.of(
            "farmer/1/rice_seeds_emerald",
            "farmer/1/tomato_emerald",
            "farmer/1/tea_seeds_emerald"
    );

    @GameTest
    public void farmerLevelOneTagContainsFoodTrades(GameTestHelper helper) {
        HolderLookup.RegistryLookup<VillagerTrade> trades =
                helper.getLevel().registryAccess().lookupOrThrow(Registries.VILLAGER_TRADE);
        TagKey<VillagerTrade> levelOne = TagKey.create(Registries.VILLAGER_TRADE,
                Identifier.withDefaultNamespace("farmer/level_1"));
        List<Identifier> tagged = trades.getOrThrow(levelOne).stream()
                .map(holder -> holder.unwrapKey().orElseThrow().identifier())
                .toList();

        for (String path : FARMER_TRADES) {
            Identifier id = Identifier.fromNamespaceAndPath("xercafood", path);
            helper.assertTrue(trades.get(ResourceKey.create(Registries.VILLAGER_TRADE, id)).isPresent(),
                    Component.literal("Missing villager trade definition: " + id));
            helper.assertTrue(tagged.contains(id),
                    Component.literal("Farmer level_1 trade tag does not include " + id));
        }
        helper.succeed();
    }
}
