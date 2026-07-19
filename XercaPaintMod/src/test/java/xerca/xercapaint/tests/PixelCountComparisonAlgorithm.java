package xerca.xercapaint.tests;

import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonAlgorithm;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

/**
 * Screenshot comparison by direct pixel budget: a match is reported when at most
 * {@code maxDiffering} pixels differ by more than {@code channelTolerance} on any RGB channel.
 * Stricter than SSIM for localized artifacts (e.g. ghost images) that structural similarity tolerates.
 */
public record PixelCountComparisonAlgorithm(int channelTolerance, int maxDiffering) implements TestScreenshotComparisonAlgorithm {
    @Override
    public @Nullable Vector2i findColor(RawImage<int[]> haystack, RawImage<int[]> needle) {
        int hw = haystack.width();
        int hh = haystack.height();
        int nw = needle.width();
        int nh = needle.height();
        if (nw > hw || nh > hh) {
            return null;
        }
        for (int offY = 0; offY <= hh - nh; offY++) {
            for (int offX = 0; offX <= hw - nw; offX++) {
                if (countDiffering(haystack.data(), hw, offX, offY, needle.data(), nw, nh) <= maxDiffering) {
                    return new Vector2i(offX, offY);
                }
            }
        }
        return null;
    }

    private int countDiffering(int[] haystack, int stride, int offX, int offY, int[] needle, int nw, int nh) {
        int differing = 0;
        for (int y = 0; y < nh; y++) {
            for (int x = 0; x < nw; x++) {
                int a = haystack[(offY + y) * stride + offX + x];
                int b = needle[y * nw + x];
                int dr = Math.abs(((a >> 16) & 0xFF) - ((b >> 16) & 0xFF));
                int dg = Math.abs(((a >> 8) & 0xFF) - ((b >> 8) & 0xFF));
                int db = Math.abs((a & 0xFF) - (b & 0xFF));
                if (Math.max(dr, Math.max(dg, db)) > channelTolerance) {
                    differing++;
                }
            }
        }
        return differing;
    }
}
