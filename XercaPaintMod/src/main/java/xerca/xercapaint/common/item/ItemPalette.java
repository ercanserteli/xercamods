package xerca.xercapaint.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.client.ClientStuff;

import java.util.List;

public class ItemPalette extends Item {
    public static final String TAG_BASIC_COLORS = "basic";
    public static final String TAG_CUSTOM_RED = "r";
    public static final String TAG_CUSTOM_GREEN = "g";
    public static final String TAG_CUSTOM_BLUE = "b";
    public static final String TAG_CUSTOM_MAXIMUM = "m";
    public static final String TAG_CUSTOM_COUNT = "n";

    public static final int BASIC_COLOR_COUNT = 16;
    public static final int CUSTOM_COLOR_COUNT = 12;

    ItemPalette() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        if (worldIn.isClientSide) {
            ClientStuff.showCanvasGui(playerIn);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
    }

    public static boolean isFull(ItemStack stack) {
        return basicColorCount(stack) == BASIC_COLOR_COUNT;
    }

    public static int basicColorCount(ItemStack stack) {
        if (stack.getItem() != Items.ITEM_PALETTE.get()) {
            return 0;
        }
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_BASIC_COLORS)) {
            byte[] basicColors = tag.getByteArray(TAG_BASIC_COLORS);
            if (basicColors.length == BASIC_COLOR_COUNT) {
                int basicCount = 0;
                for (byte basicColor : basicColors) {
                    basicCount += basicColor;
                }
                return basicCount;
            }
        }
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = stack.getTag();
        byte[] basicColors = tag == null ? new byte[0] : tag.getByteArray(TAG_BASIC_COLORS);
        int[] customCounts = tag == null ? new int[0] : tag.getIntArray(TAG_CUSTOM_COUNT);

        if (basicColors.length != BASIC_COLOR_COUNT && customCounts.length != CUSTOM_COLOR_COUNT) {
            tooltip.add(Component.translatable("palette.empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        if (basicColors.length == BASIC_COLOR_COUNT) {
            int basicCount = 0;
            for (byte basicColor : basicColors) {
                basicCount += basicColor;
            }
            tooltip.add(Component.translatable("palette.basic_count", String.valueOf(basicCount)).withStyle(ChatFormatting.GRAY));
        }

        if (customCounts.length == CUSTOM_COLOR_COUNT) {
            int fullCount = 0;
            for (int n : customCounts) {
                if (n > 0) {
                    fullCount++;
                }
            }
            tooltip.add(Component.translatable("palette.custom_count", String.valueOf(fullCount)).withStyle(ChatFormatting.GRAY));
        }
    }
}
