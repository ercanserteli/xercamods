package xerca.xercacourt.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xerca.xercacourt.Mod;
import xerca.xercacourt.SoundEvents;
import xerca.xercacourt.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CourtGameTests {
    private static final String BASIC_TEMPLATE = "xercacourt:basic_test";
    private static final String COURTROOM_BATCH = "xercacourt_regressions";

    private static ResourceLocation recipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, path);
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId);
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().recipeAccess().byKey(recipeKey);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        return (CraftingRecipe) recipe;
    }

    private static CraftingInput craftingGrid(int width, int height, ItemStack... stacks) {
        List<ItemStack> list = new ArrayList<>(stacks.length);
        Collections.addAll(list, stacks);
        return CraftingInput.of(width, height, list);
    }

    @GameTest(template = BASIC_TEMPLATE, batch = COURTROOM_BATCH)
    public static void gavelRecipeCraftsFromPlanksAndSticks(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("gavel"));
        ItemStack plank = new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS);
        ItemStack stick = new ItemStack(net.minecraft.world.item.Items.STICK);
        CraftingInput grid = craftingGrid(3, 3,
                plank.copy(), stick.copy(), plank.copy(),
                ItemStack.EMPTY, stick.copy(), ItemStack.EMPTY,
                ItemStack.EMPTY, stick.copy(), ItemStack.EMPTY
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected gavel recipe to match planks and sticks");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.GAVEL), "Expected gavel recipe to produce a gavel");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = COURTROOM_BATCH)
    public static void prosecutorBadgeAcceptsPoppyAndDandelionVariants(GameTestHelper helper) {
        CraftingRecipe poppyRecipe = requireCraftingRecipe(helper, recipeId("prosecutor_badge"));
        CraftingRecipe dandelionRecipe = requireCraftingRecipe(helper, recipeId("prosecutor_badge_alt"));

        CraftingInput poppyGrid = craftingGrid(2, 1,
                new ItemStack(net.minecraft.world.item.Items.GOLD_NUGGET),
                new ItemStack(net.minecraft.world.item.Items.POPPY)
        );
        CraftingInput dandelionGrid = craftingGrid(2, 1,
                new ItemStack(net.minecraft.world.item.Items.GOLD_NUGGET),
                new ItemStack(net.minecraft.world.item.Items.DANDELION)
        );

        helper.assertTrue(poppyRecipe.matches(poppyGrid, helper.getLevel()), "Expected prosecutor badge recipe to accept a poppy");
        helper.assertTrue(dandelionRecipe.matches(dandelionGrid, helper.getLevel()), "Expected alternate prosecutor badge recipe to accept a dandelion");
        helper.assertTrue(poppyRecipe.assemble(poppyGrid, helper.getLevel().registryAccess()).is(Items.PROSECUTOR_BADGE),
                "Expected poppy recipe to produce prosecutor badge");
        helper.assertTrue(dandelionRecipe.assemble(dandelionGrid, helper.getLevel().registryAccess()).is(Items.PROSECUTOR_BADGE),
                "Expected dandelion recipe to produce prosecutor badge");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = COURTROOM_BATCH)
    public static void badgeUseStartsCooldown(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ATTORNEY_BADGE));

        Items.ATTORNEY_BADGE.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);

        helper.assertTrue(player.getCooldowns().isOnCooldown(player.getMainHandItem()), "Expected attorney badge use to start cooldown");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = COURTROOM_BATCH)
    public static void gavelUseOnSolidBlockSucceeds(GameTestHelper helper) {
        BlockPos targetPos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(targetPos, Blocks.STONE.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.moveTo(Vec3.atCenterOf(targetPos.above()));
        ItemStack stack = new ItemStack(Items.GAVEL);
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);

        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(targetPos), net.minecraft.core.Direction.UP, targetPos, false);
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertTrue(Items.GAVEL.useOn(context).consumesAction(), "Expected gavel use on a solid block to succeed");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = COURTROOM_BATCH)
    public static void gavelOnSolidBlockPlaysSound(GameTestHelper helper) {
        SoundRecorder.clear();
        BlockPos targetPos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(targetPos, Blocks.STONE.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.moveTo(Vec3.atCenterOf(targetPos.above()));
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GAVEL));

        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(targetPos), net.minecraft.core.Direction.UP, targetPos, false);
        Items.GAVEL.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult));

        helper.assertTrue(SoundRecorder.played(SoundEvents.GAVEL), "Expected gavel on a solid block to play the gavel sound");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = COURTROOM_BATCH)
    public static void gavelOnNonSolidBlockPlaysNoSound(GameTestHelper helper) {
        SoundRecorder.clear();
        BlockPos targetPos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(targetPos, Blocks.GLASS.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.moveTo(Vec3.atCenterOf(targetPos.above()));
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GAVEL));

        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(targetPos), net.minecraft.core.Direction.UP, targetPos, false);
        Items.GAVEL.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult));

        helper.assertFalse(SoundRecorder.played(SoundEvents.GAVEL), "Expected gavel on a non-occluding block to stay silent");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = COURTROOM_BATCH)
    public static void prosecutorBadgeUsePlaysObjectionSound(GameTestHelper helper) {
        SoundRecorder.clear();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.PROSECUTOR_BADGE));

        Items.PROSECUTOR_BADGE.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);

        helper.assertTrue(SoundRecorder.played(SoundEvents.OBJECTION), "Expected prosecutor badge use to play the objection sound");
        helper.succeed();
    }
}
