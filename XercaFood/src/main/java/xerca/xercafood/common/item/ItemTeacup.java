package xerca.xercafood.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@NonnullDefault
public class ItemTeacup extends ItemStackableContainedFood {
    private final int sugarAmount;

    public ItemTeacup(int sugarAmount, Item teaCup) {
        super(sugarAmount == 0 ? (new Item.Properties()).food(Foods.TEACUP0) :
              sugarAmount == 1 ? (new Item.Properties()).food(Foods.TEACUP1) :
              sugarAmount == 2 ? (new Item.Properties()).food(Foods.TEACUP2) :
              sugarAmount == 3 ? (new Item.Properties()).food(Foods.TEACUP3) :
              sugarAmount == 4 ? (new Item.Properties()).food(Foods.TEACUP4) :
              sugarAmount == 5 ? (new Item.Properties()).food(Foods.TEACUP5) :
                                 (new Item.Properties()).food(Foods.TEACUP6), teaCup, 64);
        this.sugarAmount = sugarAmount;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack par1ItemStack) {
        return UseAnim.DRINK;
    }

    public int getSugarAmount() {
        return sugarAmount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (this.sugarAmount == 0) {
            tooltip.add(Component.literal("No sugar"));
        } else if(this.sugarAmount == 1) {
            tooltip.add(Component.literal(this.sugarAmount + " sugar"));
        } else {
            tooltip.add(Component.literal(this.sugarAmount + " sugars"));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 64;
    }
}
