package xerca.xercapaint.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xerca.xercapaint.Mod;

public class Entities {
    public static final EntityType<EntityCanvas> CANVAS = FabricEntityTypeBuilder.<EntityCanvas>create(MobCategory.MISC, EntityCanvas::new)
            .dimensions(new EntityDimensions(0.5f, 0.5f, 0.5f, EntityAttachments.createDefault(0.5f, 0.5f), true)).trackedUpdateRate(2147483647).build();

    public static final EntityType<EntityEasel> EASEL = FabricEntityTypeBuilder.<EntityEasel>create(MobCategory.MISC, EntityEasel::new)
            .dimensions(new EntityDimensions(0.8f, 1.975F, 1.8F, EntityAttachments.createDefault(0.5f, 0.5f),true)).build();

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, Mod.id("canvas"), CANVAS);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, Mod.id("easel"), EASEL);
    }
}
