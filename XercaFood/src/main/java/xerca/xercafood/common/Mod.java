package xerca.xercafood.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.block_entity.BlockEntities;
import xerca.xercafood.common.entity.Entities;
import xerca.xercafood.common.entity.EntityTomato;
import xerca.xercafood.common.item.Items;

public class Mod implements ModInitializer {
    public static final String MOD_ID = "xercafood";
    public static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceKey<LootTable> GRASS_LOOT_TABLE_ID = net.minecraft.world.level.block.Blocks.SHORT_GRASS.getLootTable().orElseThrow();

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
        // Making tomato dispensable by dispenser
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
        Blocks.registerBlocks();
        BlockEntities.registerBlockEntities();
        Items.registerItems();
        Items.registerRecipes();
        SoundEvents.registerSoundEvents();
        Entities.registerEntities();
        registerSeedDrops();
        registerDonerEvent();
        LOGGER.info(MOD_ID + " initialized");
    }

    private void registerDonerEvent() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack heldItem = player.getItemInHand(hand);
            if (world.getBlockState(hitResult.getBlockPos()).getBlock() == net.minecraft.world.level.block.Blocks.IRON_BARS
                    && heldItem.getItem() == net.minecraft.world.item.Items.MUTTON) {
                world.setBlockAndUpdate(hitResult.getBlockPos(), Blocks.BLOCK_DONER.defaultBlockState());
                heldItem.shrink(1);
                world.playSound(null, hitResult.getBlockPos(), net.minecraft.sounds.SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.8f, 0.9f + world.getRandom().nextFloat() * 0.1f);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }

    private void registerSeedDrops() {
        LootTableEvents.MODIFY.register((id, tableBuilder, source, registries) -> {
            if (source.isBuiltin() && GRASS_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolRice = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.066f))
                        .add(LootItem.lootTableItem(Items.RICE_SEEDS));
                LootPool.Builder poolTomato = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.066f))
                        .add(LootItem.lootTableItem(Items.TOMATO_SEEDS));
                LootPool.Builder poolTea = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.066f))
                        .add(LootItem.lootTableItem(Items.TEA_SEEDS));

                tableBuilder.withPool(poolRice).withPool(poolTomato).withPool(poolTea);
            }
        });
    }
}
