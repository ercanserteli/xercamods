package xerca.xercamod.common.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.system.NonnullDefault;

@MethodsReturnNonnullByDefault
@NonnullDefault

public class ItemContainedFood extends Item {
    private Item container;
    public ItemContainedFood(Properties properties, Item container) {
        super(properties.maxStackSize(1));
        this.container = container;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        return new ItemStack(container);
    }
}
