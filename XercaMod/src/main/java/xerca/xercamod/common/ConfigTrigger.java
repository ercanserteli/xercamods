package xerca.xercamod.common;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConfigTrigger extends SimpleCriterionTrigger<ConfigTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(XercaMod.MODID, "config_check");

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
        return new ConfigTrigger.Instance(entityPredicate, ConfigPredicate.deserialize(json));
    }

    public void test(ServerPlayer player) {
        this.trigger(player);
    }

    private void trigger(ServerPlayer player) {
        this.trigger(player, Instance::test);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final ConfigPredicate predicate;

        public Instance(EntityPredicate.Composite player, ConfigPredicate predicate) {
            super(ConfigTrigger.ID, player);
            this.predicate = predicate;
        }

        public JsonObject serializeToJson(SerializationContext conditions) {
            JsonObject jsonobject = super.serializeToJson(conditions);
            jsonobject.add("pred", predicate.serialize());
            return jsonobject;
        }

        public boolean test() {
            return predicate.test();
        }
    }
}
