package xerca.xercacushion;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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

    public static final EntityType<EntityCushion> CUSHION = EntityType.Builder.<EntityCushion>of(EntityCushion::new, MobCategory.MISC)
            .sized(1.0F, 0.125F)
            .clientTrackingRange(2)
            .updateInterval(10)
            .build();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Blocks.register();
        Items.register();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("cushion"), CUSHION);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> {
            for (var item : Items.all()) {
                entries.accept(item);
            }
        });
    }
}
