package xerca.xercamod.common.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.BlockCushion;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.item.ItemCushion;
import xerca.xercamod.common.item.Items;

@ObjectHolder(XercaMod.MODID)
public final class Entities {
    public static final EntityType<EntityConfettiBall> CONFETTI_BALL = null;
    public static final EntityType<EntityHook> HOOK = null;
    public static final EntityType<EntityTomato> TOMATO = null;
    public static final EntityType<EntityCushion> CUSHION = null;
    public static final EntityType<EntityHealthOrb> HEALTH_ORB = null;


    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            EntityCushion.blockCushions = new BlockCushion[]{
                    Blocks.BLACK_CUSHION,
                    Blocks.BLUE_CUSHION,
                    Blocks.BROWN_CUSHION,
                    Blocks.CYAN_CUSHION,
                    Blocks.GRAY_CUSHION,
                    Blocks.GREEN_CUSHION,
                    Blocks.LIGHT_BLUE_CUSHION,
                    Blocks.LIGHT_GRAY_CUSHION,
                    Blocks.LIME_CUSHION,
                    Blocks.MAGENTA_CUSHION,
                    Blocks.ORANGE_CUSHION,
                    Blocks.PINK_CUSHION,
                    Blocks.PURPLE_CUSHION,
                    Blocks.RED_CUSHION,
                    Blocks.WHITE_CUSHION,
                    Blocks.YELLOW_CUSHION
            };
            EntityCushion.itemCushions = new ItemCushion[]{
                    Items.BLACK_CUSHION,
                    Items.BLUE_CUSHION,
                    Items.BROWN_CUSHION,
                    Items.CYAN_CUSHION,
                    Items.GRAY_CUSHION,
                    Items.GREEN_CUSHION,
                    Items.LIGHT_BLUE_CUSHION,
                    Items.LIGHT_GRAY_CUSHION,
                    Items.LIME_CUSHION,
                    Items.MAGENTA_CUSHION,
                    Items.ORANGE_CUSHION,
                    Items.PINK_CUSHION,
                    Items.PURPLE_CUSHION,
                    Items.RED_CUSHION,
                    Items.WHITE_CUSHION,
                    Items.YELLOW_CUSHION
            };

            final EntityType<EntityConfettiBall> confettiBall = build("confetti_ball",
                    EntityType.Builder.<EntityConfettiBall>of((EntityConfettiBall::new), MobCategory.MISC).setCustomClientFactory(EntityConfettiBall::new).sized(0.25f, 0.25f).setUpdateInterval(10)
            );
            final EntityType<EntityHook> hook = build("hook",
                    EntityType.Builder.<EntityHook>of((EntityHook::new), MobCategory.MISC).setCustomClientFactory(EntityHook::new).sized(0.25f, 0.25f).setUpdateInterval(4).setTrackingRange(10)
            );
            final EntityType<EntityTomato> tomato = build("tomato",
                    EntityType.Builder.<EntityTomato>of((EntityTomato::new), MobCategory.MISC).setCustomClientFactory(EntityTomato::new).sized(0.25f, 0.25f).setUpdateInterval(10)
            );
            final EntityType<EntityCushion> cushion = build("cushion",
                    EntityType.Builder.<EntityCushion>of((EntityCushion::new), MobCategory.MISC).setCustomClientFactory(EntityCushion::new).sized(1.f, 0.125f)
            );
            final EntityType<EntityHealthOrb> healthOrb = build("health_orb",
                    EntityType.Builder.<EntityHealthOrb>of((EntityHealthOrb::new), MobCategory.MISC).setCustomClientFactory(EntityHealthOrb::new).sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20)
            );

            event.getRegistry().register(confettiBall);
            event.getRegistry().register(hook);
            event.getRegistry().register(tomato);
            event.getRegistry().register(cushion);
            event.getRegistry().register(healthOrb);

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
        final ResourceLocation registryName = new ResourceLocation(XercaMod.MODID, name);

        final EntityType<T> entityType = builder
                .build(registryName.toString());

        entityType.setRegistryName(registryName);

        return entityType;
    }
}
