package xerca.xercaomnichest.tests;

import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonAlgorithm;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

/**
 * Screenshot comparison using the Structural Similarity Index (SSIM).
 * Mean SSIM is computed over non-overlapping windows of the grayscale (luminance) images;
 * a match is reported when the mean SSIM meets the configured threshold.
 */
public record SsimComparisonAlgorithm(double threshold, int window) implements TestScreenshotComparisonAlgorithm {
    // Stabilization constants from the SSIM paper for 8-bit images: (K*L)^2 with K1=0.01, K2=0.03, L=255.
    private static final double C1 = 6.5025;
    private static final double C2 = 58.5225;

    public static SsimComparisonAlgorithm withThreshold(double threshold) {
        return new SsimComparisonAlgorithm(threshold, 8);
    }

    @Override
    public @Nullable Vector2i findColor(RawImage<int[]> haystack, RawImage<int[]> needle) {
        int hw = haystack.width();
        int hh = haystack.height();
        int nw = needle.width();
        int nh = needle.height();
        if (nw > hw || nh > hh) {
            return null;
        }

        double[] needleLum = toLuminance(needle.data(), 0, 0, nw, nw, nh);
        int[] haystackData = haystack.data();

        for (int offY = 0; offY <= hh - nh; offY++) {
            for (int offX = 0; offX <= hw - nw; offX++) {
                double[] regionLum = toLuminance(haystackData, offX, offY, hw, nw, nh);
                if (meanSsim(regionLum, needleLum, nw, nh) >= threshold) {
                    return new Vector2i(offX, offY);
                }
            }
        }
        return null;
    }

    /**
     * Compares two equal-sized RGB images directly, reporting whether mean SSIM meets the threshold.
     */
    public boolean matchesEqualSize(int[] imageA, int[] imageB, int width, int height) {
        double[] lumA = toLuminance(imageA, 0, 0, width, width, height);
        double[] lumB = toLuminance(imageB, 0, 0, width, width, height);
        return meanSsim(lumA, lumB, width, height) >= threshold;
    }

    /**
     * Extract an nw x nh luminance block from {@code data} (row stride {@code stride}) at (offX, offY).
     */
    private static double[] toLuminance(int[] data, int offX, int offY, int stride, int nw, int nh) {
        double[] lum = new double[nw * nh];
        for (int y = 0; y < nh; y++) {
            for (int x = 0; x < nw; x++) {
                int rgb = data[(offY + y) * stride + offX + x];
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                lum[y * nw + x] = 0.299 * r + 0.587 * g + 0.114 * b;
            }
        }
        return lum;
    }

    /**
     * Average SSIM over non-overlapping windows; falls back to a single global window for tiny images.
     */
    private double meanSsim(double[] a, double[] b, int width, int height) {
        int win = window;
        double total = 0.0;
        int count = 0;
        for (int y = 0; y + win <= height; y += win) {
            for (int x = 0; x + win <= width; x += win) {
                total += windowSsim(a, b, width, x, y, win);
                count++;
            }
        }
        if (count == 0) {
            return windowSsim(a, b, width, 0, 0, Math.min(width, height));
        }
        return total / count;
    }

    private static double windowSsim(double[] a, double[] b, int stride, int startX, int startY, int win) {
        int n = win * win;
        double sumA = 0, sumB = 0, sumAA = 0, sumBB = 0, sumAB = 0;
        for (int y = 0; y < win; y++) {
            for (int x = 0; x < win; x++) {
                int i = (startY + y) * stride + startX + x;
                double va = a[i];
                double vb = b[i];
                sumA += va;
                sumB += vb;
                sumAA += va * va;
                sumBB += vb * vb;
                sumAB += va * vb;
            }
        }
        double muA = sumA / n;
        double muB = sumB / n;
        double varA = sumAA / n - muA * muA;
        double varB = sumBB / n - muB * muB;
        double covAB = sumAB / n - muA * muB;

        double numerator = (2 * muA * muB + C1) * (2 * covAB + C2);
        double denominator = (muA * muA + muB * muB + C1) * (varA + varB + C2);
        return numerator / denominator;
    }
}
