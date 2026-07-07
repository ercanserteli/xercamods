package xerca.xercablocks.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xerca.xercablocks.Mod;

public final class CarvedCrimsonModels {
    private static final String BLOCK_PREFIX = "carved_crimson_";
    private static final ResourceLocation[] OVERLAY_MODELS = {
            overlayModelId("carved_crimson_1"),
            overlayModelId("carved_crimson_2"),
            overlayModelId("carved_crimson_3"),
            overlayModelId("carved_crimson_4"),
            overlayModelId("carved_crimson_5"),
            overlayModelId("carved_crimson_6"),
            overlayModelId("carved_crimson_7"),
            overlayModelId("carved_crimson_8")
    };

    private CarvedCrimsonModels() {
    }

    public static void register() {
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModels(OVERLAY_MODELS);
            // Inventory / item models are baked in the plain model set (keyed by ResourceLocation).
            pluginContext.modifyModelAfterBake().register(ModelModifier.WRAP_LAST_PHASE, CarvedCrimsonModels::wrapItemModel);
            // World models are baked per blockstate variant (keyed by ModelResourceLocation).
            pluginContext.modifyBlockModelAfterBake().register(ModelModifier.WRAP_LAST_PHASE, CarvedCrimsonModels::wrapBlockModel);
        });
    }

    private static @Nullable BakedModel wrapItemModel(@Nullable BakedModel model, ModelModifier.AfterBake.Context context) {
        if (model == null) {
            return null;
        }

        String overlayBasePath = getInventoryOverlayPath(context.id());
        if (overlayBasePath == null) {
            return model;
        }

        BakedModel overlayModel = context.baker().bake(overlayModelId(overlayBasePath), context.settings());
        return overlayModel == null ? model : new EmissiveOverlayBakedModel(model, overlayModel);
    }

    private static @Nullable BakedModel wrapBlockModel(@Nullable BakedModel model, ModelModifier.AfterBakeBlock.Context context) {
        if (model == null) {
            return null;
        }

        ModelResourceLocation id = context.id();
        String overlayBasePath = getWorldOverlayPath(id.id());
        if (overlayBasePath == null) {
            return model;
        }

        // The block context has no settings(), so rebuild the rotation from the facing variant.
        ModelState state = rotationForVariant(id.variant());
        BakedModel overlayModel = context.baker().bake(overlayModelId(overlayBasePath), state);
        return overlayModel == null ? model : new EmissiveOverlayBakedModel(model, overlayModel);
    }

    private static @Nullable String getWorldOverlayPath(ResourceLocation blockId) {
        if (!Mod.MOD_ID.equals(blockId.getNamespace())) {
            return null;
        }
        String path = blockId.getPath();
        return path.startsWith(BLOCK_PREFIX) ? path : null;
    }

    private static @Nullable String getInventoryOverlayPath(ResourceLocation modelId) {
        if (!Mod.MOD_ID.equals(modelId.getNamespace())) {
            return null;
        }
        String path = modelId.getPath();
        if (!path.startsWith("item/")) {
            return null;
        }
        String name = path.substring("item/".length());
        return name.startsWith(BLOCK_PREFIX) ? name : null;
    }

    private static ModelState rotationForVariant(String variant) {
        return switch (variant) {
            case "facing=west" -> BlockModelRotation.by(0, 90);
            case "facing=north" -> BlockModelRotation.by(0, 180);
            case "facing=east" -> BlockModelRotation.by(0, 270);
            default -> BlockModelRotation.by(0, 0);
        };
    }

    private static ResourceLocation overlayModelId(String blockId) {
        return Mod.id("block/carved_wood/" + blockId + "_overlay");
    }
}
