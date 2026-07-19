package xerca.xercatools.tests;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

final class TestAsserts {
    private TestAsserts() {
    }

    static void assertTrue(GameTestHelper helper, boolean condition, String message) {
        helper.assertTrue(condition, Component.literal(message));
    }

    static void assertFalse(GameTestHelper helper, boolean condition, String message) {
        helper.assertFalse(condition, Component.literal(message));
    }

}
