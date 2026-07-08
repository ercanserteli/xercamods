package xerca.xercapaint.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.Mod;
import xerca.xercapaint.item.ItemPalette;

// number of basic colors on the palette (0..16).
public record ColorsProperty() implements RangeSelectItemModelProperty {
    public static final ResourceLocation ID = Mod.id("palette_colors");
    public static final MapCodec<ColorsProperty> MAP_CODEC = MapCodec.unit(new ColorsProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return ItemPalette.basicColorCount(stack);
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
