package xerca.xercapaint.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercapaint.common.XercaPaint;

@ObjectHolder(XercaPaint.MODID)
public class Entities {
    public static final EntityType<EntityCanvas> CANVAS = null;

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            final EntityType<EntityCanvas> canvasEntityType = build("canvas",
                    EntityType.Builder.<EntityCanvas>of((EntityCanvas::new), MobCategory.MISC).
                            setCustomClientFactory(EntityCanvas::new).sized(0.5f, 0.5f).setUpdateInterval(2147483647).setTrackingRange(10).setShouldReceiveVelocityUpdates(false)
            );
            event.getRegistry().register(canvasEntityType);
        }
    }


    /**
     * Build an {@link EntityType} from a {@link EntityType.Builder} using the specified name.
     *
     * @param name    The entity type name
     * @param builder The entity type builder to build
     * @return The built entity type
     */
    private static <T extends Entity> EntityType<T> build(final String name, final EntityType.Builder<T> builder) {
        final ResourceLocation registryName = new ResourceLocation(XercaPaint.MODID, name);

        final EntityType<T> entityType = builder
                .build(registryName.toString());

        entityType.setRegistryName(registryName);

        return entityType;
    }
}
