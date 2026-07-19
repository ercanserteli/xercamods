package xerca.xercamusic.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import xerca.xercamusic.common.Mod;

import java.util.*;

import static xerca.xercamusic.tests.TestAsserts.assertTrue;

/**
 * Server-side coverage of every music crafting recipe, ported from the crafting half of the
 * {@code music-fabric.sikuli} script. Each recipe's authoritative grid (from
 * {@code data/xercamusic/recipe/*.json}) is fed to the recipe manager and the resolved output is
 * asserted, replacing the Sikuli test that drove the crafting-table GUI by pixel.
 */
@SuppressWarnings({"DataFlowIssue", "unused"})
public final class MusicRecipeGameTests {

    private static Map<Character, Item> key(Object... charItemPairs) {
        Map<Character, Item> map = new HashMap<>();
        for (int i = 0; i < charItemPairs.length; i += 2) {
            map.put((Character) charItemPairs[i], (Item) charItemPairs[i + 1]);
        }
        return map;
    }

    private static void assertShaped(GameTestHelper helper, String expectedPath, String[] pattern, Map<Character, Item> key) {
        int height = pattern.length;
        int width = 0;
        for (String row : pattern) {
            width = Math.max(width, row.length());
        }
        List<ItemStack> items = new ArrayList<>(width * height);
        for (String row : pattern) {
            for (int x = 0; x < width; x++) {
                char c = x < row.length() ? row.charAt(x) : ' ';
                if (c == ' ') {
                    items.add(ItemStack.EMPTY);
                } else {
                    Item item = key.get(c);
                    assertTrue(helper, item != null, "Missing key mapping for '" + c + "' in recipe " + expectedPath);
                    items.add(new ItemStack(item));
                }
            }
        }
        assertCrafts(helper, expectedPath, CraftingInput.of(width, height, items));
    }

    private static void assertShapeless(GameTestHelper helper, String expectedPath, Item... ingredients) {
        List<ItemStack> items = new ArrayList<>(ingredients.length);
        for (Item ingredient : ingredients) {
            items.add(new ItemStack(ingredient));
        }
        assertCrafts(helper, expectedPath, CraftingInput.of(ingredients.length, 1, items));
    }

    private static void assertCrafts(GameTestHelper helper, String expectedPath, CraftingInput input) {
        Level level = helper.getLevel();
        Optional<RecipeHolder<CraftingRecipe>> holder = helper.getLevel().recipeAccess()
                .getRecipeFor(RecipeType.CRAFTING, input, level);
        assertTrue(helper, holder.isPresent(), "Expected a crafting recipe to match for " + expectedPath);
        if (holder.isPresent()) {
            ItemStack result = holder.get().value().assemble(input);
            Identifier resultId = BuiltInRegistries.ITEM.getKey(result.getItem());
            assertTrue(helper, Mod.id(expectedPath).equals(resultId),
                    "Expected " + expectedPath + " recipe to craft xercamusic:" + expectedPath + " but got " + resultId);
        }
    }

    @GameTest
    public void stringInstrumentsCraft(GameTestHelper helper) {
        assertShaped(helper, "guitar", new String[]{" s ", "pip", " p "},
                key('p', Items.OAK_PLANKS, 's', Items.STICK, 'i', Items.STRING));
        assertShaped(helper, "violin", new String[]{" s ", "pip", " p "},
                key('p', Items.SPRUCE_PLANKS, 's', Items.STICK, 'i', Items.STRING));
        assertShaped(helper, "cello", new String[]{" s ", "pip", " p "},
                key('p', Items.DARK_OAK_PLANKS, 's', Items.STICK, 'i', Items.STRING));
        assertShaped(helper, "banjo", new String[]{" s ", "pip", " p "},
                key('p', Items.ACACIA_PLANKS, 's', Items.STICK, 'i', Items.STRING));
        assertShaped(helper, "lyre", new String[]{"sis", " s "},
                key('s', Items.STICK, 'i', Items.STRING));
        assertShaped(helper, "bass_guitar", new String[]{" s ", "pip", "rpr"},
                key('p', Items.SPRUCE_PLANKS, 's', Items.STICK, 'i', Items.STRING, 'r', Items.REDSTONE));
        assertShaped(helper, "redstone_guitar", new String[]{" s ", "pip", "rpr"},
                key('p', Items.OAK_PLANKS, 's', Items.STICK, 'i', Items.STRING, 'r', Items.REDSTONE));
        helper.succeed();
    }

    @GameTest
    public void percussionInstrumentsCraft(GameTestHelper helper) {
        assertShaped(helper, "drum", new String[]{" p ", "plp", " p "},
                key('p', Items.OAK_PLANKS, 'l', Items.LEATHER));
        assertShaped(helper, "cymbal", new String[]{" g ", "gig", " g "},
                key('g', Items.GOLD_NUGGET, 'i', Items.IRON_NUGGET));
        assertShaped(helper, "xylophone", new String[]{"sss", "ppp"},
                key('s', Items.STICK, 'p', Items.OAK_PLANKS));
        assertShaped(helper, "tubular_bell", new String[]{"sss", "g g"},
                key('s', Items.STICK, 'g', Items.GOLD_NUGGET));
        assertShaped(helper, "sansula", new String[]{"iii", "ppp"},
                key('i', Items.IRON_NUGGET, 'p', Items.OAK_PLANKS));
        assertShaped(helper, "drum_kit", new String[]{"c c", "ddd"},
                key('c', xerca.xercamusic.common.item.Items.CYMBAL, 'd', xerca.xercamusic.common.item.Items.DRUM));
        helper.succeed();
    }

    @GameTest
    public void windInstrumentsCraft(GameTestHelper helper) {
        assertShaped(helper, "flute", new String[]{"sss"},
                key('s', Items.STICK));
        assertShaped(helper, "oboe", new String[]{"iii", "sss"},
                key('s', Items.STICK, 'i', Items.IRON_NUGGET));
        assertShaped(helper, "saxophone", new String[]{"  g", "g g", " g "},
                key('g', Items.GOLD_NUGGET));
        assertShaped(helper, "trumpet", new String[]{"  g", "gg ", "gg "},
                key('g', Items.GOLD_NUGGET));
        assertShaped(helper, "french_horn", new String[]{"  g", "ggg", "gg "},
                key('g', Items.GOLD_NUGGET));
        helper.succeed();
    }

    @GameTest
    public void keyboardInstrumentsCraft(GameTestHelper helper) {
        assertShaped(helper, "piano", new String[]{"ppp", "sis", "ppp"},
                key('p', Items.OAK_PLANKS, 's', Items.STRING, 'i', Items.IRON_NUGGET));
        assertShaped(helper, "redstone_piano", new String[]{"ppp", "srs", "ppp"},
                key('p', Items.OAK_PLANKS, 's', Items.STRING, 'r', Items.REDSTONE));
        assertShaped(helper, "organ", new String[]{"ppp", "sgs", "ppp"},
                key('p', Items.OAK_PLANKS, 's', Items.STRING, 'g', Items.GOLD_NUGGET));
        helper.succeed();
    }

    @GameTest
    public void musicItemsCraft(GameTestHelper helper) {
        assertShaped(helper, "music_box", new String[]{"www", "wew", "wrw"},
                key('w', Items.OAK_PLANKS, 'e', Items.GOLD_INGOT, 'r', Items.REDSTONE));
        assertShapeless(helper, "music_sheet", Items.PAPER, Items.INK_SAC, Items.FEATHER);
        assertShapeless(helper, "metronome", Items.NOTE_BLOCK, Items.CLOCK);
        helper.succeed();
    }
}
