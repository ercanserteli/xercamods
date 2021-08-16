package xerca.xercapaint.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.client.ClientStuff;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

public class ItemPalette extends Item {
    ItemPalette(String name) {
        super(new Properties().tab(Items.paintTab).stacksTo(1));
        this.setRegistryName(name);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        if(worldIn.isClientSide) {
            ClientStuff.showCanvasGui(playerIn);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            // Empty palette
            items.add(new ItemStack(this));

            // Full palette
            ItemStack fullPalette = new ItemStack(this);
            byte[] basicColors = new byte[16];
            Arrays.fill(basicColors, (byte)1);
            fullPalette.getOrCreateTag().putByteArray("basic", basicColors);
            items.add(fullPalette);
        }
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
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null){
                byte[] basicColors = tag.getByteArray("basic");
                if (basicColors.length == 16) {
                    int basicCount = 0;
                    for(byte basicColor : basicColors){
                        basicCount += basicColor;
                    }
                    tooltip.add(new TranslatableComponent("palette.basic_count", String.valueOf(basicCount)).withStyle(ChatFormatting.GRAY));
                }

                int[] ns = tag.getIntArray("n");
                if (ns.length == 12){
                    int fullCount = 0;

                    for(int n : ns){
                        if(n > 0){
                            fullCount++;
                        }
                    }
                    tooltip.add(new TranslatableComponent("palette.custom_count", String.valueOf(fullCount)).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        else{
            tooltip.add(new TranslatableComponent("palette.empty").withStyle(ChatFormatting.GRAY));
        }
    }
}
