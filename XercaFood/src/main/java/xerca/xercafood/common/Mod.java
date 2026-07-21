package xerca.xercafood.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.block_entity.BlockEntities;
import xerca.xercafood.common.entity.Entities;
import xerca.xercafood.common.entity.EntityTomato;
import xerca.xercafood.common.item.Items;

@net.neoforged.fml.common.Mod(Mod.MOD_ID)
public class Mod {
    public static final String MOD_ID = "xercafood";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceKey<LootTable> GRASS_LOOT_TABLE_ID = net.minecraft.world.level.block.Blocks.SHORT_GRASS.getLootTable().orElseThrow();

    public Mod(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.addListener(this::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(this::onLootTableLoad);
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
        event.register(Registries.BLOCK, Blocks::register);
        event.register(Registries.ITEM, Items::register);
        event.register(Registries.RECIPE_SERIALIZER, Items::registerRecipes);
        event.register(Registries.SOUND_EVENT, SoundEvents::registerSoundEvents);
        event.register(Registries.ENTITY_TYPE, Entities::registerEntities);
        event.register(Registries.BLOCK_ENTITY_TYPE, BlockEntities::registerBlockEntities);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(Items.TOMATO, new DefaultDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource source, ItemStack stackIn) {
                    Direction direction = source.state().getValue(DispenserBlock.FACING);
                    Position position = DispenserBlock.getDispensePosition(source);
                    Projectile projectile = new EntityTomato(source.level(), position.x(), position.y(), position.z());
                    projectile.shoot(direction.getStepX(), direction.getStepY() + 0.1F, direction.getStepZ(), 1.1F, 6.0F);
                    source.level().addFreshEntity(projectile);
                    stackIn.shrink(1);
                    return stackIn;
                }
            });
            Items.registerCompostables();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        Items.addToCreativeTabs(event);
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (tryCreateDoner(event.getEntity(), event.getLevel(), event.getHand(), event.getPos()) == InteractionResult.SUCCESS) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    public static InteractionResult tryCreateDoner(Player player, Level world, net.minecraft.world.InteractionHand hand, BlockPos pos) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (world.getBlockState(pos).getBlock() == net.minecraft.world.level.block.Blocks.IRON_BARS
                && heldItem.getItem() == net.minecraft.world.item.Items.MUTTON) {
            world.setBlockAndUpdate(pos, Blocks.BLOCK_DONER.defaultBlockState());
            heldItem.shrink(1);
            world.playSound(null, pos, net.minecraft.sounds.SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.8f, 0.9f + world.getRandom().nextFloat() * 0.1f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void onLootTableLoad(LootTableLoadEvent event) {
        if (GRASS_LOOT_TABLE_ID.equals(event.getKey())) {
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.066f))
                    .add(LootItem.lootTableItem(Items.RICE_SEEDS)).build());
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.066f))
                    .add(LootItem.lootTableItem(Items.TOMATO_SEEDS)).build());
            event.getTable().addPool(LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.066f))
                    .add(LootItem.lootTableItem(Items.TEA_SEEDS)).build());
        }
    }
}
