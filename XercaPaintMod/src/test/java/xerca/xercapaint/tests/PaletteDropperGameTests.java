package xerca.xercapaint.tests;

import net.minecraft.gametest.framework.GameTestHelper;
import xerca.xercapaint.PaletteUtil;

@SuppressWarnings("unused")
public class PaletteDropperGameTests {

    private static void assertRgb(GameTestHelper helper, PaletteUtil.Color color, int r, int g, int b, String message) {
        TestAsserts.assertTrue(helper, color.r == r && color.g == g && color.b == b,
                message + " (actual=" + color.r + "," + color.g + "," + color.b + ")");
    }

    public void droppingPureBlackIntoCustomSlotDoesNotDivideByZero(GameTestHelper helper) {
        PaletteUtil.CustomColor slot = new PaletteUtil.CustomColor();
        PaletteUtil.Color pickedBlack = new PaletteUtil.Color(0xFF000000);

        try {
            slot.mix(pickedBlack);
            slot.mix(pickedBlack);
        } catch (ArithmeticException e) {
            TestAsserts.assertTrue(helper, false, "Dropping pure black caused arithmetic error: " + e.getMessage());
            return;
        }

        PaletteUtil.Color mixed = slot.getColor();
        TestAsserts.assertTrue(helper, slot.getNumberOfColors() == 2, "Expected black to be added twice");
        TestAsserts.assertTrue(helper, mixed.r == 0 && mixed.g == 0 && mixed.b == 0, "Expected resulting slot color to remain pure black");
        helper.succeed();
    }

    public void droppingSingleColorKeepsExactColor(GameTestHelper helper) {
        PaletteUtil.CustomColor slot = new PaletteUtil.CustomColor();
        PaletteUtil.Color picked = new PaletteUtil.Color(0xFF0C2238);
        slot.mix(picked);

        TestAsserts.assertTrue(helper, slot.getNumberOfColors() == 1, "Expected single dropped color count");
        assertRgb(helper, slot.getColor(), 12, 34, 56, "Expected custom slot color to equal dropped color");
        helper.succeed();
    }

    public void droppingTwoSaturatedColorsMixesPredictably(GameTestHelper helper) {
        PaletteUtil.CustomColor slot = new PaletteUtil.CustomColor();
        slot.mix(new PaletteUtil.Color(0xFFFF0000)); // red
        slot.mix(new PaletteUtil.Color(0xFF0000FF)); // blue

        TestAsserts.assertTrue(helper, slot.getNumberOfColors() == 2, "Expected two dropped colors count");
        // Integer gain scaling in PaletteUtil.CustomColor should produce 254 for red/blue mix.
        assertRgb(helper, slot.getColor(), 254, 0, 254, "Expected red+blue mixed color");
        helper.succeed();
    }

    public void droppingManyColorsAlwaysStaysInRgbRange(GameTestHelper helper) {
        PaletteUtil.CustomColor slot = new PaletteUtil.CustomColor();
        int[] colors = {
                0xFF000000,
                0xFFFFFFFF,
                0xFFFF0000,
                0xFF00FF00,
                0xFF0000FF,
                0xFF123456,
                0xFFCC8844
        };
        for (int rgb : colors) {
            slot.mix(new PaletteUtil.Color(rgb));
            PaletteUtil.Color current = slot.getColor();
            TestAsserts.assertTrue(helper, current.r >= 0 && current.r <= 255, "Red out of range after mix");
            TestAsserts.assertTrue(helper, current.g >= 0 && current.g <= 255, "Green out of range after mix");
            TestAsserts.assertTrue(helper, current.b >= 0 && current.b <= 255, "Blue out of range after mix");
        }

        TestAsserts.assertTrue(helper, slot.getNumberOfColors() == colors.length, "Expected all dropped colors to be counted");
        helper.succeed();
    }
}
