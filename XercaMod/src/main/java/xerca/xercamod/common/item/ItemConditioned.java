package xerca.xercamod.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class ItemConditioned extends Item {
    private final Supplier<Boolean> condition;

    public ItemConditioned(Properties properties, Supplier<Boolean> condition) {
        super(properties);
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
