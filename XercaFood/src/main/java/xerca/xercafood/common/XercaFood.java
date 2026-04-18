package xerca.xercafood.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercafood.common.block.Blocks;
import xerca.xercafood.common.block_entity.BlockEntities;
import xerca.xercafood.common.entity.Entities;
import xerca.xercafood.common.entity.EntityTomato;
import xerca.xercafood.common.item.Items;

import javax.annotation.Nonnull;
import java.util.List;

public class XercaFood implements ModInitializer {
    public static final String MODID = "xercafood";
    public static final String NAME = "Xerca Food";

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceKey<LootTable> GRASS_LOOT_TABLE_ID = net.minecraft.world.level.block.Blocks.SHORT_GRASS.getLootTable();

    public XercaFood() {

    }
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static boolean isColaExtract(ItemStack stack) {
        return stack.is(Items.COLA_EXTRACT);
    }

    @Override
    public void onInitialize() {
        // Making tomato dispensable by dispenser
        DispenserBlock.registerBehavior(Items.ITEM_TOMATO, new DefaultDispenseItemBehavior() {
            @Nonnull
            @Override
            protected ItemStack execute(@Nonnull BlockSource source, @Nonnull ItemStack stackIn) {
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
        registerTradeOffers();
    }

    private void registerTradeOffers() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, (a) -> a.addAll(List.of(
                new VillagerTrades.EmeraldForItems(Items.ITEM_RICE_SEEDS, 24, 16, 2),
                new VillagerTrades.EmeraldForItems(Items.ITEM_TOMATO, 22, 16, 2),
                new VillagerTrades.EmeraldForItems(Items.ITEM_TEA_SEEDS, 18, 16, 2))
        ));
    }

    private void registerDonerEvent() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack heldItem = player.getItemInHand(hand);
            if (world.getBlockState(hitResult.getBlockPos()).getBlock() == net.minecraft.world.level.block.Blocks.IRON_BARS
                    && heldItem.getItem() == net.minecraft.world.item.Items.MUTTON) {
                world.setBlockAndUpdate(hitResult.getBlockPos(), Blocks.BLOCK_DONER.defaultBlockState());
                heldItem.shrink(1);
                world.playSound(null, hitResult.getBlockPos(), net.minecraft.sounds.SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 0.8f, 0.9f + world.random.nextFloat() * 0.1f);
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
                        .with(LootItem.lootTableItem(Items.ITEM_RICE_SEEDS).build());
                LootPool.Builder poolTomato = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.066f))
                        .with(LootItem.lootTableItem(Items.ITEM_TEA_SEEDS).build());
                LootPool.Builder poolTea = LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.066f))
                        .with(LootItem.lootTableItem(Items.ITEM_TOMATO_SEEDS).build());

                tableBuilder.withPool(poolRice).withPool(poolTomato).withPool(poolTea);
            }
        });
    }
}
