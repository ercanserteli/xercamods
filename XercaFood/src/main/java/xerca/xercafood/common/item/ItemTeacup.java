package xerca.xercafood.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.lwjgl.system.NonnullDefault;

import java.util.function.Consumer;

@NonnullDefault
public class ItemTeacup extends ItemStackableContainedFood {
    private final int sugarAmount;

    public ItemTeacup(int sugarAmount, Item teaCup) {
        super(createProperties(sugarAmount), teaCup, 64);
        this.sugarAmount = sugarAmount;
    }

    private static Item.Properties createProperties(int sugarAmount) {
        Item.Properties properties = new Item.Properties()
                .setId(xerca.xercafood.common.Mod.itemKey("full_teacup_" + Math.min(sugarAmount, 6)));
        net.minecraft.world.item.component.Consumable consumable = Foods.teacupConsumable(sugarAmount);
        return switch (sugarAmount) {
            case 0 -> properties.food(Foods.TEACUP0, consumable);
            case 1 -> properties.food(Foods.TEACUP1, consumable);
            case 2 -> properties.food(Foods.TEACUP2, consumable);
            case 3 -> properties.food(Foods.TEACUP3, consumable);
            case 4 -> properties.food(Foods.TEACUP4, consumable);
            case 5 -> properties.food(Foods.TEACUP5, consumable);
            default -> properties.food(Foods.TEACUP6, consumable);
        };
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack par1ItemStack) {
        return ItemUseAnimation.DRINK;
    }

    public int getSugarAmount() {
        return sugarAmount;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (this.sugarAmount == 0) {
            tooltip.accept(Component.literal("No sugar"));
        } else if (this.sugarAmount == 1) {
            tooltip.accept(Component.literal(this.sugarAmount + " sugar"));
        } else {
            tooltip.accept(Component.literal(this.sugarAmount + " sugars"));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 64;
    }
}
