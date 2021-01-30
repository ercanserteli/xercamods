package xerca.xercapaint.common.item;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HangingEntityItem;
import net.minecraft.item.IItemPropertyGetter;
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
import xerca.xercapaint.client.CanvasItemRenderer;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.entity.EntityCanvas;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

@NonnullDefault
public class ItemCanvas extends HangingEntityItem {
    private CanvasType canvasType;

    ItemCanvas(String name, CanvasType canvasType) {
        super(Entities.CANVAS, new Properties().group(Items.paintTab).maxStackSize(1).setISTER(() -> new Callable<ItemStackTileEntityRenderer>()
        {
            @Nullable
            CanvasItemRenderer r = null;

            @Override
            public ItemStackTileEntityRenderer call() {
                if(r == null)
                    r = new CanvasItemRenderer();
                return r;
            }
        }));

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
        } else {
            World world = context.getWorld();

            CompoundNBT tag = itemstack.getTag();
            if(tag == null || !tag.contains("pixels") || !tag.contains("name")){
                XercaPaint.proxy.showCanvasGui(playerentity);
                return ActionResultType.SUCCESS;
            }

            int rotation = 0;
            if(direction.getAxis() == Direction.Axis.Y){
                double xDiff = blockpos.getX() - playerentity.getPosX();
                double zDiff = blockpos.getZ() - playerentity.getPosZ();
                if(Math.abs(xDiff) > Math.abs(zDiff)){
                    if(xDiff > 0){
                        rotation = 1;
                    }else{
                        rotation = 3;
                    }
                }else{
                    if(zDiff > 0){
                        rotation = 2;
                    }else{
                        rotation = 0;
                    }
                }
                if(direction == Direction.DOWN && Math.abs(xDiff) < Math.abs(zDiff)){
                    rotation += 2;
                }
            }

            if (!world.isRemote) {
                EntityCanvas entityCanvas = new EntityCanvas(world, tag, pos, direction, canvasType, rotation);

                if (entityCanvas.onValidSurface()) {
                    entityCanvas.playPlaceSound();
                    world.addEntity(entityCanvas);
                    itemstack.shrink(1);
                }
            }
        }
        return ActionResultType.SUCCESS;
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
                tooltip.add((new TranslationTextComponent("canvas.generation." + (generation - 1))).mergeStyle(TextFormatting.GRAY));
            }
        }else{
            tooltip.add(new TranslationTextComponent("canvas.empty").mergeStyle(TextFormatting.GRAY));
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

    protected boolean canPlace(PlayerEntity playerIn, Direction directionIn, ItemStack itemStackIn, BlockPos posIn) {
        if(canvasType == CanvasType.SMALL){
            return !World.isOutsideBuildHeight(posIn) && playerIn.canPlayerEdit(posIn, directionIn, itemStackIn);
        }
        else{
            return !directionIn.getAxis().isVertical() && playerIn.canPlayerEdit(posIn, directionIn, itemStackIn);
        }
    }
}
