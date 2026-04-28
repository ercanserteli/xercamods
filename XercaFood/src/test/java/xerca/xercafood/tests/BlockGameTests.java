package xerca.xercafood.tests;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.item.Items;

import java.util.List;

import static xerca.xercafood.tests.GameTestHelpers.*;

public class BlockGameTests {

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void tomatoPlantDropsTomatoWhenGrown(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_tomato_plant"));
        net.minecraft.world.level.block.state.BlockState grownTomato = block.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3, 3);

        helper.setBlock(pos, grownTomato);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);

        helper.assertItemEntityPresent(Items.TOMATO, pos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void ricePlantDropsRiceSeedsWhenGrown(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_rice_plant"));
        net.minecraft.world.level.block.state.BlockState grownRice = block.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_7, 7);

        helper.setBlock(pos, grownRice);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);

        helper.assertItemEntityPresent(Items.RICE_SEEDS, pos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teaPlantDropsTeaLeafWhenGrown(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_tea_plant"));
        net.minecraft.world.level.block.state.BlockState grownTea = block.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3, 3);

        helper.setBlock(pos, grownTea);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);

        helper.assertItemEntityPresent(Items.TEA_LEAF, pos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void vatDropsVat(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "vat"));
        net.minecraft.world.level.block.state.BlockState vat = block.defaultBlockState();

        helper.setBlock(pos, vat);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);

        helper.assertItemEntityPresent(Items.VAT, pos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void vatBreaksFasterWithPickaxeThanByHand(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, xerca.xercafood.common.block.Blocks.VAT.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockState state = helper.getLevel().getBlockState(pos);

        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
        float handProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        player.getInventory().setItem(player.getInventory().selected, new ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE));
        float pickaxeProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        helper.assertTrue(pickaxeProgress > handProgress, "Expected curdling vat to break faster with a pickaxe than by hand");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void vatDropsItselfWithoutAnyTool(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, xerca.xercafood.common.block.Blocks.VAT.defaultBlockState());

        BlockState state = helper.getLevel().getBlockState(pos);
        List<ItemStack> drops = net.minecraft.world.level.block.Block.getDrops(
                state,
                helper.getLevel(),
                pos,
                helper.getLevel().getBlockEntity(pos),
                null,
                ItemStack.EMPTY
        );

        helper.assertTrue(drops.size() == 1, "Expected curdling vat to have one drop without tools");
        helper.assertTrue(drops.get(0).is(Items.VAT), "Expected curdling vat to drop itself without tools");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void piesBreakAtCakeSpeed(GameTestHelper helper) {
        BlockPos cakePos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos applePiePos = helper.absolutePos(new BlockPos(2, 2, 1));
        BlockPos berryPiePos = helper.absolutePos(new BlockPos(3, 2, 1));
        net.minecraft.world.level.block.Block applePieBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_apple_pie"));
        net.minecraft.world.level.block.Block berryPieBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_sweet_berry_pie"));

        helper.getLevel().setBlockAndUpdate(cakePos, Blocks.CAKE.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(applePiePos, applePieBlock.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(berryPiePos, berryPieBlock.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);

        float cakeProgress = helper.getLevel().getBlockState(cakePos).getDestroyProgress(player, helper.getLevel(), cakePos);
        float applePieProgress = helper.getLevel().getBlockState(applePiePos).getDestroyProgress(player, helper.getLevel(), applePiePos);
        float berryPieProgress = helper.getLevel().getBlockState(berryPiePos).getDestroyProgress(player, helper.getLevel(), berryPiePos);

        helper.assertTrue(Float.compare(applePieProgress, cakeProgress) == 0,
                "Expected apple pie to break at the same speed as cake");
        helper.assertTrue(Float.compare(berryPieProgress, cakeProgress) == 0,
                "Expected sweet berry pie to break at the same speed as cake");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void piesConsumeBySlicesUntilRemoved(GameTestHelper helper) {
        BlockPos applePiePos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos berryPiePos = helper.absolutePos(new BlockPos(2, 2, 1));
        net.minecraft.world.level.block.Block applePieBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_apple_pie"));
        net.minecraft.world.level.block.Block berryPieBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_sweet_berry_pie"));
        helper.getLevel().setBlockAndUpdate(applePiePos, applePieBlock.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(berryPiePos, berryPieBlock.defaultBlockState());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(0);

        for (int i = 0; i < 7; i++) {
            useBlockWithoutItem(helper, applePiePos, player);
        }
        helper.assertTrue(helper.getLevel().getBlockState(applePiePos).isAir(), "Expected apple pie to be consumed after 7 slices");

        player.getFoodData().setFoodLevel(0);
        for (int i = 0; i < 7; i++) {
            useBlockWithoutItem(helper, berryPiePos, player);
        }
        helper.assertTrue(helper.getLevel().getBlockState(berryPiePos).isAir(), "Expected sweet berry pie to be consumed after 7 slices");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void cheeseWheelAndPizzaConsumeByQuarters(GameTestHelper helper) {
        BlockPos cheesePos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos pizzaPos = helper.absolutePos(new BlockPos(2, 2, 1));
        net.minecraft.world.level.block.Block cheeseBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "cheese_wheel"));
        helper.getLevel().setBlockAndUpdate(cheesePos, cheeseBlock.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(pizzaPos, xerca.xercafood.common.block.Blocks.PIZZA.defaultBlockState());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(0);

        for (int i = 0; i < 4; i++) {
            useBlockWithoutItem(helper, cheesePos, player);
        }
        helper.assertTrue(helper.getLevel().getBlockState(cheesePos).isAir(), "Expected cheese wheel to be gone after 4 bites");

        player.getFoodData().setFoodLevel(0);
        for (int i = 0; i < 4; i++) {
            useBlockWithoutItem(helper, pizzaPos, player);
        }
        helper.assertTrue(helper.getLevel().getBlockState(pizzaPos).isAir(), "Expected pizza to be gone after 4 bites");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void cheeseWheelSlicingWithKnifeDropsSlice(GameTestHelper helper) {
        BlockPos cheesePos = helper.absolutePos(new BlockPos(1, 2, 1));
        net.minecraft.world.level.block.Block cheeseBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "cheese_wheel"));
        helper.getLevel().setBlockAndUpdate(cheesePos, cheeseBlock.defaultBlockState());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack knife = new ItemStack(requireKnifeItem());
        useBlockWithItem(helper, cheesePos, player, knife);

        helper.assertTrue(hasNearbyItem(helper, new BlockPos(1, 2, 1), Items.CHEESE_SLICE, 3.0), "Expected slicing cheese to drop a cheese slice");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void vatMilkToCheeseConversionFlowWorks(GameTestHelper helper) {
        BlockPos vatPos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(vatPos, xerca.xercafood.common.block.Blocks.VAT.defaultBlockState());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack milkBucket = new ItemStack(net.minecraft.world.item.Items.MILK_BUCKET);

        useBlockWithItem(helper, vatPos, player, milkBucket);
        helper.assertTrue(helper.getLevel().getBlockState(vatPos).is(xerca.xercafood.common.block.Blocks.VAT_MILK), "Expected vat to become milk vat");

        xerca.xercafood.common.block.Blocks.VAT_MILK.randomTick(helper.getLevel().getBlockState(vatPos), helper.getLevel(), vatPos, helper.getLevel().random);
        helper.assertTrue(helper.getLevel().getBlockState(vatPos).is(xerca.xercafood.common.block.Blocks.VAT_CHEESE), "Expected milk vat to curdle into cheese vat");

        useBlockWithoutItem(helper, vatPos, player);
        helper.assertTrue(hasNearbyItem(helper, new BlockPos(1, 2, 1), Items.CHEESE_WHEEL, 3.0), "Expected cheese vat interaction to drop cheese wheel");
        helper.assertTrue(helper.getLevel().getBlockState(vatPos).is(xerca.xercafood.common.block.Blocks.VAT), "Expected cheese vat to revert to empty vat");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void donerCreationCookingAndSlicingFlowWorks(GameTestHelper helper) {
        BlockPos donerPos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(donerPos, Blocks.IRON_BARS.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(donerPos.below(), Blocks.CAMPFIRE.defaultBlockState().setValue(net.minecraft.world.level.block.CampfireBlock.LIT, true));
        helper.getLevel().setBlockAndUpdate(donerPos.east(), Blocks.REDSTONE_BLOCK.defaultBlockState());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        ItemStack mutton = new ItemStack(net.minecraft.world.item.Items.MUTTON, 4);
        player.getInventory().setItem(player.getInventory().selected, mutton);
        UseBlockCallback.EVENT.invoker().interact(player, helper.getLevel(), InteractionHand.MAIN_HAND, hitTopOf(donerPos));
        helper.assertTrue(helper.getLevel().getBlockState(donerPos).is(xerca.xercafood.common.block.Blocks.BLOCK_DONER), "Expected mutton+iron bars to create doner");

        useBlockWithItem(helper, donerPos, player, mutton);
        useBlockWithItem(helper, donerPos, player, mutton);
        useBlockWithItem(helper, donerPos, player, mutton);
        helper.assertTrue(helper.getLevel().getBlockState(donerPos).getValue(xerca.xercafood.common.block.BlockDoner.MEAT_AMOUNT) == 4, "Expected doner to reach max meat");

        xerca.xercafood.common.block_entity.BlockEntityDoner be = requireDonerBlockEntity(helper, donerPos);
        for (int i = 0; i < 510; i++) {
            xerca.xercafood.common.block_entity.BlockEntityDoner.tick(helper.getLevel(), be);
        }
        helper.assertFalse(helper.getLevel().getBlockState(donerPos).getValue(xerca.xercafood.common.block.BlockDoner.IS_RAW), "Expected doner to cook when heated and powered");

        ItemStack knife = new ItemStack(requireKnifeItem());
        useBlockWithItem(helper, donerPos, player, knife);
        helper.assertTrue(hasNearbyItem(helper, new BlockPos(1, 2, 1), Items.DONER_SLICE, 3.0), "Expected slicing cooked doner to drop doner slice");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void cropGrowthAndBoneMealBehaviorForTeaAndTomato(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos teaPos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos tomatoPos = helper.absolutePos(new BlockPos(2, 2, 1));
        BlockPos soilTea = teaPos.below();
        BlockPos soilTomato = tomatoPos.below();
        level.setBlockAndUpdate(soilTea, Blocks.FARMLAND.defaultBlockState());
        level.setBlockAndUpdate(soilTomato, Blocks.FARMLAND.defaultBlockState());
        level.setBlockAndUpdate(teaPos, xerca.xercafood.common.block.Blocks.BLOCK_TEA_PLANT.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3, 0));
        level.setBlockAndUpdate(tomatoPos, net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_tomato_plant")).defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3, 0));

        net.minecraft.world.level.block.BonemealableBlock tea = (net.minecraft.world.level.block.BonemealableBlock) level.getBlockState(teaPos).getBlock();
        net.minecraft.world.level.block.BonemealableBlock tomato = (net.minecraft.world.level.block.BonemealableBlock) level.getBlockState(tomatoPos).getBlock();
        for (int i = 0; i < 5; i++) {
            BlockState teaState = level.getBlockState(teaPos);
            BlockState tomatoState = level.getBlockState(tomatoPos);
            if (tea.isValidBonemealTarget(level, teaPos, teaState)) {
                tea.performBonemeal(level, level.random, teaPos, teaState);
            }
            if (tomato.isValidBonemealTarget(level, tomatoPos, tomatoState)) {
                tomato.performBonemeal(level, level.random, tomatoPos, tomatoState);
            }
        }

        helper.assertTrue(level.getBlockState(teaPos).getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3) > 0, "Expected tea plant to grow with bonemeal");
        helper.assertTrue(level.getBlockState(tomatoPos).getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3) > 0, "Expected tomato plant to grow with bonemeal");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void shortGrassCanDropTeaAndTomatoSeeds(GameTestHelper helper) {
        BlockPos grassPos = helper.absolutePos(new BlockPos(1, 2, 1));
        for (int i = 0; i < 200; i++) {
            helper.getLevel().setBlockAndUpdate(grassPos, Blocks.SHORT_GRASS.defaultBlockState());
            helper.getLevel().destroyBlock(grassPos, true);
        }
        helper.assertTrue(hasNearbyItem(helper, new BlockPos(1, 2, 1), Items.TEA_SEEDS, 8.0), "Expected short grass to drop tea seeds");
        helper.assertTrue(hasNearbyItem(helper, new BlockPos(1, 2, 1), Items.TOMATO_SEEDS, 8.0), "Expected short grass to drop tomato seeds");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teapotBlockInteractionFillsCup(GameTestHelper helper) {
        BlockPos teapotPos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(teapotPos, xerca.xercafood.common.block.Blocks.BLOCK_TEAPOT.defaultBlockState().setValue(xerca.xercafood.common.block.BlockTeapot.TEA_AMOUNT, 2));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack cup = new ItemStack(Items.TEACUP);
        player.getInventory().setItem(player.getInventory().selected, cup);

        useBlockWithItem(helper, teapotPos, player, cup);
        helper.assertTrue(player.getInventory().contains(new ItemStack(Items.FULL_TEACUP_0)), "Expected teapot block interaction to fill a cup");
        helper.assertTrue(helper.getLevel().getBlockState(teapotPos).getValue(xerca.xercafood.common.block.BlockTeapot.TEA_AMOUNT) == 1, "Expected teapot block tea amount to decrease");
        helper.succeed();
    }
}
