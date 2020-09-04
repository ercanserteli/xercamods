package xerca.xercamod.common;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SeedLootModifier extends LootModifier {
    private final Item itemSeed;
    private final boolean isFood;

    protected SeedLootModifier(ILootCondition[] conditionsIn, Item itemSeed, boolean isFood) {
        super(conditionsIn);
        this.itemSeed = itemSeed;
        this.isFood = isFood;
    }

    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if((isFood && !Config.isFoodEnabled()) || (!isFood && !Config.isTeaEnabled())){
            return generatedLoot;
        }
        generatedLoot.add(new ItemStack(itemSeed));
        return generatedLoot;
    }

    static class Serializer extends GlobalLootModifierSerializer<SeedLootModifier> {
        @Override
        public SeedLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            Item seed = ForgeRegistries.ITEMS.getValue(new ResourceLocation((JSONUtils.getString(object, "seedItem"))));
            boolean isFood = JSONUtils.getBoolean(object, "isFood");
            return new SeedLootModifier(conditionsIn, seed, isFood);
        }
    }
}
