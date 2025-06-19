package xerca.xercapaint.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xerca.xercapaint.Mod;

public class Entities {
    public static final ResourceLocation CANVAS_ID = Mod.id("canvas");
    public static final ResourceLocation EASEL_ID = Mod.id("easel");

    private static final ResourceKey<EntityType<?>> CANVAS_KEY = ResourceKey.create(Registries.ENTITY_TYPE, CANVAS_ID);
    private static final ResourceKey<EntityType<?>> EASEL_KEY = ResourceKey.create(Registries.ENTITY_TYPE, EASEL_ID);

    public static final EntityType<EntityCanvas> CANVAS = FabricEntityTypeBuilder.<EntityCanvas>create(MobCategory.MISC, EntityCanvas::new)
            .dimensions(new EntityDimensions(0.5f, 0.5f, 0.5f, EntityAttachments.createDefault(0.5f, 0.5f), true)).trackedUpdateRate(2147483647).build(CANVAS_KEY);

    public static final EntityType<EntityEasel> EASEL = FabricEntityTypeBuilder.<EntityEasel>create(MobCategory.MISC, EntityEasel::new)
            .dimensions(new EntityDimensions(0.8f, 1.975F, 1.8F, EntityAttachments.createDefault(0.5f, 0.5f),true)).build(EASEL_KEY);

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, CANVAS_ID, CANVAS);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, EASEL_ID, EASEL);
    }
}
