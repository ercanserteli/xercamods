package xerca.xercamod.common;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SeedLootModifier extends LootModifier {
    private final Item itemSeed;
    private final boolean isFood;

    protected SeedLootModifier(LootItemCondition[] conditionsIn, Item itemSeed, boolean isFood) {
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
        public SeedLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            Item seed = ForgeRegistries.ITEMS.getValue(new ResourceLocation((GsonHelper.getAsString(object, "seedItem"))));
            boolean isFood = GsonHelper.getAsBoolean(object, "isFood");
            return new SeedLootModifier(conditionsIn, seed, isFood);
        }

        @Override
        public JsonObject write(SeedLootModifier instance) {
            JsonObject json = new JsonObject();
            ResourceLocation itemRL = Registry.ITEM.getKey(instance.itemSeed);
            json.addProperty("seedItem", itemRL.toString());
            json.addProperty("isFood", instance.isFood);
            return json;
        }
    }
}
