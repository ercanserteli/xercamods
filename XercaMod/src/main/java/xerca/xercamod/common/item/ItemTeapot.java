package xerca.xercamod.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTeapot extends Item {
    public static final int maxTea = 7;
    private final int teaAmount;
    private final boolean isHot;

    public ItemTeapot(int teaAmount, boolean isHot) {
        super((teaAmount == 7 ? new Item.Properties().group(Items.teaTab) : new Item.Properties()).defaultMaxDamage(maxTea));
        this.teaAmount = teaAmount;
        this.isHot = isHot;
        String baseName = isHot ? "item_hot_teapot_" : "item_full_teapot_";
        this.setRegistryName(baseName + teaAmount);
    }

    public int getTeaAmount() {
        return teaAmount;
    }

    public boolean isHot() {
        return isHot;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("Tea amount: " + teaAmount));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
}
