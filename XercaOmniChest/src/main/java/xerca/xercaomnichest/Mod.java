package xerca.xercaomnichest;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercaomnichest.block.Blocks;
import xerca.xercaomnichest.block_entity.BlockEntities;
import xerca.xercaomnichest.data.OmniChestSavedData;
import xerca.xercaomnichest.item.Items;

@net.neoforged.fml.common.Mod(Mod.MOD_ID)
public final class Mod {
    public static final String MOD_ID = "xercaomnichest";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        LOGGER.info("{} initialized", MOD_ID);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceKey<Item> itemKey(String path) {
        return ResourceKey.create(Registries.ITEM, id(path));
    }

    public static ResourceKey<Block> blockKey(String path) {
        return ResourceKey.create(Registries.BLOCK, id(path));
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, helper -> helper.register(id("omni_chest"), Blocks.OMNI_CHEST));
        event.register(Registries.ITEM, helper -> helper.register(id("omni_chest"), Items.OMNI_CHEST));
        event.register(Registries.BLOCK_ENTITY_TYPE, helper -> helper.register(id("omni_chest"), BlockEntities.OMNI_CHEST));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Items.OMNI_CHEST);
        }
    }

    private void onServerStarted(ServerStartedEvent event) {
        OmniChestSavedData.migrateLegacyData(event.getServer());
    }
}
