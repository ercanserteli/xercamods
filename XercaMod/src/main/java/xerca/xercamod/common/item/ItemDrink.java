package xerca.xercamod.common.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;

@MethodsReturnNonnullByDefault

public class ItemDrink extends ItemContainedFood {

    public ItemDrink(Properties properties, Item container) {
        super(properties, container);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
}
