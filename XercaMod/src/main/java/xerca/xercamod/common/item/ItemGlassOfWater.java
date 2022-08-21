package xerca.xercamod.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.NotNull;

public class ItemGlassOfWater extends ItemDrink{
    public ItemGlassOfWater(Properties properties, Item container) {
        super(properties, container);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        return ItemGlass.getCarbonatedWater(ctx) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
