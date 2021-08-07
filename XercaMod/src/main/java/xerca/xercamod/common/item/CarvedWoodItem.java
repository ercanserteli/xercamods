package xerca.xercamod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;

import javax.annotation.Nullable;
import java.util.List;

public class CarvedWoodItem extends BlockConditionedItem {
    private final int variation;
    public CarvedWoodItem(Block blockIn, Properties properties, int variation) {
        super(blockIn, properties,  Config::isCarvedWoodEnabled);
        this.variation = variation;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        TextComponent variationText = new TextComponent("Variation " + variation);
        tooltip.add(variationText.withStyle(ChatFormatting.GRAY));
    }
}
