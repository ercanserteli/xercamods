package xerca.xercablocks.client;

import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.dispatch.*;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import xerca.xercablocks.Mod;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public final class CarvedCrimsonModels {
    private static final String BLOCK_PREFIX = "carved_crimson_";
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final Map<OverlayKey, StandaloneModelKey<BlockStateModelPart>> OVERLAY_KEYS = new HashMap<>();

    private record OverlayKey(Identifier overlayId, Direction facing) {
    }

    private CarvedCrimsonModels() {
    }

    @SubscribeEvent
    public static void onRegisterStandalone(ModelEvent.RegisterStandalone event) {
        OVERLAY_KEYS.clear();
        for (int i = 1; i <= 8; i++) {
            Identifier overlayId = overlayModelId(BLOCK_PREFIX + i);
            for (Direction facing : HORIZONTAL) {
                ModelState rotation = rotationFor(facing);
                StandaloneModelKey<BlockStateModelPart> key = new StandaloneModelKey<>(() -> overlayId + "#" + facing.getName());
                event.register(key, new SimpleUnbakedStandaloneModel<>(overlayId,
                        (model, baker, name) -> EmissiveOverlayBlockStateModel.makeEmissive(SimpleModelWrapper.bake(baker, overlayId, rotation))));
                OVERLAY_KEYS.put(new OverlayKey(overlayId, facing), key);
            }
        }
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<BlockState, BlockStateModel> models = event.getBakingResult().blockStateModels();
        for (Map.Entry<BlockState, BlockStateModel> entry : models.entrySet()) {
            BlockState state = entry.getKey();
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (!Mod.MOD_ID.equals(blockId.getNamespace()) || !blockId.getPath().startsWith(BLOCK_PREFIX)) {
                continue;
            }
            Identifier overlayId = overlayModelId(blockId.getPath());
            Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
            StandaloneModelKey<BlockStateModelPart> key = OVERLAY_KEYS.get(new OverlayKey(overlayId, facing));
            if (key != null) {
                entry.setValue(new EmissiveOverlayBlockStateModel(entry.getValue(), key));
            }
        }
    }

    private static ModelState rotationFor(Direction facing) {
        return switch (facing) {
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
