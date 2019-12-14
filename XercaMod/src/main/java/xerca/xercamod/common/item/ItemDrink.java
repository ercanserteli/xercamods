package xerca.xercamod.common.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.NonNullList;
import xerca.xercamod.common.Config;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault

public class ItemDrink extends ItemContainedFood {

    public ItemDrink(Properties properties, Item container) {
        super(properties, container);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.FOOD_ENABLE.get()){
            return;
        }
        super.fillItemGroup(group, items);
    }
}
