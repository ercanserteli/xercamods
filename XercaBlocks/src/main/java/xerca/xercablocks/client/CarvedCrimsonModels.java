package xerca.xercablocks.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
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
            // The overlay models are not referenced by any blockstate file, so mark them for resolution.
            pluginContext.addModel(ExtraModelKey.create(() -> "xercablocks:carved_crimson_overlays"), new UnbakedExtraModel<Unit>() {
                @Override
                public void resolveDependencies(ResolvableModel.Resolver resolver) {
                    for (ResourceLocation id : OVERLAY_MODELS) {
                        resolver.markDependency(id);
                    }
                }

                @Override
                public Unit bake(ModelBaker baker) {
                    return Unit.INSTANCE;
                }
            });
            pluginContext.modifyBlockModelAfterBake().register(ModelModifier.WRAP_LAST_PHASE, CarvedCrimsonModels::wrapBlockModel);
        });
    }

    private static BlockStateModel wrapBlockModel(BlockStateModel model, ModelModifier.AfterBakeBlock.Context context) {
        BlockState state = context.state();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!Mod.MOD_ID.equals(blockId.getNamespace()) || !blockId.getPath().startsWith(BLOCK_PREFIX)) {
            return model;
        }

        BlockModelPart overlayPart = SimpleModelWrapper.bake(context.baker(), overlayModelId(blockId.getPath()), rotationFor(state));
        return new EmissiveOverlayBlockStateModel(model, overlayPart);
    }

    private static ModelState rotationFor(BlockState state) {
        return switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
            case WEST -> BlockModelRotation.X0_Y90;
            case NORTH -> BlockModelRotation.X0_Y180;
            case EAST -> BlockModelRotation.X0_Y270;
            default -> BlockModelRotation.X0_Y0;
        };
    }

    private static ResourceLocation overlayModelId(String blockId) {
        return Mod.id("block/carved_wood/" + blockId + "_overlay");
    }
}
