package xerca.xercapaint.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.XercaPaint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

public class ItemPalette extends Item {
    ItemPalette(String name) {
        super(new Properties().group(Items.paintTab).maxStackSize(1));
        this.setRegistryName(name);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        XercaPaint.proxy.showCanvasGui(playerIn);
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag != null){
                byte[] basicColors = tag.getByteArray("basic");
                if (basicColors.length == 16) {
                    int basicCount = 0;
                    for(byte basicColor : basicColors){
                        basicCount += basicColor;
                    }
                    tooltip.add(new TranslationTextComponent("palette.basic_count", String.valueOf(basicCount)).applyTextStyle(TextFormatting.GRAY));
                }

                int[] ns = tag.getIntArray("n");
                if (ns.length == 12){
                    int fullCount = 0;

                    for(int n : ns){
                        if(n > 0){
                            fullCount++;
                        }
                    }
                    tooltip.add(new TranslationTextComponent("palette.custom_count", String.valueOf(fullCount)).applyTextStyle(TextFormatting.GRAY));
                }
            }
        }
    }
}
