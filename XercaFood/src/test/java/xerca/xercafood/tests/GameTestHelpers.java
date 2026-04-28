package xerca.xercafood.tests;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xerca.xercafood.common.KnifeCompat;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block_entity.BlockEntityDoner;

import java.util.*;

class GameTestHelpers {
    static final String BASIC_TEMPLATE = "xercafood:basic_test";
    static final String RECIPE_BATCH = "xercafood_recipes";

    static ResourceLocation recipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Mod.MODID, path);
    }

    static ResourceLocation advancementId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Mod.MODID, path);
    }

    static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, ResourceLocation id) {
        Optional<RecipeHolder<?>> opt = helper.getLevel().getRecipeManager().byKey(id);
        helper.assertTrue(opt.isPresent(), "Missing recipe: " + id);
        Recipe<?> recipe = opt.orElseThrow().value();
        helper.assertTrue(recipe instanceof CraftingRecipe, "Expected crafting recipe for " + id);
        return (CraftingRecipe) recipe;
    }

    static CampfireCookingRecipe requireCampfireRecipe(GameTestHelper helper, ResourceLocation id) {
        Optional<RecipeHolder<?>> opt = helper.getLevel().getRecipeManager().byKey(id);
        helper.assertTrue(opt.isPresent(), "Missing recipe: " + id);
        Recipe<?> recipe = opt.orElseThrow().value();
        helper.assertTrue(recipe instanceof CampfireCookingRecipe, "Expected campfire cooking recipe for " + id);
        return (CampfireCookingRecipe) recipe;
    }

    static SmeltingRecipe requireSmeltingRecipe(GameTestHelper helper, ResourceLocation id) {
        Optional<RecipeHolder<?>> opt = helper.getLevel().getRecipeManager().byKey(id);
        helper.assertTrue(opt.isPresent(), "Missing recipe: " + id);
        Recipe<?> recipe = opt.orElseThrow().value();
        helper.assertTrue(recipe instanceof SmeltingRecipe, "Expected smelting recipe for " + id);
        return (SmeltingRecipe) recipe;
    }

    static SmokingRecipe requireSmokingRecipe(GameTestHelper helper, ResourceLocation id) {
        Optional<RecipeHolder<?>> opt = helper.getLevel().getRecipeManager().byKey(id);
        helper.assertTrue(opt.isPresent(), "Missing recipe: " + id);
        Recipe<?> recipe = opt.orElseThrow().value();
        helper.assertTrue(recipe instanceof SmokingRecipe, "Expected smoking recipe for " + id);
        return (SmokingRecipe) recipe;
    }

    static AdvancementHolder requireAdvancement(GameTestHelper helper, ResourceLocation id) {
        AdvancementHolder advancement = helper.getLevel().getServer().getAdvancements().get(id);
        helper.assertTrue(advancement != null, "Missing advancement: " + id);
        return Objects.requireNonNull(advancement, "Missing advancement after assertion: " + id);
    }

    static net.minecraft.world.item.Item requireKnifeItem() {
        return KnifeCompat.getKnifeItem();
    }

    static BlockEntityDoner requireDonerBlockEntity(GameTestHelper helper, BlockPos pos) {
        if (helper.getLevel().getBlockEntity(pos) instanceof BlockEntityDoner doner) {
            return doner;
        }
        helper.assertTrue(false, "Expected doner block entity at " + pos);
        throw new IllegalStateException("Unreachable after GameTest assertion failure");
    }

    static FoodProperties requireFoodProperties(GameTestHelper helper, net.minecraft.world.item.Item item) {
        FoodProperties food = item.components().get(net.minecraft.core.component.DataComponents.FOOD);
        helper.assertTrue(food != null, "Expected food component on " + item);
        return Objects.requireNonNull(food, "Food component unexpectedly missing after assertion");
    }

    static ServerPlayer makeServerPlayer(GameTestHelper helper) {
        return helper.makeMockServerPlayerInLevel();  // NOSONAR
    }

    static AdvancementProgress advancementProgress(ServerPlayer player, AdvancementHolder advancement) {
        return player.getAdvancements().getOrStartProgress(advancement);
    }

    static void triggerInventoryChanged(ServerPlayer player, ItemStack stack) {
        player.getInventory().add(stack.copy());
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), stack.copy());
    }

    static CraftingInput craftingGrid(int width, int height, ItemStack... stacks) {
        List<ItemStack> list = new ArrayList<>(stacks.length);
        Collections.addAll(list, stacks);
        return CraftingInput.of(width, height, list);
    }

    static BlockHitResult hitTopOf(BlockPos pos) {
        return new BlockHitResult(Vec3.atCenterOf(pos), net.minecraft.core.Direction.UP, pos, false);
    }

    static boolean hasNearbyItem(GameTestHelper helper, BlockPos pos, net.minecraft.world.item.Item item, double radius) {
        AABB box = new AABB(helper.absolutePos(pos)).inflate(radius);
        for (ItemEntity e : helper.getLevel().getEntitiesOfClass(ItemEntity.class, box)) {
            if (e.getItem().is(item)) return true;
        }
        return false;
    }

    static void useBlockWithoutItem(GameTestHelper helper, BlockPos pos, Player player) {
        BlockState state = helper.getLevel().getBlockState(pos);
        try {
            java.lang.reflect.Method m = net.minecraft.world.level.block.state.BlockBehaviour.class.getDeclaredMethod(
                    "useWithoutItem",
                    net.minecraft.world.level.block.state.BlockState.class,
                    net.minecraft.world.level.Level.class,
                    net.minecraft.core.BlockPos.class,
                    net.minecraft.world.entity.player.Player.class,
                    net.minecraft.world.phys.BlockHitResult.class
            );
            m.setAccessible(true);
            m.invoke(state.getBlock(), state, helper.getLevel(), pos, player, hitTopOf(pos));
        } catch (Exception e) {
            helper.fail("Failed to invoke useWithoutItem: " + e.getMessage());
        }
    }

    static void useBlockWithItem(GameTestHelper helper, BlockPos pos, Player player, ItemStack stack) {
        BlockState state = helper.getLevel().getBlockState(pos);
        try {
            java.lang.reflect.Method m = net.minecraft.world.level.block.state.BlockBehaviour.class.getDeclaredMethod(
                    "useItemOn",
                    net.minecraft.world.item.ItemStack.class,
                    net.minecraft.world.level.block.state.BlockState.class,
                    net.minecraft.world.level.Level.class,
                    net.minecraft.core.BlockPos.class,
                    net.minecraft.world.entity.player.Player.class,
                    net.minecraft.world.InteractionHand.class,
                    net.minecraft.world.phys.BlockHitResult.class
            );
            m.setAccessible(true);
            m.invoke(state.getBlock(), stack, state, helper.getLevel(), pos, player, InteractionHand.MAIN_HAND, hitTopOf(pos));
        } catch (Exception e) {
            helper.fail("Failed to invoke useItemOn: " + e.getMessage());
        }
    }
}
