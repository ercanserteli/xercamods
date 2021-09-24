package xerca.xercamod.common.tile_entity;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xerca.xercamod.common.ContainerCarvingStation;
import xerca.xercamod.common.ContainerFunctionalBookcase;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.Blocks;

@ObjectHolder(XercaMod.MODID)
public class TileEntities {
    public static final BlockEntityType<?> FUNCTIONAL_BOOKCASE = null;
    public static final BlockEntityType<TileEntityDoner> DONER = null;
    public static final BlockEntityType<TileEntityOmniChest> OMNI_CHEST = null;
    public static final MenuType<ContainerFunctionalBookcase> CONTAINER_FUNCTIONAL_BOOKCASE = null;
    public static final MenuType<ContainerCarvingStation> CONTAINER_CARVING_STATION = null;
//    public static final MenuType<ContainerOmniChest> CONTAINER_OMNI_CHEST = null;

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerTileEntities(final RegistryEvent.Register<BlockEntityType<?>> event) {
            event.getRegistry().register(BlockEntityType.Builder.of(TileEntityFunctionalBookcase::new, Blocks.BLOCK_BOOKCASE).build(null).setRegistryName(XercaMod.MODID, "functional_bookcase"));
            event.getRegistry().register(BlockEntityType.Builder.of(TileEntityDoner::new, Blocks.BLOCK_DONER).build(null).setRegistryName(XercaMod.MODID, "doner"));
            event.getRegistry().register(BlockEntityType.Builder.of(TileEntityOmniChest::new, Blocks.OMNI_CHEST).build(null).setRegistryName(XercaMod.MODID, "omni_chest"));
        }

        @SubscribeEvent
        public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create(ContainerFunctionalBookcase::new).setRegistryName("container_functional_bookcase"));
            event.getRegistry().register(IForgeContainerType.create(ContainerCarvingStation::new).setRegistryName("container_carving_station"));
        }
    }
}
