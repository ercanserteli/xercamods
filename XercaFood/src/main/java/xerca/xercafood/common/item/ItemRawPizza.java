package xerca.xercafood.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block.BlockPizza;

import java.util.List;

public class ItemRawPizza extends Item {
    private final BlockPizza.Ingredient slot1;
    private final BlockPizza.Ingredient slot2;
    private final BlockPizza.Ingredient slot3;

    public ItemRawPizza(BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3, FoodProperties food) {
        super(new Properties().food(food));
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        ItemPizza.addPizzaIngredientToTooltip(tooltip, slot1);
        ItemPizza.addPizzaIngredientToTooltip(tooltip, slot2);
        ItemPizza.addPizzaIngredientToTooltip(tooltip, slot3);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (BlockPizza.isAllEmpty(slot1, slot2, slot3))
            return Component.translatable(Mod.MODID + ".pizza_raw_plain");
        return Component.translatable(Mod.MODID + ".pizza_raw");
    }
}
