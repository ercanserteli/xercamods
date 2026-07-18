package xerca.xercacourt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercacourt.item.Items;

public class Mod implements ModInitializer {
    public static final String MOD_ID = "xercacourt";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        SoundEvents.register();
        Items.register();

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(Items.GAVEL);
            entries.accept(Items.ATTORNEY_BADGE);
            entries.accept(Items.PROSECUTOR_BADGE);
        });
        LOGGER.info(MOD_ID + " initialized");
    }
}
