package xerca.xercatools.tests;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xerca.xercatools.crafting.RecipeEnderBowFilling;
import xerca.xercatools.crafting.RecipeFlaskFilling;
import xerca.xercatools.enchantment.FlaskEnchantments;
import xerca.xercatools.item.ItemFlask;
import xerca.xercatools.item.ItemPotionLauncher;
import xerca.xercatools.item.Items;

import java.util.List;

public class FlaskAndLauncherGameTests {
    private static final String BASIC_TEMPLATE = "xercatools:basic_test";
    private static final String WEAPONS_BATCH = "xercatools_tests";

    // Builds a regular potion stack (PotionItem) with the given potion type.
    private static ItemStack makeRegularPotion(Holder<Potion> holder) {
        ItemStack stack = new ItemStack(net.minecraft.world.item.Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));
        return stack;
    }

    // Builds a splash potion stack (ThrowablePotionItem) used by the Ender Bow recipe.
    private static ItemStack makeSplashPotion(Holder<Potion> holder) {
        ItemStack stack = new ItemStack(net.minecraft.world.item.Items.SPLASH_POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));
        return stack;
    }

    // ── Flask max-charges ─────────────────────────────────────────────────────

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskMaxChargesBaseIs16(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack flask = new ItemStack(Items.FLASK);
        int max = ItemFlask.getMaxCharges(flask, level);
        helper.assertTrue(max == 16, "Base flask max charges should be 16, got " + max);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskHasDrinkConsumableSoDrinkingMakesSound(GameTestHelper helper) {
        ItemStack flask = new ItemStack(Items.FLASK);
        var consumable = flask.get(DataComponents.CONSUMABLE);
        helper.assertTrue(consumable != null,
                "Flask needs a consumable component: vanilla only plays drinking sounds during use ticks through it");
        helper.assertTrue(consumable.animation() == net.minecraft.world.item.ItemUseAnimation.DRINK,
                "Flask consumable should use the drink animation/sound profile");

        // The gulp sounds must actually trigger within the flask's own use duration.
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        int useDuration = flask.getItem().getUseDuration(flask, player);
        helper.assertTrue(useDuration == 32, "Base flask use duration should stay 32 ticks, got " + useDuration);
        boolean makesSound = false;
        for (int remaining = useDuration; remaining > 0; remaining--) {
            makesSound |= consumable.shouldEmitParticlesAndSounds(remaining);
        }
        helper.assertTrue(makesSound, "Expected drinking sounds to trigger during the flask's use duration");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskCapacityEnchantmentDoublesMaxCharges(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack flask = new ItemStack(Items.FLASK);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(FlaskEnchantments.capacityEnchantment(level.registryAccess()), 1);
        flask.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        int max = ItemFlask.getMaxCharges(flask, level);
        helper.assertTrue(max == 32, "Capacity I should give max 32 charges (16×2), got " + max);
        helper.succeed();
    }

    // ── Flask use duration ────────────────────────────────────────────────────

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskUseDurationBaseIs32(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack flask = new ItemStack(Items.FLASK);
        int duration = Items.FLASK.getUseDuration(flask, player);
        helper.assertTrue(duration == 32, "Base flask use duration should be 32, got " + duration);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskChugLevel1DecreasesUseDuration(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack flask = new ItemStack(Items.FLASK);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(FlaskEnchantments.chugEnchantment(level.registryAccess()), 1);
        flask.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        int duration = Items.FLASK.getUseDuration(flask, player);
        helper.assertTrue(duration == 21, "Chug I flask use duration should be 21, got " + duration);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskChugLevel2DecreasesUseDurationFurther(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack flask = new ItemStack(Items.FLASK);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(FlaskEnchantments.chugEnchantment(level.registryAccess()), 2);
        flask.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        int duration = Items.FLASK.getUseDuration(flask, player);
        helper.assertTrue(duration == 10, "Chug II flask use duration should be 10, got " + duration);
        helper.succeed();
    }

    // ── Flask filling recipe ──────────────────────────────────────────────────

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskFillingRecipeAcceptsFlaskAndPotion(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        RecipeFlaskFilling recipe = new RecipeFlaskFilling(CraftingBookCategory.MISC);

        ItemStack flask = new ItemStack(Items.FLASK);
        ItemStack potion = makeRegularPotion(Potions.HEALING);
        // 3×3 grid: flask + one potion, rest empty
        CraftingInput input = CraftingInput.of(3, 3, List.of(
            flask, potion, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        ));

        helper.assertTrue(recipe.matches(input, level),
            "Flask + potion recipe should match");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskFillingRecipeRejectsOverCapacity(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        RecipeFlaskFilling recipe = new RecipeFlaskFilling(CraftingBookCategory.MISC);

        ItemStack flask = new ItemStack(Items.FLASK);
        ItemFlask.setCharges(flask, 16); // already at max capacity
        ItemStack potion = makeRegularPotion(Potions.HEALING);
        CraftingInput input = CraftingInput.of(3, 3, List.of(
            flask, potion, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        ));

        helper.assertTrue(!recipe.matches(input, level),
            "Flask filling should be rejected when already full");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskFillingRecipeRejectsMixedPotions(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        RecipeFlaskFilling recipe = new RecipeFlaskFilling(CraftingBookCategory.MISC);

        ItemStack flask = new ItemStack(Items.FLASK);
        ItemStack healing = makeRegularPotion(Potions.HEALING);
        ItemStack regen = makeRegularPotion(Potions.REGENERATION);
        CraftingInput input = CraftingInput.of(3, 3, List.of(
            flask, healing, regen,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        ));

        helper.assertTrue(!recipe.matches(input, level),
            "Flask filling with mixed potion types should be rejected");
        helper.succeed();
    }

    // ── Flask drinking effect ─────────────────────────────────────────────────

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void flaskDrinkingAppliesInstantHealingEffect(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        ItemStack flask = new ItemStack(Items.FLASK);
        ItemFlask.setCharges(flask, 1);
        // Healing II potion contents (instant effect)
        flask.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.STRONG_HEALING));
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, flask);

        float damagedHealth = player.getMaxHealth() - 6.0f;
        player.setHealth(damagedHealth);
        float healthBefore = player.getHealth();

        Items.FLASK.finishUsingItem(flask, level, player);

        helper.assertTrue(player.getHealth() > healthBefore,
            "Drinking a healing flask should increase player health");
        helper.succeed();
    }

    // ── Potion Launcher (Ender Bow) ───────────────────────────────────────────

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void potionLauncherFillingRecipeAcceptsEnderBowAndSplashPotion(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        RecipeEnderBowFilling recipe = new RecipeEnderBowFilling(CraftingBookCategory.MISC);

        ItemStack launcher = new ItemStack(Items.ENDER_BOW);
        ItemStack splash = makeSplashPotion(Potions.HEALING);
        CraftingInput input = CraftingInput.of(3, 3, List.of(
            launcher, splash, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
            ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        ));

        helper.assertTrue(recipe.matches(input, level),
            "Ender Bow + splash potion recipe should match");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void potionLauncherFiresSplashPotionByDefault(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);
        player.setXRot(0.0f);

        ItemStack launcher = new ItemStack(Items.ENDER_BOW);
        ItemFlask.setCharges(launcher, 1);
        launcher.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.HEALING));
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, launcher);

        Items.ENDER_BOW.use(level, player, net.minecraft.world.InteractionHand.MAIN_HAND);

        AABB searchBox = new AABB(abs.x - 10, abs.y - 10, abs.z - 10,
                                   abs.x + 10, abs.y + 10, abs.z + 10);
        List<ThrownPotion> projectiles = level.getEntitiesOfClass(ThrownPotion.class, searchBox);
        helper.assertTrue(!projectiles.isEmpty(), "Ender Bow use should spawn a ThrownPotion");

        boolean isSplash = projectiles.get(0).getItem().is(net.minecraft.world.item.Items.SPLASH_POTION);
        helper.assertTrue(isSplash, "Ender Bow without lingering flag should fire a splash potion");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void potionLauncherFiresLingeringPotionWhenFlagSet(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);
        player.setXRot(0.0f);

        ItemStack launcher = new ItemStack(Items.ENDER_BOW);
        ItemFlask.setCharges(launcher, 1);
        launcher.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Potions.HEALING));
        ItemPotionLauncher.setLingering(launcher, true);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, launcher);

        Items.ENDER_BOW.use(level, player, net.minecraft.world.InteractionHand.MAIN_HAND);

        AABB searchBox = new AABB(abs.x - 10, abs.y - 10, abs.z - 10,
                                   abs.x + 10, abs.y + 10, abs.z + 10);
        List<ThrownPotion> projectiles = level.getEntitiesOfClass(ThrownPotion.class, searchBox);
        helper.assertTrue(!projectiles.isEmpty(), "Ender Bow use should spawn a ThrownPotion");

        boolean isLingering = projectiles.get(0).getItem().is(net.minecraft.world.item.Items.LINGERING_POTION);
        helper.assertTrue(isLingering, "Ender Bow with lingering flag should fire a lingering potion");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void potionLauncherFailsWithZeroCharges(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);

        ItemStack launcher = new ItemStack(Items.ENDER_BOW);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, launcher);
        // No charges set → getCharges returns 0

        var result = Items.ENDER_BOW.use(level, player, net.minecraft.world.InteractionHand.MAIN_HAND);

        helper.assertTrue(result == net.minecraft.world.InteractionResult.FAIL,
            "Ender Bow with zero charges should return FAIL");

        AABB searchBox = new AABB(abs.x - 10, abs.y - 10, abs.z - 10,
                                   abs.x + 10, abs.y + 10, abs.z + 10);
        helper.assertTrue(level.getEntitiesOfClass(ThrownPotion.class, searchBox).isEmpty(),
            "No projectile should be spawned when charges are zero");
        helper.succeed();
    }
}
