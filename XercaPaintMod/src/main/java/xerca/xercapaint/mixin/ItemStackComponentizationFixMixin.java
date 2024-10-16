package xerca.xercapaint.mixin;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xerca.xercapaint.Mod;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.item.ItemPalette;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
    @Inject(at = @At("TAIL"), method = "fixItemStack(Lnet/minecraft/util/datafix/fixes/ItemStackComponentizationFix$ItemStackData;Lcom/mojang/serialization/Dynamic;)V")
    private static void fixItemStackMixin(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> tag, CallbackInfo info) {
        if (itemStackData.is("xercapaint:item_canvas") || itemStackData.is("xercapaint:item_canvas_large") || itemStackData.is("xercapaint:item_canvas_long") || itemStackData.is("xercapaint:item_canvas_tall")) {
            Mod.LOGGER.debug("Found a canvas, porting it to the component format");

            itemStackData.moveTagToComponent("name", "xercapaint:canvas_id");
            itemStackData.moveTagToComponent("v", "xercapaint:canvas_version");
            itemStackData.moveTagToComponent("pixels", "xercapaint:canvas_pixels");
            itemStackData.moveTagToComponent("generation", "xercapaint:canvas_generation");
            itemStackData.moveTagToComponent("title", "xercapaint:canvas_title");
            itemStackData.moveTagToComponent("author", "xercapaint:canvas_author");
        }
        if (itemStackData.is("xercapaint:item_palette")) {
            Mod.LOGGER.debug("Found a palette, porting it to the component format");

            itemStackData.moveTagToComponent("basic", "xercapaint:palette_basic_colors");

            OptionalDynamic<?> rDynamic = itemStackData.removeTag("r");
            OptionalDynamic<?> gDynamic = itemStackData.removeTag("g");
            OptionalDynamic<?> bDynamic = itemStackData.removeTag("b");
            OptionalDynamic<?> mDynamic = itemStackData.removeTag("m");
            OptionalDynamic<?> nDynamic = itemStackData.removeTag("n");

            Optional<int[]> rArrayOpt = rDynamic.asIntStreamOpt().result().map(IntStream::toArray);
            Optional<int[]> gArrayOpt = gDynamic.asIntStreamOpt().result().map(IntStream::toArray);
            Optional<int[]> bArrayOpt = bDynamic.asIntStreamOpt().result().map(IntStream::toArray);
            Optional<int[]> mArrayOpt = mDynamic.asIntStreamOpt().result().map(IntStream::toArray);
            Optional<int[]> nArrayOpt = nDynamic.asIntStreamOpt().result().map(IntStream::toArray);

            if (rArrayOpt.isPresent() && gArrayOpt.isPresent() && bArrayOpt.isPresent() && mArrayOpt.isPresent() && nArrayOpt.isPresent()) {
                int[] rArray = rArrayOpt.get();
                int[] gArray = gArrayOpt.get();
                int[] bArray = bArrayOpt.get();
                int[] mArray = mArrayOpt.get();
                int[] nArray = nArrayOpt.get();

                PaletteUtil.CustomColor[] customColors = new PaletteUtil.CustomColor[ItemPalette.ComponentCustomColor.CUSTOM_COLOR_COUNT];

                for (int i = 0; i < ItemPalette.ComponentCustomColor.CUSTOM_COLOR_COUNT; i++) {
                    int totalRed = rArray[i];
                    int totalGreen = gArray[i];
                    int totalBlue = bArray[i];
                    int totalMaximum = mArray[i];
                    int numberOfColors = nArray[i];

                    customColors[i] = new PaletteUtil.CustomColor(totalRed, totalGreen, totalBlue, totalMaximum, numberOfColors);
                }

                IntStream intStream = Arrays.stream(customColors).flatMapToInt(cc -> Arrays.stream(new int[]{cc.totalRed, cc.totalGreen, cc.totalBlue, cc.totalMaximum, cc.numberOfColors}));
                itemStackData.setComponent("xercapaint:palette_custom_colors", tag.createIntList(intStream));
            }
        }
    }
}
