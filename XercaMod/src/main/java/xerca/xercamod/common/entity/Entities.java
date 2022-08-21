package xerca.xercamod.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.BlockCushion;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.item.ItemCushion;
import xerca.xercamod.common.item.Items;

public final class Entities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, XercaMod.MODID);

    public static final RegistryObject<EntityType<EntityConfettiBall>> CONFETTI_BALL = ENTITIES.register("confetti_ball",
            () -> EntityType.Builder.<EntityConfettiBall>of(EntityConfettiBall::new, MobCategory.MISC)
                    .setCustomClientFactory(EntityConfettiBall::new)
                    .sized(0.25f, 0.25f)
                    .setUpdateInterval(10)
                    .build("confetti_ball")
    );
    public static final RegistryObject<EntityType<EntityHook>> HOOK = ENTITIES.register("hook",
            () -> EntityType.Builder.<EntityHook>of((EntityHook::new), MobCategory.MISC)
                    .setCustomClientFactory(EntityHook::new)
                    .sized(0.25f, 0.25f)
                    .setUpdateInterval(4)
                    .setTrackingRange(10)
                    .build("hook")
    );
    public static final RegistryObject<EntityType<EntityTomato>> TOMATO = ENTITIES.register("tomato",
            () -> EntityType.Builder.<EntityTomato>of((EntityTomato::new), MobCategory.MISC)
                    .setCustomClientFactory(EntityTomato::new)
                    .sized(0.25f, 0.25f)
                    .setUpdateInterval(10)
                    .build("tomato")
    );
    public static final RegistryObject<EntityType<EntityCushion>> CUSHION = ENTITIES.register("cushion",
            () -> EntityType.Builder.<EntityCushion>of((EntityCushion::new), MobCategory.MISC)
                    .setCustomClientFactory(EntityCushion::new)
                    .sized(1.f, 0.125f)
                    .build("cushion")
    );
    public static final RegistryObject<EntityType<EntityHealthOrb>> HEALTH_ORB = ENTITIES.register("health_orb",
            () -> EntityType.Builder.<EntityHealthOrb>of((EntityHealthOrb::new), MobCategory.MISC)
                    .setCustomClientFactory(EntityHealthOrb::new)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(6)
                    .updateInterval(20)
                    .build("health_orb")
    );

    public static void setup() {
        EntityCushion.blockCushions = new BlockCushion[]{
                Blocks.BLACK_CUSHION.get(),
                Blocks.BLUE_CUSHION.get(),
                Blocks.BROWN_CUSHION.get(),
                Blocks.CYAN_CUSHION.get(),
                Blocks.GRAY_CUSHION.get(),
                Blocks.GREEN_CUSHION.get(),
                Blocks.LIGHT_BLUE_CUSHION.get(),
                Blocks.LIGHT_GRAY_CUSHION.get(),
                Blocks.LIME_CUSHION.get(),
                Blocks.MAGENTA_CUSHION.get(),
                Blocks.ORANGE_CUSHION.get(),
                Blocks.PINK_CUSHION.get(),
                Blocks.PURPLE_CUSHION.get(),
                Blocks.RED_CUSHION.get(),
                Blocks.WHITE_CUSHION.get(),
                Blocks.YELLOW_CUSHION.get()
        };
        EntityCushion.itemCushions = new ItemCushion[]{
                Items.BLACK_CUSHION.get(),
                Items.BLUE_CUSHION.get(),
                Items.BROWN_CUSHION.get(),
                Items.CYAN_CUSHION.get(),
                Items.GRAY_CUSHION.get(),
                Items.GREEN_CUSHION.get(),
                Items.LIGHT_BLUE_CUSHION.get(),
                Items.LIGHT_GRAY_CUSHION.get(),
                Items.LIME_CUSHION.get(),
                Items.MAGENTA_CUSHION.get(),
                Items.ORANGE_CUSHION.get(),
                Items.PINK_CUSHION.get(),
                Items.PURPLE_CUSHION.get(),
                Items.RED_CUSHION.get(),
                Items.WHITE_CUSHION.get(),
                Items.YELLOW_CUSHION.get()
        };
    }
}
