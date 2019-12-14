package xerca.xercamod.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemConditioned extends Item {
    private final ForgeConfigSpec.BooleanValue condition;

    public ItemConditioned(Properties properties, ForgeConfigSpec.BooleanValue condition) {
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
