package xerca.xercablocks.client;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public final class EmissiveOverlayBlockStateModel extends WrapperBlockStateModel {
    private final BlockModelPart overlayPart;

    public EmissiveOverlayBlockStateModel(BlockStateModel baseModel, BlockModelPart overlayPart) {
        super(baseModel);
        this.overlayPart = overlayPart;
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
        super.emitQuads(emitter, blockView, pos, state, random, cullTest);

        emitter.pushTransform(quad -> {
            quad.renderLayer(ChunkSectionLayer.TRANSLUCENT)
                    .emissive(true)
                    .diffuseShade(false)
                    .ambientOcclusion(TriState.FALSE);
            return true;
        });
        try {
            overlayPart.emitQuads(emitter, cullTest);
        } finally {
            emitter.popTransform();
        }
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter blockView, BlockPos pos, BlockState state, RandomSource random) {
        Object baseKey = wrapped.createGeometryKey(blockView, pos, state, random);
        if (baseKey == null) {
            return null;
        }
        record Key(Object baseKey, EmissiveOverlayBlockStateModel model) {
        }
        return new Key(baseKey, this);
    }
}
