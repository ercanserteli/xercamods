package xerca.xercapaint.common.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
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

import net.minecraft.world.item.Item.Properties;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;

@NonnullDefault
public class ItemCanvas extends HangingEntityItem {
    private CanvasType canvasType;

    ItemCanvas(String name, CanvasType canvasType) {
        super(Entities.CANVAS, new Properties().tab(Items.paintTab).stacksTo(1).setISTER(() -> new Callable<BlockEntityWithoutLevelRenderer>()
        {
            @Nullable
            CanvasItemRenderer r = null;

            @Override
            public BlockEntityWithoutLevelRenderer call() {
                if(r == null)
                    r = new CanvasItemRenderer();
                return r;
            }
        }));

        this.setRegistryName(name);
        this.canvasType = canvasType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        XercaPaint.proxy.showCanvasGui(playerIn);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos pos = blockpos.relative(direction);
        Player playerentity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (playerentity != null && !this.mayPlace(playerentity, direction, itemstack, pos)) {
            XercaPaint.proxy.showCanvasGui(playerentity);
        } else {
            Level world = context.getLevel();

            CompoundTag tag = itemstack.getTag();
            if(tag == null || !tag.contains("pixels") || !tag.contains("name")){
                XercaPaint.proxy.showCanvasGui(playerentity);
                return InteractionResult.SUCCESS;
            }

            int rotation = 0;
            if(direction.getAxis() == Direction.Axis.Y){
                double xDiff = blockpos.getX() - playerentity.getX();
                double zDiff = blockpos.getZ() - playerentity.getZ();
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

            if (!world.isClientSide) {
                EntityCanvas entityCanvas = new EntityCanvas(world, tag, pos, direction, canvasType, rotation);

                if (entityCanvas.survives()) {
                    entityCanvas.playPlacementSound();
                    world.addFreshEntity(entityCanvas);
                    itemstack.shrink(1);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null){
                String s = tag.getString("title");
                if (!StringUtil.isNullOrEmpty(s)) {
                    return new TextComponent(s);
                }
            }
        }
        return super.getName(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            String s = tag.getString("author");

            if (!StringUtil.isNullOrEmpty(s)) {
                tooltip.add(new TranslatableComponent("canvas.byAuthor", s));
            }

            int generation = tag.getInt("generation");
            // generation = 0 means empty, 1 means original, more means copy
            if(generation > 0){
                tooltip.add((new TranslatableComponent("canvas.generation." + (generation - 1))).withStyle(ChatFormatting.GRAY));
            }
        }else{
            tooltip.add(new TranslatableComponent("canvas.empty").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack stack) {
        if(stack.hasTag()){
            CompoundTag tag = stack.getTag();
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

    protected boolean mayPlace(Player playerIn, Direction directionIn, ItemStack itemStackIn, BlockPos posIn) {
        if(canvasType == CanvasType.SMALL){
            return !Level.isOutsideBuildHeight(posIn) && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        }
        else{
            return !directionIn.getAxis().isVertical() && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        }
    }
}
