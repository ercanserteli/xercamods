package xerca.xercablocks.tests;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.ResourceLocation;
import xerca.xercablocks.tests.mixin.TextureAtlasAccessor;

import java.util.List;
import java.util.Map;

final class CarvedCrimsonAnimationTestHelper {
    private static final String OVERLAY_PREFIX = "block/carved_wood/carved_crimson_";

    private CarvedCrimsonAnimationTestHelper() {
    }

    static void freezeAtFirstFrame(Minecraft client) {
        TextureAtlas atlas = client.getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        TextureAtlasAccessor accessor = (TextureAtlasAccessor) atlas;
        List<TextureAtlasSprite.Ticker> tickers = accessor.xercablocksTests$getAnimatedTextures();
        tickers.forEach(TextureAtlasSprite.Ticker::close);
        accessor.xercablocksTests$setAnimatedTextures(List.of());

        int overlayCount = 0;
        for (Map.Entry<ResourceLocation, TextureAtlasSprite> entry : accessor.xercablocksTests$getTexturesByName().entrySet()) {
            ResourceLocation id = entry.getKey();
            if ("xercablocks".equals(id.getNamespace()) && id.getPath().startsWith(OVERLAY_PREFIX)
                    && id.getPath().endsWith("_o")) {
                entry.getValue().uploadFirstFrame(atlas.getTexture());
                overlayCount++;
            }
        }
        if (overlayCount == 0) {
            throw new AssertionError("No carved crimson overlay sprites were loaded");
        }
    }
}
