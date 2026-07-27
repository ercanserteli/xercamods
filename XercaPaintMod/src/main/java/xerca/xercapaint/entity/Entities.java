package xerca.xercapaint.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercapaint.Mod;

public class Entities {
    private Entities() {
    }

    public static final ResourceLocation CANVAS_ID = Mod.id("canvas");
    public static final ResourceLocation EASEL_ID = Mod.id("easel");

    // EntityType.Builder.build writes to a registry, so build during registration.
    public static EntityType<EntityCanvas> CANVAS;
    public static EntityType<EntityEasel> EASEL;

    public static void registerEntities(RegisterEvent.RegisterHelper<EntityType<?>> helper) {
        CANVAS = EntityType.Builder.<EntityCanvas>of(EntityCanvas::new, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .eyeHeight(0.25f)
                .updateInterval(Integer.MAX_VALUE)
                .build(CANVAS_ID.toString());
        EASEL = EntityType.Builder.<EntityEasel>of(EntityEasel::new, MobCategory.MISC)
                .sized(0.8f, 1.975f)
                .eyeHeight(1.8f)
                .build(EASEL_ID.toString());
        helper.register(CANVAS_ID, CANVAS);
        helper.register(EASEL_ID, EASEL);
    }
}
