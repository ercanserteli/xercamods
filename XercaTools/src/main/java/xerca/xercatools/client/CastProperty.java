package xerca.xercatools.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xerca.xercatools.Mod;
import xerca.xercatools.item.ItemGrabHook;

public record CastProperty() implements ConditionalItemModelProperty {
    public static final Identifier ID = Mod.id("cast");
    public static final MapCodec<CastProperty> MAP_CODEC = MapCodec.unit(new CastProperty());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return ItemGrabHook.isCast(stack);
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }
}
