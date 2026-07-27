package xerca.xercacushion;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercacushion.block.Blocks;
import xerca.xercacushion.entity.EntityCushion;
import xerca.xercacushion.item.Items;

@net.neoforged.fml.common.Mod(Mod.MOD_ID)
public final class Mod {
    public static final String MOD_ID = "xercacushion";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static final ResourceLocation CUSHION_ID = id("cushion");
    // Built during registration: NeoForge keeps registries frozen outside RegisterEvent, and
    // EntityType.Builder.build writes to a registry.
    public static EntityType<EntityCushion> CUSHION;

    public Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::addCreative);
        LOGGER.info("{} initialized", MOD_ID);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, Blocks::register);
        event.register(Registries.ITEM, Items::register);
        event.register(Registries.ENTITY_TYPE, helper -> {
            CUSHION = EntityType.Builder.<EntityCushion>of(EntityCushion::new, MobCategory.MISC)
                    .sized(1.0F, 0.125F)
                    .clientTrackingRange(2)
                    .updateInterval(10)
                    .build("cushion");
            helper.register(CUSHION_ID, CUSHION);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            for (var item : Items.all()) {
                event.accept(item);
            }
        }
    }
}
