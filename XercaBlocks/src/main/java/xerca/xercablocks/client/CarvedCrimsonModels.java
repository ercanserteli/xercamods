package xerca.xercablocks.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xerca.xercablocks.Mod;

public final class CarvedCrimsonModels {
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
            pluginContext.modifyModelAfterBake().register(ModelModifier.WRAP_LAST_PHASE, CarvedCrimsonModels::wrapCrimsonModel);
        });
    }

    private static @Nullable BakedModel wrapCrimsonModel(@Nullable BakedModel model, ModelModifier.AfterBake.Context context) {
        if (model == null) {
            return null;
        }

        String overlayBasePath = getOverlayBasePath(context);
        if (overlayBasePath == null) {
            return model;
        }

        BakedModel overlayModel = context.baker().bake(overlayModelId(overlayBasePath), context.settings());
        return overlayModel == null ? model : new EmissiveOverlayBakedModel(model, overlayModel);
    }

    private static @Nullable String getOverlayBasePath(ModelModifier.AfterBake.Context context) {
        ResourceLocation resourceId = context.resourceId();
        if (resourceId != null) {
            return getWorldOverlayPath(resourceId);
        }
        return getInventoryOverlayPath(context.topLevelId());
    }

    private static @Nullable String getWorldOverlayPath(ResourceLocation resourceId) {
        if (!Mod.MOD_ID.equals(resourceId.getNamespace())) {
            return null;
        }
        String path = resourceId.getPath();
        if (!path.startsWith("block/carved_wood/carved_crimson_") || path.endsWith("_overlay")) {
            return null;
        }
        return path.substring("block/carved_wood/".length());
    }

    private static @Nullable String getInventoryOverlayPath(@Nullable ModelResourceLocation topLevelId) {
        if (topLevelId == null || !"inventory".equals(topLevelId.variant())) {
            return null;
        }
        ResourceLocation id = topLevelId.id();
        if (!Mod.MOD_ID.equals(id.getNamespace())) {
            return null;
        }
        String path = id.getPath();
        return path.startsWith("carved_crimson_") ? path : null;
    }

    private static ResourceLocation overlayModelId(String blockId) {
        return Mod.id("block/carved_wood/" + blockId + "_overlay");
    }
}
