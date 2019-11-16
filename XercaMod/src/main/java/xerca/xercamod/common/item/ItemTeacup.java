package xerca.xercamod.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nullable;
import java.util.List;

@NonnullDefault
public class ItemTeacup extends Item {
    private final int sugarAmount;

    public ItemTeacup(int sugarAmount) {
        super(sugarAmount == 0 ? (new Item.Properties().group(Items.teaTab)).food(Foods.TEACUP0) :
              sugarAmount == 1 ? (new Item.Properties()).food(Foods.TEACUP1) :
              sugarAmount == 2 ? (new Item.Properties()).food(Foods.TEACUP2) :
              sugarAmount == 3 ? (new Item.Properties()).food(Foods.TEACUP3) :
              sugarAmount == 4 ? (new Item.Properties()).food(Foods.TEACUP4) :
              sugarAmount == 5 ? (new Item.Properties()).food(Foods.TEACUP5) :
                                 (new Item.Properties()).food(Foods.TEACUP6));
        this.sugarAmount = sugarAmount;
        this.setRegistryName("item_full_teacup_" + sugarAmount);
    }

    @Override
    public UseAction getUseAction(ItemStack par1ItemStack) {
        return UseAction.DRINK;
    }

    public int getSugarAmount() {
        return sugarAmount;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (this.sugarAmount == 0) {
            tooltip.add(new StringTextComponent("No sugar"));
        } else if(this.sugarAmount == 1) {
            tooltip.add(new StringTextComponent(this.sugarAmount + " sugar"));
        } else {
            tooltip.add(new StringTextComponent(this.sugarAmount + " sugars"));
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        ItemStack container = new ItemStack(Items.ITEM_TEACUP);
        if(stack.getCount() == 0){
            return container;
        }
        else{
            if(entityLiving instanceof PlayerEntity){
                PlayerInventory inv = ((PlayerEntity)(entityLiving)).inventory;
                if(inv.storeItemStack(container) == -1 && inv.getFirstEmptyStack() == -1){
                    if(!worldIn.isRemote) {
                        worldIn.addEntity(new ItemEntity(worldIn, entityLiving.posX, entityLiving.posY, entityLiving.posZ, container));
                    }
                }else{
                    inv.addItemStackToInventory(container);
                }
            }
            return stack;
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 64;
    }
}
