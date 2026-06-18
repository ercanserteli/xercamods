package xerca.xercaomnichest;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercaomnichest.block.Blocks;
import xerca.xercaomnichest.block_entity.BlockEntities;
import xerca.xercaomnichest.item.Items;

public final class Mod implements ModInitializer {
    public static final String MOD_ID = "xercaomnichest";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Blocks.registerBlocks();
        BlockEntities.registerBlockEntities();
        Items.registerItems();
        LOGGER.info(MOD_ID + " initialized");
    }
}
