package xerca.xercacushion;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercacushion.block.Blocks;
import xerca.xercacushion.entity.EntityCushion;
import xerca.xercacushion.item.Items;

public final class Mod implements ModInitializer {
    public static final String MOD_ID = "xercacushion";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static final ResourceLocation CUSHION_ID = id("cushion");
    private static final ResourceKey<EntityType<?>> CUSHION_KEY = ResourceKey.create(Registries.ENTITY_TYPE, CUSHION_ID);
    public static final EntityType<EntityCushion> CUSHION = EntityType.Builder.<EntityCushion>of(EntityCushion::new, MobCategory.MISC)
            .sized(1.0F, 0.125F)
            .clientTrackingRange(2)
            .updateInterval(10)
            .build(CUSHION_KEY);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Blocks.register();
        Items.register();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, CUSHION_ID, CUSHION);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> {
            for (var item : Items.all()) {
                entries.accept(item);
            }
        });
        LOGGER.info(MOD_ID + " initialized");
    }
}
