package xerca.xercablocks.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.resources.model.BakedModel;
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

        ResourceLocation resourceId = context.resourceId();
        if (resourceId == null) {
            return model;
        }

        String path = resourceId.getPath();
        if (!path.startsWith("block/carved_wood/carved_crimson_") || path.endsWith("_overlay")) {
            return model;
        }

        ResourceLocation overlayId = ResourceLocation.fromNamespaceAndPath(resourceId.getNamespace(), path + "_overlay");
        BakedModel overlayModel = context.baker().bake(overlayId, context.settings());
        return overlayModel == null ? model : new EmissiveOverlayBakedModel(model, overlayModel);
    }

    private static ResourceLocation overlayModelId(String blockId) {
        return Mod.id("block/carved_wood/" + blockId + "_overlay");
    }
}
