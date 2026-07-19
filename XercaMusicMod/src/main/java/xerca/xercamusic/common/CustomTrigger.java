package xerca.xercamusic.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class CustomTrigger extends SimpleCriterionTrigger<CustomTrigger.TriggerInstance> {
    CustomTrigger() {
        super();
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, p -> true);
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(Codec.optionalField("player", EntityPredicate.ADVANCEMENT_CODEC, false)
                        .forGetter(TriggerInstance::player)
                ).apply(instance, TriggerInstance::new)
        );
    }
}