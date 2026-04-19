package xerca.xercablocks.client;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class EmissiveOverlayBakedModel implements BakedModel, FabricBakedModel {
    private static @Nullable RenderMaterial emissiveOverlayMaterial;

    private final BakedModel baseModel;
    private final BakedModel overlayModel;

    public EmissiveOverlayBakedModel(BakedModel baseModel, BakedModel overlayModel) {
        this.baseModel = baseModel;
        this.overlayModel = overlayModel;
    }

    @Override
    public boolean isVanillaAdapter() {
        return overlayMaterial() == null;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        ((FabricBakedModel) baseModel).emitBlockQuads(blockView, state, pos, randomSupplier, context);
        emitOverlay(context, () -> ((FabricBakedModel) overlayModel).emitBlockQuads(blockView, state, pos, randomSupplier, context));
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        ((FabricBakedModel) baseModel).emitItemQuads(stack, randomSupplier, context);
        emitOverlay(context, () -> ((FabricBakedModel) overlayModel).emitItemQuads(stack, randomSupplier, context));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
        List<BakedQuad> baseQuads = baseModel.getQuads(state, side, random);
        List<BakedQuad> overlayQuads = overlayModel.getQuads(state, side, random);

        if (overlayQuads.isEmpty()) {
            return baseQuads;
        }

        if (baseQuads.isEmpty()) {
            return overlayQuads;
        }

        List<BakedQuad> combinedQuads = new ArrayList<>(baseQuads.size() + overlayQuads.size());
        combinedQuads.addAll(baseQuads);
        combinedQuads.addAll(overlayQuads);
        return combinedQuads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return baseModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return baseModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return baseModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return baseModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return baseModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return baseModel.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return baseModel.getOverrides();
    }

    private void emitOverlay(RenderContext context, Runnable overlayEmitter) {
        RenderMaterial overlayMaterial = overlayMaterial();
        if (overlayMaterial == null) {
            return;
        }

        context.pushTransform(quad -> {
            quad.material(overlayMaterial);
            return true;
        });

        try {
            overlayEmitter.run();
        } finally {
            context.popTransform();
        }
    }

    private static @Nullable RenderMaterial overlayMaterial() {
        if (emissiveOverlayMaterial == null && RendererAccess.INSTANCE.getRenderer() != null) {
            emissiveOverlayMaterial = RendererAccess.INSTANCE.getRenderer()
                    .materialFinder()
                    .blendMode(BlendMode.TRANSLUCENT)
                    .emissive(true)
                    .disableDiffuse(true)
                    .ambientOcclusion(TriState.FALSE)
                    .find();
        }

        return emissiveOverlayMaterial;
    }
}
