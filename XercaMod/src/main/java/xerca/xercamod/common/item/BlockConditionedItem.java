package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

public class BlockConditionedItem extends BlockItem {
    private final Supplier<Boolean> condition;
    public BlockConditionedItem(Block blockIn, Properties properties, Supplier<Boolean> condition) {
        super(blockIn, properties);
        this.condition = condition;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!condition.get()){
            return;
        }
        super.fillItemCategory(group, items);
    }
}
