package xerca.xercablocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercablocks.block.Blocks;
import xerca.xercablocks.block_entity.BlockEntities;
import xerca.xercablocks.item.Items;
import xerca.xercablocks.menu.Menus;
import xerca.xercablocks.recipe.Recipes;

@net.neoforged.fml.common.Mod(Mod.MOD_ID)
public final class Mod {
    public static final String MOD_ID = "xercablocks";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(Items::addCreative);
        LOGGER.info("{} initialized", MOD_ID);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private void onRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, Blocks::register);
        event.register(Registries.ITEM, Items::register);
        event.register(Registries.BLOCK_ENTITY_TYPE, BlockEntities::register);
        event.register(Registries.MENU, Menus::register);
        event.register(Registries.RECIPE_TYPE, Recipes::registerTypes);
        event.register(Registries.RECIPE_SERIALIZER, Recipes::registerSerializers);
    }
}
