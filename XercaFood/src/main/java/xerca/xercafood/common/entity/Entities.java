package xerca.xercafood.common.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xerca.xercafood.common.XercaFood;

public final class Entities {
    public static final EntityType<EntityTomato> TOMATO = FabricEntityTypeBuilder.<EntityTomato>create(MobCategory.MISC, EntityTomato::new)
            .dimensions(new EntityDimensions(0.25f, 0.25f, true)).trackedUpdateRate(10).build();

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(XercaFood.MODID, "tomato"), TOMATO);
    }
}
