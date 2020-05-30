package xerca.xercapaint.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HangingEntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.system.NonnullDefault;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.entity.EntityCanvas;

import javax.annotation.Nonnull;

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
            return ActionResultType.FAIL;
        } else {
            World world = context.getWorld();

            CompoundNBT tag = itemstack.getTag();
            if(tag == null || !tag.contains("pixels") || !tag.contains("name")){
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
