package xerca.xercaomnichest.tests;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.BlockOmniChest;
import xerca.xercaomnichest.block.Blocks;
import xerca.xercaomnichest.block_entity.BlockEntityOmniChest;
import xerca.xercaomnichest.data.OmniChestInventory;
import xerca.xercaomnichest.data.OmniChestSavedData;
import xerca.xercaomnichest.item.Items;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public final class OmniChestGameTests {

    private static void assertTrue(GameTestHelper helper, boolean condition, String message) {
        helper.assertTrue(condition, net.minecraft.network.chat.Component.literal(message));
    }

    private static Identifier recipeId(String path) {
        return Mod.id(path);
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, Identifier recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().recipeAccess().byKey(
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, recipeId));
        assertTrue(helper, recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        assertTrue(helper, recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        assert recipe instanceof CraftingRecipe;
        return (CraftingRecipe) recipe;
    }

    private static BlockEntityOmniChest requireOmniChest(GameTestHelper helper, BlockPos pos, String message) {
        if (helper.getLevel().getBlockEntity(pos) instanceof BlockEntityOmniChest chest) {
            return chest;
        }
        assertTrue(helper, false, message);
        throw new IllegalStateException("Unreachable after GameTest assertion failure");
    }

    private static BlockHitResult hitTopOf(BlockPos pos) {
        return new BlockHitResult(Vec3.atCenterOf(pos), net.minecraft.core.Direction.UP, pos, false);
    }

    private static InteractionResult invokeUseWithoutItem(GameTestHelper helper, BlockPos pos, Player player) {
        BlockState state = helper.getLevel().getBlockState(pos);
        try {
            java.lang.reflect.Method method = net.minecraft.world.level.block.state.BlockBehaviour.class.getDeclaredMethod(
                    "useWithoutItem",
                    BlockState.class,
                    net.minecraft.world.level.Level.class,
                    BlockPos.class,
                    Player.class,
                    BlockHitResult.class
            );
            method.setAccessible(true);
            return (InteractionResult) method.invoke(state.getBlock(), state, helper.getLevel(), pos, player, hitTopOf(pos));
        } catch (Exception e) {
            helper.fail(net.minecraft.network.chat.Component.literal("Failed to invoke Omni Chest useWithoutItem: " + e.getMessage()));
            return InteractionResult.FAIL;
        }
    }

    private static InteractionResult invokeUseItemOn(GameTestHelper helper, BlockPos pos, Player player, ItemStack stack) {
        BlockState state = helper.getLevel().getBlockState(pos);
        try {
            java.lang.reflect.Method method = net.minecraft.world.level.block.state.BlockBehaviour.class.getDeclaredMethod(
                    "useItemOn",
                    ItemStack.class,
                    BlockState.class,
                    net.minecraft.world.level.Level.class,
                    BlockPos.class,
                    Player.class,
                    InteractionHand.class,
                    BlockHitResult.class
            );
            method.setAccessible(true);
            return (InteractionResult) method.invoke(state.getBlock(), stack, state, helper.getLevel(), pos, player, InteractionHand.MAIN_HAND, hitTopOf(pos));
        } catch (Exception e) {
            helper.fail(net.minecraft.network.chat.Component.literal("Failed to invoke Omni Chest useItemOn: " + e.getMessage()));
            return InteractionResult.FAIL;
        }
    }

    public void omniChestRecipeCraftsFromAmethystEyesAndEnderChest(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("omni_chest"));
        CraftingInput grid = CraftingInput.of(3, 3, java.util.List.of(
                new ItemStack(net.minecraft.world.item.Items.AMETHYST_BLOCK), new ItemStack(net.minecraft.world.item.Items.ENDER_EYE), new ItemStack(net.minecraft.world.item.Items.AMETHYST_BLOCK),
                new ItemStack(net.minecraft.world.item.Items.ENDER_EYE), new ItemStack(net.minecraft.world.item.Items.ENDER_CHEST), new ItemStack(net.minecraft.world.item.Items.ENDER_EYE),
                new ItemStack(net.minecraft.world.item.Items.AMETHYST_BLOCK), new ItemStack(net.minecraft.world.item.Items.ENDER_EYE), new ItemStack(net.minecraft.world.item.Items.AMETHYST_BLOCK)
        ));

        assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected Omni Chest recipe to match");
        ItemStack result = recipe.assemble(grid);
        assertTrue(helper, result.is(Items.OMNI_CHEST), "Expected recipe to craft Omni Chest");
        helper.succeed();
    }

    public void omniChestInventoryIsSharedAcrossPlacedChests(GameTestHelper helper) {
        BlockPos firstPos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos secondPos = helper.absolutePos(new BlockPos(3, 2, 1));
        helper.getLevel().setBlockAndUpdate(firstPos, Blocks.OMNI_CHEST.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(secondPos, Blocks.OMNI_CHEST.defaultBlockState());

        OmniChestInventory inventory = BlockOmniChest.getContainer(helper.getLevel().getServer());
        inventory.setItem(0, new ItemStack(net.minecraft.world.item.Items.DIAMOND, 7));

        Player firstPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        Player secondPlayer = helper.makeMockPlayer(GameType.SURVIVAL);

        BlockEntityOmniChest firstChest = requireOmniChest(helper, firstPos, "Expected first Omni Chest block entity");
        BlockEntityOmniChest secondChest = requireOmniChest(helper, secondPos, "Expected second Omni Chest block entity");

        inventory.setActiveChest(firstChest, firstPlayer);
        inventory.setActiveChest(secondChest, secondPlayer);

        ChestMenu firstMenu = ChestMenu.threeRows(0, firstPlayer.getInventory(), inventory);
        ChestMenu secondMenu = ChestMenu.threeRows(1, secondPlayer.getInventory(), inventory);

        assertTrue(helper, firstMenu.getContainer().getItem(0).getCount() == 7, "Expected first player to see shared inventory contents");
        assertTrue(helper, secondMenu.getContainer().getItem(0).getCount() == 7, "Expected second player to see shared inventory contents");

        secondMenu.getContainer().setItem(0, new ItemStack(net.minecraft.world.item.Items.EMERALD, 3));
        assertTrue(helper, firstMenu.getContainer().getItem(0).is(net.minecraft.world.item.Items.EMERALD), "Expected inventory mutation to be shared");
        assertTrue(helper, firstMenu.getContainer().getItem(0).getCount() == 3, "Expected shared inventory count to update");

        secondMenu.removed(secondPlayer);
        firstMenu.removed(firstPlayer);
        helper.succeed();
    }

    public void omniChestTracksDifferentActiveChestsPerPlayer(GameTestHelper helper) {
        BlockPos firstPos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos secondPos = helper.absolutePos(new BlockPos(3, 2, 1));
        helper.getLevel().setBlockAndUpdate(firstPos, Blocks.OMNI_CHEST.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(secondPos, Blocks.OMNI_CHEST.defaultBlockState());

        Player firstPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        Player secondPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        OmniChestInventory inventory = BlockOmniChest.getContainer(helper.getLevel().getServer());

        BlockEntityOmniChest firstChest = requireOmniChest(helper, firstPos, "Expected first Omni Chest block entity");
        BlockEntityOmniChest secondChest = requireOmniChest(helper, secondPos, "Expected second Omni Chest block entity");
        inventory.setActiveChest(firstChest, firstPlayer);
        inventory.setActiveChest(secondChest, secondPlayer);

        assertTrue(helper, inventory.testPlayerChest(firstPlayer, firstChest), "Expected first player to be tracked against first chest");
        assertTrue(helper, inventory.testPlayerChest(secondPlayer, secondChest), "Expected second player to be tracked against second chest");
        assertTrue(helper, !inventory.testPlayerChest(firstPlayer, secondChest), "Expected players to keep separate active chest mappings");
        helper.succeed();
    }

    public void omniChestSavedDataRoundTripsStoredItems(GameTestHelper helper) {
        OmniChestSavedData original = new OmniChestSavedData();
        original.getInventory().setItem(4, new ItemStack(net.minecraft.world.item.Items.DIAMOND, 5));

        RegistryOps<Tag> ops = helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Tag tag = OmniChestSavedData.TYPE.codec().encodeStart(ops, original).getOrThrow();
        OmniChestSavedData restored = OmniChestSavedData.TYPE.codec().parse(ops, tag).getOrThrow();

        ItemStack restoredStack = restored.getInventory().getItem(4);
        assertTrue(helper, restoredStack.is(net.minecraft.world.item.Items.DIAMOND), "Expected saved data to restore the stored item");
        assertTrue(helper, restoredStack.getCount() == 5, "Expected saved data to restore the stored item count");
        helper.succeed();
    }

    public void omniChestLegacySavedDataFileMigratesToNamespacedStorage(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        OmniChestSavedData legacy = new OmniChestSavedData();
        legacy.getInventory().setItem(3, new ItemStack(net.minecraft.world.item.Items.DIAMOND, 9));
        RegistryOps<Tag> ops = helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        CompoundTag root = new CompoundTag();
        root.putInt("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());
        root.put("data", OmniChestSavedData.TYPE.codec().encodeStart(ops, legacy).getOrThrow());

        try {
            Path testDir = server.getWorldPath(LevelResource.DATA).resolve("omni_chest_migration_test");
            Files.createDirectories(testDir);
            Path legacyFile = testDir.resolve("omni_chest.dat");
            NbtIo.writeCompressed(root, legacyFile);
            Path storageDir = testDir.resolve("new");
            Files.createDirectories(storageDir);
            Files.deleteIfExists(storageDir.resolve("xercaomnichest").resolve("omni_chest.dat"));
            try (SavedDataStorage storage = new SavedDataStorage(storageDir, server.getFixerUpper(), server.registryAccess())) {
                assertTrue(helper, storage.get(OmniChestSavedData.TYPE) == null, "Expected fresh storage to have no omni chest data");
                OmniChestSavedData.migrateLegacyFile(storage, server.registryAccess(), legacyFile);
                OmniChestSavedData migrated = storage.get(OmniChestSavedData.TYPE);
                assertTrue(helper, migrated != null, "Expected migration to load legacy omni chest data");
                ItemStack migratedStack = migrated.getInventory().getItem(3);
                assertTrue(helper, migratedStack.is(net.minecraft.world.item.Items.DIAMOND), "Expected migrated omni chest to keep the stored item");
                assertTrue(helper, migratedStack.getCount() == 9, "Expected migrated omni chest to keep the stored item count");
            }
        } catch (IOException e) {
            assertTrue(helper, false, "Failed to run legacy omni chest data migration: " + e);
        }
        helper.succeed();
    }

    public void omniChestBreaksAtPickaxeSpeedLikeEnderChest(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, Blocks.OMNI_CHEST.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockState omniChestState = helper.getLevel().getBlockState(pos);
        BlockState enderChestState = net.minecraft.world.level.block.Blocks.ENDER_CHEST.defaultBlockState();

        player.getInventory().setSelectedItem(ItemStack.EMPTY);
        float handProgress = omniChestState.getDestroyProgress(player, helper.getLevel(), pos);

        ItemStack pickaxe = new ItemStack(net.minecraft.world.item.Items.NETHERITE_PICKAXE);
        EnchantmentHelper.updateEnchantments(pickaxe, enchantments -> enchantments.set(
                helper.getLevel().registryAccess()
                        .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY),
                5
        ));
        player.getInventory().setSelectedItem(pickaxe);

        float omniChestProgress = omniChestState.getDestroyProgress(player, helper.getLevel(), pos);
        float enderChestProgress = enderChestState.getDestroyProgress(player, helper.getLevel(), pos);

        assertTrue(helper, omniChestProgress > handProgress, "Expected pickaxe mining progress to be faster than hand mining progress");
        assertTrue(helper, Math.abs(omniChestProgress - enderChestProgress) < 0.000001F, "Expected Omni Chest mining progress to match Ender Chest with the same pickaxe");
        helper.succeed();
    }

    public void omniChestLootRequiresSilkTouchForFullBlockDrop(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, Blocks.OMNI_CHEST.defaultBlockState());
        BlockState state = helper.getLevel().getBlockState(pos);
        BlockEntity blockEntity = helper.getLevel().getBlockEntity(pos);

        List<ItemStack> normalDrops = net.minecraft.world.level.block.Block.getDrops(
                state,
                helper.getLevel(),
                pos,
                blockEntity,
                null,
                new ItemStack(net.minecraft.world.item.Items.NETHERITE_PICKAXE)
        );
        assertTrue(helper, normalDrops.size() == 1, "Expected one non-silk-touch drop");
        assertTrue(helper, normalDrops.getFirst().is(net.minecraft.world.item.Items.OBSIDIAN), "Expected non-silk-touch Omni Chest to drop obsidian");
        assertTrue(helper, normalDrops.getFirst().getCount() == 8, "Expected non-silk-touch Omni Chest to drop eight obsidian");

        ItemStack silkTouchPickaxe = new ItemStack(net.minecraft.world.item.Items.NETHERITE_PICKAXE);
        EnchantmentHelper.updateEnchantments(silkTouchPickaxe, enchantments -> enchantments.set(
                helper.getLevel().registryAccess()
                        .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH),
                1
        ));

        List<ItemStack> silkTouchDrops = net.minecraft.world.level.block.Block.getDrops(
                state,
                helper.getLevel(),
                pos,
                blockEntity,
                null,
                silkTouchPickaxe
        );
        assertTrue(helper, silkTouchDrops.size() == 1, "Expected one silk-touch drop");
        assertTrue(helper, silkTouchDrops.getFirst().is(Items.OMNI_CHEST), "Expected silk-touch Omni Chest to drop itself");
        assertTrue(helper, silkTouchDrops.getFirst().getCount() == 1, "Expected silk-touch Omni Chest to drop exactly one block");
        helper.succeed();
    }

    public void omniChestCanBePlacedWaterlogged(GameTestHelper helper) {
        BlockPos supportPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos chestPos = supportPos.above();
        helper.getLevel().setBlockAndUpdate(supportPos, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(chestPos, net.minecraft.world.level.block.Blocks.WATER.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack chestStack = new ItemStack(Items.OMNI_CHEST);
        player.setItemInHand(InteractionHand.MAIN_HAND, chestStack);
        Items.OMNI_CHEST.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTopOf(supportPos)));

        BlockState placedState = helper.getLevel().getBlockState(chestPos);
        assertTrue(helper, placedState.is(Blocks.OMNI_CHEST), "Expected Omni Chest item placement to replace the water block");
        assertTrue(helper, placedState.getValue(BlockOmniChest.WATERLOGGED), "Expected placed Omni Chest to keep waterlogged state");
        assertTrue(helper, helper.getLevel().getFluidState(chestPos).getType() == Fluids.WATER, "Expected waterlogged Omni Chest to expose a water fluid state");
        helper.succeed();
    }

    public void omniChestUseItemOpensMenuAndTracksActiveChest(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, Blocks.OMNI_CHEST.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockEntityOmniChest chest = requireOmniChest(helper, pos, "Expected Omni Chest block entity");
        OmniChestInventory inventory = BlockOmniChest.getContainer(helper.getLevel().getServer());

        InteractionResult result = invokeUseItemOn(helper, pos, player, ItemStack.EMPTY);

        assertTrue(helper, result == InteractionResult.SUCCESS, "Expected item interaction to open the Omni Chest");
        assertTrue(helper, inventory.testPlayerChest(player, chest), "Expected Omni Chest interaction to track the player's active chest");
        helper.succeed();
    }

    public void blockedOmniChestDoesNotOpenMenu(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, Blocks.OMNI_CHEST.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(pos.above(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockEntityOmniChest chest = requireOmniChest(helper, pos, "Expected Omni Chest block entity");
        OmniChestInventory inventory = BlockOmniChest.getContainer(helper.getLevel().getServer());

        InteractionResult result = invokeUseWithoutItem(helper, pos, player);

        assertTrue(helper, result == InteractionResult.PASS, "Expected blocked Omni Chest to refuse menu opening");
        helper.assertFalse(inventory.testPlayerChest(player, chest), net.minecraft.network.chat.Component.literal("Expected blocked Omni Chest not to register an active chest for the player"));
        helper.assertFalse(player.containerMenu instanceof ChestMenu, net.minecraft.network.chat.Component.literal("Expected blocked Omni Chest not to open a chest menu"));
        helper.succeed();
    }
}
