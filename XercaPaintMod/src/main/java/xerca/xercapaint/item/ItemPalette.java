package xerca.xercapaint.item;

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
import org.jetbrains.annotations.NotNull;
import xerca.xercapaint.client.ModClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
        CompoundTag tag = stack.getTag();
        if(tag != null && tag.contains("basic")){
            byte[] basicColors = tag.getByteArray("basic");
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null){
                byte[] basicColors = tag.getByteArray("basic");
                if (basicColors.length == 16) {
                    int basicCount = 0;
                    for(byte basicColor : basicColors){
                        basicCount += basicColor;
                    }
                    tooltip.add(Component.translatable("palette.basic_count", String.valueOf(basicCount)).withStyle(ChatFormatting.GRAY));
                }

                int[] ns = tag.getIntArray("n");
                if (ns.length == 12){
                    int fullCount = 0;

                    for(int n : ns){
                        if(n > 0){
                            fullCount++;
                        }
                    }
                    tooltip.add(Component.translatable("palette.custom_count", String.valueOf(fullCount)).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        else{
            tooltip.add(Component.translatable("palette.empty").withStyle(ChatFormatting.GRAY));
        }
    }
}
