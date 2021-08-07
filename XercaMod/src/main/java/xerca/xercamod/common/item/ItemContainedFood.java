package xerca.xercamod.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault

public class ItemContainedFood extends Item {
    private Item container;
    public ItemContainedFood(Properties properties, Item container) {
        super(properties.stacksTo(1));
        this.container = container;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        return new ItemStack(container);
    }
}
