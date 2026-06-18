package xerca.xercapaint.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xerca.xercapaint.Mod;

public class Entities {
    private Entities() {
    }

    public static final ResourceLocation CANVAS_ID = Mod.id("canvas");
    public static final ResourceLocation EASEL_ID = Mod.id("easel");
    private static final ResourceKey<EntityType<?>> CANVAS_KEY = ResourceKey.create(Registries.ENTITY_TYPE, CANVAS_ID);
    private static final ResourceKey<EntityType<?>> EASEL_KEY = ResourceKey.create(Registries.ENTITY_TYPE, EASEL_ID);

    public static final EntityType<EntityCanvas> CANVAS = EntityType.Builder.<EntityCanvas>of(EntityCanvas::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .eyeHeight(0.25f)
            .updateInterval(Integer.MAX_VALUE)
            .build(CANVAS_KEY);

    public static final EntityType<EntityEasel> EASEL = EntityType.Builder.<EntityEasel>of(EntityEasel::new, MobCategory.MISC)
            .sized(0.8f, 1.975f)
            .eyeHeight(1.8f)
            .build(EASEL_KEY);

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, CANVAS_ID, CANVAS);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, EASEL_ID, EASEL);
    }
}
