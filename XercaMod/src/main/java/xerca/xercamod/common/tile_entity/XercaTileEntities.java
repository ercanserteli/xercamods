package xerca.xercamod.common.tile_entity;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamod.common.ContainerFunctionalBookcase;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.Blocks;

@ObjectHolder(XercaMod.MODID)
public class XercaTileEntities {
    public static final TileEntityType<?> FUNCTIONAL_BOOKCASE = null;
    public static final TileEntityType<TileEntityDoner> DONER = null;
    public static final ContainerType<ContainerFunctionalBookcase> CONTAINER_FUNCTIONAL_BOOKCASE = null;

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
            event.getRegistry().register(TileEntityType.Builder.create(TileEntityFunctionalBookcase::new, Blocks.BLOCK_BOOKCASE).build(null).setRegistryName(XercaMod.MODID, "functional_bookcase"));
            event.getRegistry().register(TileEntityType.Builder.create(TileEntityDoner::new, Blocks.BLOCK_DONER).build(null).setRegistryName(XercaMod.MODID, "doner"));
        }

        @SubscribeEvent
        public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create(ContainerFunctionalBookcase::new).setRegistryName("container_functional_bookcase"));
        }
    }
}
