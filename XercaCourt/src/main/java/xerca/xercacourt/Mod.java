package xerca.xercacourt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercacourt.item.Items;

public class Mod implements ModInitializer {
    public static final String MOD_ID = "xercacourt";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        SoundEvents.register();
        Items.register();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(Items.GAVEL);
            entries.accept(Items.ATTORNEY_BADGE);
            entries.accept(Items.PROSECUTOR_BADGE);
        });
        LOGGER.info(MOD_ID + " initialized");
    }
}
