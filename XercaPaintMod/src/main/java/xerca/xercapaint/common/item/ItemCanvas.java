package xerca.xercapaint.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HangingEntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.NonnullDefault;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.entity.EntityCanvas;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@NonnullDefault
public class ItemCanvas extends HangingEntityItem {
    private CanvasType canvasType;

    ItemCanvas(String name, CanvasType canvasType) {
        super(Entities.CANVAS, new Properties().group(Items.paintTab).maxStackSize(1));
        this.setRegistryName(name);
        this.canvasType = canvasType;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        XercaPaint.proxy.showCanvasGui(playerIn);
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos blockpos = context.getPos();
        Direction direction = context.getFace();
        BlockPos pos = blockpos.offset(direction);
        PlayerEntity playerentity = context.getPlayer();
        ItemStack itemstack = context.getItem();
        if (playerentity != null && !this.canPlace(playerentity, direction, itemstack, pos)) {
            XercaPaint.proxy.showCanvasGui(playerentity);
            return ActionResultType.SUCCESS;
        } else {
            World world = context.getWorld();

            CompoundNBT tag = itemstack.getTag();
            if(tag == null || !tag.contains("pixels") || !tag.contains("name")){
                XercaPaint.proxy.showCanvasGui(playerentity);
                return ActionResultType.SUCCESS;
            }

            EntityCanvas entityCanvas = new EntityCanvas(world, tag, pos, direction, canvasType);

            if (entityCanvas.onValidSurface()) {
                if (!world.isRemote) {
                    entityCanvas.playPlaceSound();
                    world.addEntity(entityCanvas);
                }

                itemstack.shrink(1);
            }

            return ActionResultType.SUCCESS;
        }
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag != null){
                String s = tag.getString("title");
                if (!StringUtils.isNullOrEmpty(s)) {
                    return new StringTextComponent(s);
                }
            }
        }
        return super.getDisplayName(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            String s = tag.getString("author");

            if (!StringUtils.isNullOrEmpty(s)) {
                tooltip.add(new TranslationTextComponent("canvas.byAuthor", s));
            }

            int generation = tag.getInt("generation");
            // generation = 0 means empty, 1 means original, more means copy
            if(generation > 0){
                tooltip.add((new TranslationTextComponent("canvas.generation." + (generation - 1))).applyTextStyle(TextFormatting.GRAY));
            }
        }else{
            tooltip.add(new TranslationTextComponent("canvas.empty").applyTextStyle(TextFormatting.GRAY));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        if(stack.hasTag()){
            CompoundNBT tag = stack.getTag();
            if(tag != null) {
                int generation = tag.getInt("generation");
                return generation > 0;
            }
        }
        return false;
    }

    public int getWidth() {
        return CanvasType.getWidth(canvasType);
    }

    public int getHeight() {
        return CanvasType.getHeight(canvasType);
    }

    public CanvasType getCanvasType() {
        return canvasType;
    }

}
