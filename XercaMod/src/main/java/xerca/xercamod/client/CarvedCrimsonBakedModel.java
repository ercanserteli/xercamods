package xerca.xercamod.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CarvedCrimsonBakedModel implements BakedModel {
    private final BakedModel model;
    public CarvedCrimsonBakedModel(BakedModel model)
    {
        this.model = model;
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
    {
        List<BakedQuad> quads = this.model.getQuads(state, side, rand, data, renderType);
        if(renderType == RenderType.translucent()) {
            List<BakedQuad> newQuads = new ArrayList<>(6);
            for (BakedQuad quad : quads) {
                int[] vertexData = quad.getVertices();
                for (int j = 0; j < 4; j++) {
                    vertexData[8 * j + 6] = Math.max(getLightValue(0, 12), vertexData[8 * j + 6]);
                }
                newQuads.add(new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade()));
            }
            return newQuads;
        }
        return quads;
    }

    private static final int UPPER_HALF = 65536; // 2^16
    @SuppressWarnings("SameParameterValue")
    private static int getLightValue(int skyLighting, int blockLighting) {
        return UPPER_HALF * skyLighting * 16 + blockLighting * 16;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
        throw new AssertionError("IBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return model.isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return model.getOverrides();
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
    {
        return ChunkRenderTypeSet.of(RenderType.cutout(), RenderType.translucent());
    }
}