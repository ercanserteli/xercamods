package xerca.xercablocks.client;

import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
import net.minecraft.client.renderer.block.dispatch.*;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercablocks.Mod;

public final class CarvedCrimsonModels {
    private static final String BLOCK_PREFIX = "carved_crimson_";
    private static final Identifier[] OVERLAY_MODELS = {
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
                    for (Identifier id : OVERLAY_MODELS) {
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
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!Mod.MOD_ID.equals(blockId.getNamespace()) || !blockId.getPath().startsWith(BLOCK_PREFIX)) {
            return model;
        }

        BlockStateModelPart overlayPart = SimpleModelWrapper.bake(context.baker(), overlayModelId(blockId.getPath()), rotationFor(state));
        return new EmissiveOverlayBlockStateModel(model, overlayPart);
    }

    private static ModelState rotationFor(BlockState state) {
        return switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
            case WEST -> yRotation(Quadrant.R90);
            case NORTH -> yRotation(Quadrant.R180);
            case EAST -> yRotation(Quadrant.R270);
            default -> BlockModelRotation.IDENTITY;
        };
    }

    private static ModelState yRotation(Quadrant quadrant) {
        return Variant.SimpleModelState.DEFAULT.withY(quadrant).asModelState();
    }

    private static Identifier overlayModelId(String blockId) {
        return Mod.id("block/carved_wood/" + blockId + "_overlay");
    }
}
