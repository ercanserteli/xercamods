package xerca.xercamod.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class BlockConditionedItem extends BlockItem {
    private final Supplier<Boolean> condition;
    public BlockConditionedItem(Block blockIn, Properties properties, Supplier<Boolean> condition) {
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
