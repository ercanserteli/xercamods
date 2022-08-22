package xerca.xercamod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class CarvedWoodItem extends BlockConditionedItem {
    private final int variation;
    public CarvedWoodItem(Block blockIn, Properties properties, int variation) {
        super(blockIn, properties,  Config::isCarvedWoodEnabled);
        this.variation = variation;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        MutableComponent variationText = Component.literal("Variation " + variation);
        tooltip.add(variationText.withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType)
    {
        return 300;
    }
}
