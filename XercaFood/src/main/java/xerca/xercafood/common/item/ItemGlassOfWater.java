package xerca.xercafood.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class ItemGlassOfWater extends ItemDrink{
    public ItemGlassOfWater(Properties properties, Item container) {
        super(properties, container);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        return ItemGlass.getCarbonatedWater(ctx) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
