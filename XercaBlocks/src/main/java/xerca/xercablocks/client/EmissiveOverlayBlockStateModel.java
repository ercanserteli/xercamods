package xerca.xercablocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EmissiveOverlayBlockStateModel extends DelegateBlockStateModel {
    private static final int FULL_LIGHT_EMISSION = 15;
    private final StandaloneModelKey<BlockStateModelPart> overlayKey;
    private @Nullable BlockStateModelPart cachedOverlay;

    public EmissiveOverlayBlockStateModel(BlockStateModel base, StandaloneModelKey<BlockStateModelPart> overlayKey) {
        super(base);
        this.overlayKey = overlayKey;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        super.collectParts(level, pos, state, random, parts);
        BlockStateModelPart overlay = overlay();
        if (overlay != null) {
            parts.add(overlay);
        }
    }

    private @Nullable BlockStateModelPart overlay() {
        if (cachedOverlay == null) {
            cachedOverlay = Minecraft.getInstance().getModelManager().getStandaloneModel(overlayKey);
        }
        return cachedOverlay;
    }

    // Renders the overlay part fullbright and translucent, without diffuse shading or ambient occlusion.
    static BlockStateModelPart makeEmissive(BlockStateModelPart source) {
        return new EmissiveModelPart(source);
    }

    private static final class EmissiveModelPart implements BlockStateModelPart {
        private final BlockStateModelPart source;
        private final Map<Direction, List<BakedQuad>> quadsByDirection = new HashMap<>();
        private final List<BakedQuad> generalQuads;

        private EmissiveModelPart(BlockStateModelPart source) {
            this.source = source;
            for (Direction direction : Direction.values()) {
                quadsByDirection.put(direction, emissiveQuads(source.getQuads(direction)));
            }
            this.generalQuads = emissiveQuads(source.getQuads(null));
        }

        private static List<BakedQuad> emissiveQuads(List<BakedQuad> quads) {
            List<BakedQuad> result = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) {
                result.add(toEmissive(quad));
            }
            return result;
        }

        private static BakedQuad toEmissive(BakedQuad quad) {
            BakedQuad.MaterialInfo info = quad.materialInfo();
            BakedQuad.MaterialInfo emissive = new BakedQuad.MaterialInfo(info.sprite(), ChunkSectionLayer.TRANSLUCENT,
                    info.itemRenderType(), info.tintIndex(), false, FULL_LIGHT_EMISSION);
            return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                    quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(), quad.direction(), emissive);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            return direction == null ? generalQuads : quadsByDirection.get(direction);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public Material.Baked particleMaterial() {
            return source.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return source.materialFlags() | BakedQuad.FLAG_TRANSLUCENT;
        }
    }
}
