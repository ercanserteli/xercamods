package xerca.xercablocks.tests.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(TextureAtlas.class)
public interface TextureAtlasAccessor {
    @Accessor("animatedTexturesStates")
    List<SpriteContents.AnimationState> xercablocksTests$getAnimatedTexturesStates();

    @Accessor("animatedTexturesStates")
    void xercablocksTests$setAnimatedTexturesStates(List<SpriteContents.AnimationState> animatedTexturesStates);

    @Accessor("texturesByName")
    Map<Identifier, TextureAtlasSprite> xercablocksTests$getTexturesByName();

    @Accessor("maxMipLevel")
    int xercablocksTests$getMaxMipLevel();
}
