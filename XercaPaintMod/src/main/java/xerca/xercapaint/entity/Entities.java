package xerca.xercapaint.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercapaint.Mod;

public class Entities {
    private Entities() {
    }

    public static final Identifier CANVAS_ID = Mod.id("canvas");
    public static final Identifier EASEL_ID = Mod.id("easel");
    private static final ResourceKey<EntityType<?>> CANVAS_KEY = ResourceKey.create(Registries.ENTITY_TYPE, CANVAS_ID);
    private static final ResourceKey<EntityType<?>> EASEL_KEY = ResourceKey.create(Registries.ENTITY_TYPE, EASEL_ID);

    // Entity types are built during registration: EntityType.Builder.build writes to a frozen registry.
    public static EntityType<EntityCanvas> CANVAS;
    public static EntityType<EntityEasel> EASEL;

    public static void registerEntities(RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        CANVAS = EntityType.Builder.<EntityCanvas>of(EntityCanvas::new, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .eyeHeight(0.25f)
                .updateInterval(Integer.MAX_VALUE)
                .build(CANVAS_KEY);
        EASEL = EntityType.Builder.<EntityEasel>of(EntityEasel::new, MobCategory.MISC)
                .sized(0.8f, 1.975f)
                .eyeHeight(1.8f)
                .build(EASEL_KEY);
        helper.register(CANVAS_ID, CANVAS);
        helper.register(EASEL_ID, EASEL);
    }
}
