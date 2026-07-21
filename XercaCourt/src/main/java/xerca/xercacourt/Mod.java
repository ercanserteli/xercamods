package xerca.xercacourt;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercacourt.item.Items;

@net.neoforged.fml.common.Mod(Mod.MOD_ID)
public class Mod {
    public static final String MOD_ID = "xercacourt";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::addCreative);
        LOGGER.info("{} initialized", MOD_ID);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, SoundEvents::register);
        event.register(Registries.ITEM, Items::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(Items.GAVEL);
            event.accept(Items.ATTORNEY_BADGE);
            event.accept(Items.PROSECUTOR_BADGE);
        }
    }
}
