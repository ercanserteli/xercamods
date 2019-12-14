package xerca.xercamod.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeConfigSpec;
import xerca.xercamod.common.Config;

import javax.annotation.ParametersAreNonnullByDefault;

public class BlockNamedConditionedItem extends BlockNamedItem {
    private final ForgeConfigSpec.BooleanValue condition;
    public BlockNamedConditionedItem(Block blockIn, Properties properties, ForgeConfigSpec.BooleanValue condition) {
        super(blockIn, properties);
        this.condition = condition;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!condition.get()){
            return;
        }
        super.fillItemGroup(group, items);
    }
}
