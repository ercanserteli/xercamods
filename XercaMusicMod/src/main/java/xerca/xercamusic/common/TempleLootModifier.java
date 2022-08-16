package xerca.xercamusic.common;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.item.Items;

import java.util.List;

public class TempleLootModifier extends LootModifier {

    protected TempleLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(Items.GOD));
        return generatedLoot;
    }

    static class Serializer extends GlobalLootModifierSerializer<TempleLootModifier> {
        @Override
        public TempleLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            return new TempleLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(TempleLootModifier instance) {
            return new JsonObject();
        }
    }
}
