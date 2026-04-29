package xerca.xercablocks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercablocks.block.Blocks;
import xerca.xercablocks.block_entity.BlockEntities;
import xerca.xercablocks.item.Items;
import xerca.xercablocks.menu.Menus;
import xerca.xercablocks.recipe.Recipes;

public final class Mod implements ModInitializer {
    public static final String MOD_ID = "xercablocks";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Blocks.registerBlocks();
        BlockEntities.registerBlockEntities();
        Items.registerItems();
        Menus.register();
        Recipes.register();
        LOGGER.info(MOD_ID + " initialized");
    }
}
