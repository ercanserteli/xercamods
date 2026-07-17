package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

import java.lang.reflect.Field;

/**
 * Shared helpers for the paint-mod client gametests.
 * <p>Input is driven by invoking the screens' overridden mouse/key handlers on the client thread. The
 * fabric client-gametest input layer always passes scancode/modifiers as {@code 0}, so ctrl-modified
 * shortcuts (canvas undo) are exercised by calling the handlers directly with an explicit modifier
 * bitmask.
 */
@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
final class PaintClientTests {
    private PaintClientTests() {
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void pressKey(ClientGameTestContext context, int keyCode, int scanCode, int modifiers) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                screen.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
            }
        });
        context.waitTick();
    }

    static void clickAt(ClientGameTestContext context, double x, double y, int button) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                MouseButtonEvent event = new MouseButtonEvent(x, y, new MouseButtonInfo(button, 0));
                screen.mouseClicked(event, false);
                screen.mouseReleased(event);
            }
        });
        context.waitTick();
    }

    static void mouseDown(ClientGameTestContext context, double x, double y, int button) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                screen.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(button, 0)), false);
            }
        });
        context.waitTick();
    }

    static void mouseDragTo(ClientGameTestContext context, double x, double y, int button, double deltaX, double deltaY) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                screen.mouseDragged(new MouseButtonEvent(x, y, new MouseButtonInfo(button, 0)), deltaX, deltaY);
            }
        });
        context.waitTick();
    }

    static void mouseUp(ClientGameTestContext context, double x, double y, int button) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                screen.mouseReleased(new MouseButtonEvent(x, y, new MouseButtonInfo(button, 0)));
            }
        });
        context.waitTick();
    }

    static void typeChars(ClientGameTestContext context, String text) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                for (int i = 0; i < text.length(); i++) {
                    screen.charTyped(new CharacterEvent(text.charAt(i), 0));
                }
            }
        });
        context.waitTick();
    }

    static void scrollVertically(ClientGameTestContext context, double x, double y, double amount) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                screen.mouseScrolled(x, y, 0, amount);
            }
        });
        context.waitTick();
    }

    @SuppressWarnings({"unchecked", "TypeParameterUnusedInFormals", "RethrowReflectiveOperationExceptionAsLinkageError"})
    static <T> T readField(Object target, Class<?> owner, String name) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to read field " + owner.getSimpleName() + "." + name, e);
        }
    }
}
