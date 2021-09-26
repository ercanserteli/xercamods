package xerca.xercamod.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.BlockPizza;

import javax.annotation.Nullable;
import java.util.List;

import static xerca.xercamod.common.block.BlockPizza.isAllEmpty;
import static xerca.xercamod.common.block.BlockPizza.postfix;
import static xerca.xercamod.common.item.ItemPizza.addPizzaIngredientToTooltip;

public class ItemRawPizza extends ItemConditioned {
    private final BlockPizza.Ingredient slot1;
    private final BlockPizza.Ingredient slot2;
    private final BlockPizza.Ingredient slot3;

    public ItemRawPizza(BlockPizza.Ingredient slot1, BlockPizza.Ingredient slot2, BlockPizza.Ingredient slot3, FoodProperties food) {
        super(isAllEmpty(slot1, slot2, slot3) ? new Properties().tab(CreativeModeTab.TAB_FOOD).food(food) : new Properties().food(food),  Config::isFoodEnabled);
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
        this.setRegistryName("raw_pizza" + postfix(slot1, slot2, slot3));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addPizzaIngredientToTooltip(tooltip, slot1);
        addPizzaIngredientToTooltip(tooltip, slot2);
        addPizzaIngredientToTooltip(tooltip, slot3);
    }

    @Override
    public Component getName(ItemStack stack) {
        if(isAllEmpty(slot1, slot2, slot3))
            return new TranslatableComponent(XercaMod.MODID + ".pizza_raw_plain");
        return new TranslatableComponent(XercaMod.MODID + ".pizza_raw");
    }
}
