package xerca.xercafood.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class ItemEmptyTeapot extends ItemTea {
    public ItemEmptyTeapot(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        MutableComponent text = Component.translatable("xercafood.empty_teapot_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
    }
}
