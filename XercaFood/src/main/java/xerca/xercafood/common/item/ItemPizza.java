package xerca.xercafood.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import xerca.xercafood.common.XercaFood;
import xerca.xercafood.common.block.BlockPizza;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPizza extends BlockItem {
    private final BlockPizza.Ingredient slot1;
    private final BlockPizza.Ingredient slot2;
    private final BlockPizza.Ingredient slot3;

    public ItemPizza(Block blockIn, BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3) {
        super(blockIn, BlockPizza.isAllEmpty(slot1, slot2, slot3) ? new Item.Properties().tab(CreativeModeTab.TAB_FOOD) : new Item.Properties());
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addPizzaIngredientToTooltip(tooltip, slot1);
        addPizzaIngredientToTooltip(tooltip, slot2);
        addPizzaIngredientToTooltip(tooltip, slot3);
    }

    static void addPizzaIngredientToTooltip(List<Component> tooltip, BlockPizza.Ingredient ingredient){
        if(!ingredient.equals(BlockPizza.Ingredient.EMPTY)) {
            tooltip.add(Component.translatable(XercaFood.MODID + ".ingredient." + ingredient.name().toLowerCase()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        if(slot1.equals(BlockPizza.Ingredient.EMPTY) && slot2.equals(BlockPizza.Ingredient.EMPTY) && slot3.equals(BlockPizza.Ingredient.EMPTY))
            return Component.translatable(XercaFood.MODID + ".pizza_plain");
        return Component.translatable(XercaFood.MODID + ".pizza");
    }
}
