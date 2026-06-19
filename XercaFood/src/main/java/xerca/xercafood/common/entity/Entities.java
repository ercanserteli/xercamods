package xerca.xercafood.common.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xerca.xercafood.common.Mod;

public final class Entities {
    private Entities() {
    }

    public static final EntityType<EntityTomato> TOMATO = EntityType.Builder.<EntityTomato>of(EntityTomato::new, MobCategory.MISC)
            .sized(0.25f, 0.25f).updateInterval(10)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Mod.id("tomato")));

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, Mod.id("tomato"), TOMATO);
    }
}
