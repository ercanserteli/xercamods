package xerca.xercamusic.client;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

/**
 * Shared helpers for the music-mod client gametests. These tests live in
 * {@code xerca.xercamusic.client} so they can read the GUIs' package-private editing state directly;
 * only the few genuinely private fields are read reflectively (mirroring the reflection already used
 * by the server game tests).
 *
 * <p>Input is driven by invoking the screens' overridden handlers on the client thread. The fabric
 * client-gametest input layer always passes scancode/modifiers as {@code 0} and cannot make
 * {@code GLFW.glfwGetKey} observe simulated keys, so directly calling the handlers is the only way to
 * exercise scancode-based note keys and the ctrl-modified editing shortcuts.
 */
@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
final class MusicClientTests {
    private MusicClientTests() {
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

    static void releaseKey(ClientGameTestContext context, int keyCode, int scanCode, int modifiers) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                screen.keyReleased(new KeyEvent(keyCode, scanCode, modifiers));
            }
        });
        context.waitTick();
    }

    /**
     * Presses a note on the on-screen keyboard by its offset from the first note key (Q).
     */
    static void pressNoteKey(ClientGameTestContext context, int noteKeyOffset, int modifiers) {
        pressKey(context, GLFW.GLFW_KEY_UNKNOWN, firstNoteScanCode(context) + noteKeyOffset, modifiers);
    }

    static void releaseNoteKey(ClientGameTestContext context, int noteKeyOffset, int modifiers) {
        releaseKey(context, GLFW.GLFW_KEY_UNKNOWN, firstNoteScanCode(context) + noteKeyOffset, modifiers);
    }

    static int firstNoteScanCode(ClientGameTestContext context) {
        return context.computeOnClient(client -> GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_Q));
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

    static void scrollVertically(ClientGameTestContext context, double amount) {
        context.runOnClient(client -> {
            Screen screen = client.screen;
            if (screen != null) {
                screen.mouseScrolled(0, 0, 0, amount);
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
