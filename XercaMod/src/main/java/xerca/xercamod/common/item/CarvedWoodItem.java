package xerca.xercamod.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import xerca.xercamod.common.Config;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

public class CarvedWoodItem extends BlockConditionedItem {
    private final int variation;
    public CarvedWoodItem(Block blockIn, Properties properties, int variation) {
        super(blockIn, properties,  Config::isCarvedWoodEnabled);
        this.variation = variation;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        StringTextComponent variationText = new StringTextComponent("Variation " + variation);
        tooltip.add(variationText.mergeStyle(TextFormatting.GRAY));
    }
}
