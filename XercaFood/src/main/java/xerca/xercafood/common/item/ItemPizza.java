package xerca.xercafood.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.block.BlockPizza;

import java.util.function.Consumer;

public class ItemPizza extends BlockItem {
    private final BlockPizza.Ingredient slot1;
    private final BlockPizza.Ingredient slot2;
    private final BlockPizza.Ingredient slot3;

    public ItemPizza(Block blockIn, BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3) {
        super(blockIn, new Item.Properties().setId(Mod.itemKey(pizzaName("pizza", slot1, slot2, slot3))));
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
    }

    static String pizzaName(String base, BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3) {
        StringBuilder sb = new StringBuilder(base);
        for (BlockPizza.Ingredient ingredient : new BlockPizza.Ingredient[]{slot1, slot2, slot3}) {
            if (ingredient != BlockPizza.Ingredient.EMPTY) {
                sb.append('_').append(ingredient.name().toLowerCase());
            }
        }
        return sb.toString();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        addPizzaIngredientToTooltip(tooltip, slot1);
        addPizzaIngredientToTooltip(tooltip, slot2);
        addPizzaIngredientToTooltip(tooltip, slot3);
    }

    static void addPizzaIngredientToTooltip(Consumer<Component> tooltip, BlockPizza.Ingredient ingredient) {
        if (ingredient != BlockPizza.Ingredient.EMPTY) {
            tooltip.accept(Component.translatable(Mod.MOD_ID + ".ingredient." + ingredient.name().toLowerCase()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        if (slot1 == BlockPizza.Ingredient.EMPTY && slot2 == BlockPizza.Ingredient.EMPTY && slot3 == BlockPizza.Ingredient.EMPTY)
            return Component.translatable(Mod.MOD_ID + ".pizza_plain");
        return Component.translatable(Mod.MOD_ID + ".pizza");
    }
}
