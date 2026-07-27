package xerca.xercablocks.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

/**
 * Custom model geometry that bakes a {@code base} and an {@code overlay} child model with the same
 * blockstate rotation, then combines them into an {@link EmissiveOverlayBakedModel} so the overlay
 * renders as a fullbright, translucent glow. Used by the carved crimson blocks (see their block
 * model JSON, {@code "loader": "xercablocks:emissive_overlay"}).
 */
public final class EmissiveOverlayGeometry implements IUnbakedGeometry<EmissiveOverlayGeometry> {
    private final BlockModel base;
    private final BlockModel overlay;

    private EmissiveOverlayGeometry(BlockModel base, BlockModel overlay) {
        this.base = base;
        this.overlay = overlay;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        BakedModel bakedBase = base.bake(baker, base, spriteGetter, modelState, true);
        BakedModel bakedOverlay = overlay.bake(baker, overlay, spriteGetter, modelState, true);
        return new EmissiveOverlayBakedModel(bakedBase, bakedOverlay);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        base.resolveParents(modelGetter);
        overlay.resolveParents(modelGetter);
    }

    public static final class Loader implements IGeometryLoader<EmissiveOverlayGeometry> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public EmissiveOverlayGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            if (!jsonObject.has("base") || !jsonObject.has("overlay")) {
                throw new JsonParseException("Emissive overlay model requires both a \"base\" and an \"overlay\" element.");
            }
            BlockModel base = deserializationContext.deserialize(jsonObject.get("base"), BlockModel.class);
            BlockModel overlay = deserializationContext.deserialize(jsonObject.get("overlay"), BlockModel.class);
            return new EmissiveOverlayGeometry(base, overlay);
        }
    }
}
