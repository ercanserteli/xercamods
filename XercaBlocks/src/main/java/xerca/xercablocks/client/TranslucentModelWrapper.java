package xerca.xercablocks.client;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

// Like minecraft:model but renders on the translucent item sheet, so semi-transparent
// overlay textures alpha-blend over the layer below instead of writing raw cutout alpha
// (which the GUI item atlas would then composite against the screen behind the GUI).
public final class TranslucentModelWrapper implements ItemModel {
    private final QuadCollection quads;
    private final Supplier<Vector3fc[]> extents;
    private final ModelRenderProperties properties;
    private final Matrix4fc transformation;

    TranslucentModelWrapper(QuadCollection quads, ModelRenderProperties properties, Matrix4fc transformation) {
        this.quads = quads;
        this.properties = properties;
        this.transformation = transformation;
        this.extents = Suppliers.memoize(() -> CuboidItemModelWrapper.computeExtents(quads.getAll()));
    }

    @Override
    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        renderState.appendModelIdentityElement(this);
        ItemStackRenderState.LayerRenderState layer = renderState.newLayer();
        layer.setExtents(this.extents);
        layer.setLocalTransform(this.transformation);
        this.properties.applyToLayer(layer, displayContext);
        List<BakedQuad> layerQuads = layer.prepareQuadList();
        for (BakedQuad quad : this.quads.getAll()) {
            layerQuads.add(withItemRenderType(quad, Sheets.translucentBlockItemSheet()));
        }
        if (this.quads.hasMaterialFlag(BakedQuad.FLAG_ANIMATED)) {
            renderState.setAnimated();
        }
    }

    private static BakedQuad withItemRenderType(BakedQuad quad, RenderType renderType) {
        BakedQuad.MaterialInfo info = quad.materialInfo();
        BakedQuad.MaterialInfo updated = new BakedQuad.MaterialInfo(info.sprite(), info.layer(), renderType,
                info.tintIndex(), info.shade(), info.lightEmission());
        return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(), quad.direction(), updated);
    }

    public record Unbaked(Identifier model) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model)
                ).apply(instance, Unbaked::new)
        );

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel resolvedModel = baker.getModel(this.model);
            TextureSlots textureSlots = resolvedModel.getTopTextureSlots();
            QuadCollection quads = resolvedModel.bakeTopGeometry(textureSlots, baker, BlockModelRotation.IDENTITY);
            ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, textureSlots);
            return new TranslucentModelWrapper(quads, properties, transformation);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
