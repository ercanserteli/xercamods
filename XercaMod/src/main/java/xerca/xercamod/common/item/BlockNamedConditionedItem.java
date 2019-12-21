package xerca.xercamod.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class BlockNamedConditionedItem extends BlockNamedItem {
    private final Supplier<Boolean> condition;
    public BlockNamedConditionedItem(Block blockIn, Properties properties, Supplier<Boolean> condition) {
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
