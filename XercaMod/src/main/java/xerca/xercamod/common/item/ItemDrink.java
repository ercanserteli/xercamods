package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.item.Item.Properties;

public class ItemDrink extends ItemStackableContainedFood {

    public ItemDrink(Properties properties, Item container) {
        super(properties, container, 16);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isFoodEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }
}
