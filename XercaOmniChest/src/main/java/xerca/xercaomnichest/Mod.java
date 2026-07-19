package xerca.xercaomnichest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercaomnichest.block.Blocks;
import xerca.xercaomnichest.block_entity.BlockEntities;
import xerca.xercaomnichest.data.OmniChestSavedData;
import xerca.xercaomnichest.item.Items;

public final class Mod implements ModInitializer {
    public static final String MOD_ID = "xercaomnichest";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceKey<Item> itemKey(String path) {
        return ResourceKey.create(Registries.ITEM, id(path));
    }

    public static ResourceKey<Block> blockKey(String path) {
        return ResourceKey.create(Registries.BLOCK, id(path));
    }

    @Override
    public void onInitialize() {
        Blocks.registerBlocks();
        BlockEntities.registerBlockEntities();
        Items.registerItems();
        ServerLifecycleEvents.SERVER_STARTED.register(OmniChestSavedData::migrateLegacyData);
        LOGGER.info(MOD_ID + " initialized");
    }
}
