package xerca.xercaomnichest.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercaomnichest.Mod;
import xerca.xercaomnichest.block.BlockOmniChest;
import xerca.xercaomnichest.block.Blocks;
import xerca.xercaomnichest.block_entity.BlockEntityOmniChest;
import xerca.xercaomnichest.data.OmniChestInventory;
import xerca.xercaomnichest.data.OmniChestSavedData;
import xerca.xercaomnichest.item.Items;

import java.util.List;
import java.util.Optional;

public final class OmniChestGameTests {
    private static final String BASIC_TEMPLATE = "xercaomnichest:basic_test";
    private static final String BATCH = "xercaomnichest.omni_chest";

    private static ResourceLocation recipeId(String path) {
        return Mod.id(path);
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        return (CraftingRecipe) recipe;
    }

    private static BlockEntityOmniChest requireOmniChest(GameTestHelper helper, BlockPos pos, String message) {
        if (helper.getLevel().getBlockEntity(pos) instanceof BlockEntityOmniChest chest) {
            return chest;
        }
        helper.assertTrue(false, message);
        throw new IllegalStateException("Unreachable after GameTest assertion failure");
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void omniChestRecipeCraftsFromAmethystEyesAndEnderChest(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("omni_chest"));
        CraftingInput grid = CraftingInput.of(3, 3, java.util.List.of(
                new ItemStack(net.minecraft.world.item.Items.AMETHYST_BLOCK), new ItemStack(net.minecraft.world.item.Items.ENDER_EYE), new ItemStack(net.minecraft.world.item.Items.AMETHYST_BLOCK),
                new ItemStack(net.minecraft.world.item.Items.ENDER_EYE), new ItemStack(net.minecraft.world.item.Items.ENDER_CHEST), new ItemStack(net.minecraft.world.item.Items.ENDER_EYE),
                new ItemStack(net.minecraft.world.item.Items.AMETHYST_BLOCK), new ItemStack(net.minecraft.world.item.Items.ENDER_EYE), new ItemStack(net.minecraft.world.item.Items.AMETHYST_BLOCK)
        ));

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected Omni Chest recipe to match");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.OMNI_CHEST), "Expected recipe to craft Omni Chest");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void omniChestInventoryIsSharedAcrossPlacedChests(GameTestHelper helper) {
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

        helper.assertTrue(firstMenu.getContainer().getItem(0).getCount() == 7, "Expected first player to see shared inventory contents");
        helper.assertTrue(secondMenu.getContainer().getItem(0).getCount() == 7, "Expected second player to see shared inventory contents");

        secondMenu.getContainer().setItem(0, new ItemStack(net.minecraft.world.item.Items.EMERALD, 3));
        helper.assertTrue(firstMenu.getContainer().getItem(0).is(net.minecraft.world.item.Items.EMERALD), "Expected inventory mutation to be shared");
        helper.assertTrue(firstMenu.getContainer().getItem(0).getCount() == 3, "Expected shared inventory count to update");

        secondMenu.removed(secondPlayer);
        firstMenu.removed(firstPlayer);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void omniChestTracksDifferentActiveChestsPerPlayer(GameTestHelper helper) {
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

        helper.assertTrue(inventory.testPlayerChest(firstPlayer, firstChest), "Expected first player to be tracked against first chest");
        helper.assertTrue(inventory.testPlayerChest(secondPlayer, secondChest), "Expected second player to be tracked against second chest");
        helper.assertTrue(!inventory.testPlayerChest(firstPlayer, secondChest), "Expected players to keep separate active chest mappings");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void omniChestSavedDataRoundTripsStoredItems(GameTestHelper helper) {
        OmniChestSavedData original = new OmniChestSavedData();
        original.getInventory().setItem(4, new ItemStack(net.minecraft.world.item.Items.DIAMOND, 5));

        CompoundTag tag = original.save(new CompoundTag(), helper.getLevel().registryAccess());
        OmniChestSavedData restored = OmniChestSavedData.load(tag, helper.getLevel().registryAccess());

        ItemStack restoredStack = restored.getInventory().getItem(4);
        helper.assertTrue(restoredStack.is(net.minecraft.world.item.Items.DIAMOND), "Expected saved data to restore the stored item");
        helper.assertTrue(restoredStack.getCount() == 5, "Expected saved data to restore the stored item count");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void omniChestBreaksAtPickaxeSpeedLikeEnderChest(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, Blocks.OMNI_CHEST.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockState omniChestState = helper.getLevel().getBlockState(pos);
        BlockState enderChestState = net.minecraft.world.level.block.Blocks.ENDER_CHEST.defaultBlockState();

        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
        float handProgress = omniChestState.getDestroyProgress(player, helper.getLevel(), pos);

        ItemStack pickaxe = new ItemStack(net.minecraft.world.item.Items.NETHERITE_PICKAXE);
        EnchantmentHelper.updateEnchantments(pickaxe, enchantments -> enchantments.set(
                helper.getLevel().registryAccess()
                        .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY),
                5
        ));
        player.getInventory().setItem(player.getInventory().selected, pickaxe);

        float omniChestProgress = omniChestState.getDestroyProgress(player, helper.getLevel(), pos);
        float enderChestProgress = enderChestState.getDestroyProgress(player, helper.getLevel(), pos);

        helper.assertTrue(omniChestProgress > handProgress, "Expected pickaxe mining progress to be faster than hand mining progress");
        helper.assertTrue(Math.abs(omniChestProgress - enderChestProgress) < 0.000001F, "Expected Omni Chest mining progress to match Ender Chest with the same pickaxe");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void omniChestLootRequiresSilkTouchForFullBlockDrop(GameTestHelper helper) {
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
        helper.assertTrue(normalDrops.size() == 1, "Expected one non-silk-touch drop");
        helper.assertTrue(normalDrops.get(0).is(net.minecraft.world.item.Items.OBSIDIAN), "Expected non-silk-touch Omni Chest to drop obsidian");
        helper.assertTrue(normalDrops.get(0).getCount() == 8, "Expected non-silk-touch Omni Chest to drop eight obsidian");

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
        helper.assertTrue(silkTouchDrops.size() == 1, "Expected one silk-touch drop");
        helper.assertTrue(silkTouchDrops.get(0).is(Items.OMNI_CHEST), "Expected silk-touch Omni Chest to drop itself");
        helper.assertTrue(silkTouchDrops.get(0).getCount() == 1, "Expected silk-touch Omni Chest to drop exactly one block");
        helper.succeed();
    }
}
