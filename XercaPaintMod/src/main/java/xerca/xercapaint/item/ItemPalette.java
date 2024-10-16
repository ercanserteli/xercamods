package xerca.xercapaint.item;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.PaletteUtil;
import xerca.xercapaint.client.ModClient;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ItemPalette extends Item {
    ItemPalette() {
        super(new Properties().stacksTo(1));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, @NotNull Player playerIn, @Nonnull InteractionHand hand) {
        if(worldIn.isClientSide) {
            ModClient.showCanvasGui(playerIn);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
    }

    public static boolean isFull(ItemStack stack){
        return basicColorCount(stack) == 16;
    }

    public static int basicColorCount(ItemStack stack){
        if(stack.getItem() != Items.ITEM_PALETTE){
            return 0;
        }
        byte[] basicColors = stack.get(Items.PALETTE_BASIC_COLORS);
        if(basicColors != null){
            if (basicColors.length == 16) {
                int basicCount = 0;
                for(byte basicColor : basicColors){
                    basicCount += basicColor;
                }
                return basicCount;
            }
        }
        return 0;
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        byte[] basicColors = stack.get(Items.PALETTE_BASIC_COLORS);
        ComponentCustomColor customColorComp = stack.get(Items.PALETTE_CUSTOM_COLORS);
        if (basicColors == null && customColorComp == null) {
            tooltip.add(Component.translatable("palette.empty").withStyle(ChatFormatting.GRAY));
        }
        else  {
            if (basicColors != null && basicColors.length == 16) {
                int basicCount = 0;
                for(byte basicColor : basicColors){
                    basicCount += basicColor;
                }
                tooltip.add(Component.translatable("palette.basic_count", String.valueOf(basicCount)).withStyle(ChatFormatting.GRAY));
            }

            if (customColorComp != null) {
                int fullCount = 0;
                for(PaletteUtil.CustomColor color : customColorComp.colors){
                    if(color.numberOfColors > 0){
                        fullCount++;
                    }
                }
                tooltip.add(Component.translatable("palette.custom_count", String.valueOf(fullCount)).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static class ComponentCustomColor {
        public static final int CUSTOM_COLOR_COUNT = 12;
        public PaletteUtil.CustomColor[] colors;

        public ComponentCustomColor(PaletteUtil.CustomColor[] customColors) {
            if (customColors.length != CUSTOM_COLOR_COUNT) {
                throw new IllegalArgumentException("customColors must have exactly " + CUSTOM_COLOR_COUNT + " elements.");
            }

            colors = customColors;
        }

        public static final PrimitiveCodec<ComponentCustomColor> CODEC = new PrimitiveCodec<>() {
            @Override
            public <T> DataResult<ComponentCustomColor> read(final DynamicOps<T> ops, final T input) {
                return ops.getIntStream(input).flatMap(intStream -> {
                    int[] allColorsArray = intStream.toArray();

                    if (allColorsArray.length != CUSTOM_COLOR_COUNT * 5) {
                        return DataResult.error(() -> "Expected " + CUSTOM_COLOR_COUNT * 5 + " integer values, but got: " + allColorsArray.length);
                    }

                    PaletteUtil.CustomColor[] customColors = new PaletteUtil.CustomColor[CUSTOM_COLOR_COUNT];

                    for (int i = 0; i < CUSTOM_COLOR_COUNT; i++) {
                        int baseIndex = i * 5;
                        int totalRed = allColorsArray[baseIndex];
                        int totalGreen = allColorsArray[baseIndex + 1];
                        int totalBlue = allColorsArray[baseIndex + 2];
                        int totalMaximum = allColorsArray[baseIndex + 3];
                        int numberOfColors = allColorsArray[baseIndex + 4];

                        customColors[i] = new PaletteUtil.CustomColor(totalRed, totalGreen, totalBlue, totalMaximum, numberOfColors);
                    }

                    return DataResult.success(new ComponentCustomColor(customColors));
                });
            }


            @Override
            public <T> T write(final DynamicOps<T> ops, final ComponentCustomColor value) {
                IntStream intStream = Arrays.stream(value.colors)
                        .flatMapToInt(cc -> Arrays.stream(new int[]{cc.totalRed, cc.totalGreen, cc.totalBlue, cc.totalMaximum, cc.numberOfColors}));
                return ops.createIntList(intStream);
            }

            @Override
            public String toString() {
                return "CustomColorCodec";
            }
        };
    }

}
