package xerca.xercafood.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercafood.common.Mod;

public final class Entities {
    private Entities() {
    }

    public static EntityType<EntityTomato> TOMATO;

    public static void registerEntities(RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        TOMATO = EntityType.Builder.<EntityTomato>of(EntityTomato::new, MobCategory.MISC)
                .sized(0.25f, 0.25f).updateInterval(10)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, Mod.id("tomato")));
        helper.register(Mod.id("tomato"), TOMATO);
    }
}
