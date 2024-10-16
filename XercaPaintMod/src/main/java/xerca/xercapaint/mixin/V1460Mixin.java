package xerca.xercapaint.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V1460.class)
public abstract class V1460Mixin {
    @Inject(method = "registerEntities", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void registerEntitiesMixin(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir, Map<String, Supplier<TypeTemplate>> map) {
        schema.register(map, "xercapaint:easel", (string) -> DSL.optionalFields("Item", References.ITEM_STACK.in(schema)));
        schema.register(map, "xercapaint:canvas", (string) -> DSL.optionalFields("_", References.ITEM_STACK.in(schema)));
    }
}
