package xerca.xercapaint;

import java.util.Arrays;

/**
 * Geometry helpers for the paintable canvas sides.
 */
public final class CanvasSides {
    private CanvasSides() {
    }

    public static final int DEFAULT_COLOR = 0xFFF9FFFE;

    public static int count(CanvasType type) {
        return 2 * CanvasType.getWidth(type) + 2 * CanvasType.getHeight(type);
    }

    public static int topOffset() {
        return 0;
    }

    public static int bottomOffset(CanvasType type) {
        return CanvasType.getWidth(type);
    }

    public static int leftOffset(CanvasType type) {
        return 2 * CanvasType.getWidth(type);
    }

    public static int rightOffset(CanvasType type) {
        return 2 * CanvasType.getWidth(type) + CanvasType.getHeight(type);
    }

    public static int[] defaultPixels(CanvasType type, boolean isGlass) {
        int[] pixels = new int[count(type)];
        Arrays.fill(pixels, isGlass ? 0 : DEFAULT_COLOR);
        return pixels;
    }
}
