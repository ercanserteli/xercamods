package xerca.xercablocks.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps a base block model with a second "overlay" model rendered fullbright and translucent,
 * without diffuse shading or ambient occlusion. Replaces the Fabric {@code FabricBakedModel}
 * emissive-material approach: the overlay quads are pre-transformed to max emissivity and routed
 * onto the translucent chunk layer so their semi-transparent pixels alpha-blend over the base.
 */
public final class EmissiveOverlayBakedModel implements IDynamicBakedModel {
    private static final IQuadTransformer EMISSIVE = QuadTransformers.settingMaxEmissivity();
    private static final ChunkRenderTypeSet OVERLAY_RENDER_TYPES = ChunkRenderTypeSet.of(RenderType.translucent());

    private final BakedModel base;
    private final Map<Direction, List<BakedQuad>> overlayQuads = new EnumMap<>(Direction.class);
    private final List<BakedQuad> overlayGeneralQuads;

    public EmissiveOverlayBakedModel(BakedModel base, BakedModel overlay) {
        this.base = base;
        RandomSource random = RandomSource.create(42L);
        for (Direction direction : Direction.values()) {
            overlayQuads.put(direction, emissiveQuads(overlay.getQuads(null, direction, random)));
        }
        this.overlayGeneralQuads = emissiveQuads(overlay.getQuads(null, null, random));
    }

    private static List<BakedQuad> emissiveQuads(List<BakedQuad> quads) {
        List<BakedQuad> result = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            BakedQuad bright = EMISSIVE.process(quad);
            // Drop diffuse shading and ambient occlusion so the overlay reads as a uniform glow.
            result.add(new BakedQuad(bright.getVertices(), bright.getTintIndex(), bright.getDirection(),
                    bright.getSprite(), false, false));
        }
        return result;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> result = new ArrayList<>();
        if (renderType == null || (state != null && base.getRenderTypes(state, rand, data).contains(renderType))) {
            result.addAll(base.getQuads(state, side, rand, data, renderType));
        }
        if (renderType == null || OVERLAY_RENDER_TYPES.contains(renderType)) {
            result.addAll(side == null ? overlayGeneralQuads : overlayQuads.get(side));
        }
        return result;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return ChunkRenderTypeSet.union(base.getRenderTypes(state, rand, data), OVERLAY_RENDER_TYPES);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return base.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return base.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return base.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return base.getOverrides();
    }
}
