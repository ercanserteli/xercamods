package xerca.xercafood.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.XercaFood;
import xerca.xercafood.common.block.BlockPizza;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRawPizza extends Item {
    private final BlockPizza.Ingredient slot1;
    private final BlockPizza.Ingredient slot2;
    private final BlockPizza.Ingredient slot3;

    public ItemRawPizza(BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3, FoodProperties food) {
        super(BlockPizza.isAllEmpty(slot1, slot2, slot3) ? new Properties().tab(CreativeModeTab.TAB_FOOD).food(food) : new Properties().food(food));
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        ItemPizza.addPizzaIngredientToTooltip(tooltip, slot1);
        ItemPizza.addPizzaIngredientToTooltip(tooltip, slot2);
        ItemPizza.addPizzaIngredientToTooltip(tooltip, slot3);
    }

    @Override
    public Component getName(ItemStack stack) {
        if(BlockPizza.isAllEmpty(slot1, slot2, slot3))
            return new TranslatableComponent(XercaFood.MODID + ".pizza_raw_plain");
        return new TranslatableComponent(XercaFood.MODID + ".pizza_raw");
    }
}
