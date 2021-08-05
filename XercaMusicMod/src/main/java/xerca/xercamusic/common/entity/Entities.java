package xerca.xercamusic.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamusic.common.XercaMusic;

import static xerca.xercamusic.common.XercaMusic.Null;

@ObjectHolder(XercaMusic.MODID)
public class Entities {
    public static final EntityType<EntityMusicSpirit> MUSIC_SPIRIT = Null();

    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            final EntityType<EntityMusicSpirit> musicSpiritEntityType = build("music_spirit",
                    EntityType.Builder.<EntityMusicSpirit>of((EntityMusicSpirit::new), MobCategory.MISC).setCustomClientFactory(EntityMusicSpirit::new).sized(0.25f, 0.25f).setUpdateInterval(10)
            );
            event.getRegistry().register(musicSpiritEntityType);
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
        final ResourceLocation registryName = new ResourceLocation(XercaMusic.MODID, name);

        final EntityType<T> entityType = builder
                .build(registryName.toString());

        entityType.setRegistryName(registryName);

        return entityType;
    }
}
