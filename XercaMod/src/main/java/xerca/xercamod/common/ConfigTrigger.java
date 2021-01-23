package xerca.xercamod.common;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class ConfigTrigger extends AbstractCriterionTrigger<ConfigTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(XercaMod.MODID, "config_check");

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new ConfigTrigger.Instance(entityPredicate, ConfigPredicate.deserialize(json));
    }

    public void test(ServerPlayerEntity player) {
        this.trigger(player);
    }

    private void trigger(ServerPlayerEntity player) {
        this.triggerListeners(player, Instance::test);
    }

    public static class Instance extends CriterionInstance {
        private final ConfigPredicate predicate;

        public Instance(EntityPredicate.AndPredicate player, ConfigPredicate predicate) {
            super(ConfigTrigger.ID, player);
            this.predicate = predicate;
        }

        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("pred", predicate.serialize());
            return jsonobject;
        }

        public boolean test() {
            return predicate.test();
        }
    }
}
