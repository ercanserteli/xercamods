package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class BlockNamedConditionedItem extends ItemNameBlockItem {
    private final Supplier<Boolean> condition;
    public BlockNamedConditionedItem(Block blockIn, Properties properties, Supplier<Boolean> condition) {
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
