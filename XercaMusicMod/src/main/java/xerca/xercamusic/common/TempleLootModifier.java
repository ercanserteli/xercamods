package xerca.xercamusic.common;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import xerca.xercamusic.common.item.Items;

import java.util.List;

public class TempleLootModifier extends LootModifier {

    protected TempleLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(Items.GOD));
        return generatedLoot;
    }

    static class Serializer extends GlobalLootModifierSerializer<TempleLootModifier> {
        @Override
        public TempleLootModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
            return new TempleLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(TempleLootModifier instance) {
            return new JsonObject();
        }
    }
}
