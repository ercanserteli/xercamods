package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!condition.get()){
            return;
        }
        super.fillItemCategory(group, items);
    }
}
