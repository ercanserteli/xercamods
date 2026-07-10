package xerca.xercablocks.tests.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(TextureAtlas.class)
public interface TextureAtlasAccessor {
    @Accessor("animatedTextures")
    List<TextureAtlasSprite.Ticker> xercablocksTests$getAnimatedTextures();

    @Accessor("animatedTextures")
    void xercablocksTests$setAnimatedTextures(List<TextureAtlasSprite.Ticker> animatedTextures);

    @Accessor("texturesByName")
    Map<ResourceLocation, TextureAtlasSprite> xercablocksTests$getTexturesByName();
}
