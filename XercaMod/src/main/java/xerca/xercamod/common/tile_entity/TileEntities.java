package xerca.xercamod.common.tile_entity;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xerca.xercamod.common.ContainerCarvingStation;
import xerca.xercamod.common.ContainerFunctionalBookcase;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.Blocks;

@SuppressWarnings("ConstantConditions")
public class TileEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, XercaMod.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, XercaMod.MODID);

    public static final RegistryObject<BlockEntityType<?>> FUNCTIONAL_BOOKCASE = BLOCK_ENTITIES.register("functional_bookcase", () -> BlockEntityType.Builder.of(TileEntityFunctionalBookcase::new, Blocks.BLOCK_BOOKCASE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityDoner>> DONER = BLOCK_ENTITIES.register("doner", () -> BlockEntityType.Builder.of(TileEntityDoner::new, Blocks.BLOCK_DONER.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityOmniChest>> OMNI_CHEST = BLOCK_ENTITIES.register("omni_chest", () -> BlockEntityType.Builder.of(TileEntityOmniChest::new, Blocks.OMNI_CHEST.get()).build(null));

    public static final RegistryObject<MenuType<ContainerFunctionalBookcase>> CONTAINER_FUNCTIONAL_BOOKCASE = CONTAINERS.register("container_functional_bookcase", () -> IForgeMenuType.create(ContainerFunctionalBookcase::new));
    public static final RegistryObject<MenuType<ContainerCarvingStation>> CONTAINER_CARVING_STATION = CONTAINERS.register("container_carving_station", () -> IForgeMenuType.create(ContainerCarvingStation::new));
}
