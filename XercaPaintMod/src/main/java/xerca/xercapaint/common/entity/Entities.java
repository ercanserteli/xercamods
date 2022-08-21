package xerca.xercapaint.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xerca.xercapaint.common.XercaPaint;
public class Entities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, XercaPaint.MODID);
    public static final RegistryObject<EntityType<EntityCanvas>> CANVAS = ENTITIES.register("canvas", () -> EntityType.Builder.<EntityCanvas>of(EntityCanvas::new, MobCategory.MISC)
            .setCustomClientFactory(EntityCanvas::new)
            .sized(0.5f, 0.5f)
            .setUpdateInterval(2147483647)
            .setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(false)
            .build("canvas")
    );
    public static final RegistryObject<EntityType<EntityEasel>> EASEL = ENTITIES.register("easel", () -> EntityType.Builder.<EntityEasel>of(EntityEasel::new, MobCategory.MISC)
            .setCustomClientFactory(EntityEasel::new)
            .sized(0.8f, 1.975F)
            .clientTrackingRange(10)
            .build("easel")
    );
}
