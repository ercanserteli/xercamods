package xerca.xercapaint.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.lwjgl.system.NonnullDefault;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.client.ModClient;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityCanvas;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@NonnullDefault
public class ItemCanvas extends HangingEntityItem {
    private final CanvasType canvasType;

    ItemCanvas(CanvasType canvasType) {
        super(Entities.CANVAS, new FabricItemSettings().group(Items.paintTab).stacksTo(1));
        this.canvasType = canvasType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        if(worldIn.isClientSide){
            ModClient.showCanvasGui(playerIn);
        }
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
            if(context.getLevel().isClientSide){
                ModClient.showCanvasGui(playerentity);
            }
        } else {
            Level world = context.getLevel();

            CompoundTag tag = itemstack.getTag();
            if(tag == null || !tag.contains("pixels") || !tag.contains("name")){
                if(context.getLevel().isClientSide) {
                    ModClient.showCanvasGui(playerentity);
                }
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

    public static boolean hasTitle(@Nonnull ItemStack stack){
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null){
                String s = tag.getString("title");
                return !StringUtil.isNullOrEmpty(s);
            }
        }
        return false;
    }

    public static Component getFullLabel(@Nonnull ItemStack stack){
//        TextComponent label = new TextComponent("");
        String labelString = "";
        int generation = 0;
        Component title = getCustomTitle(stack);
        if(title != null){
            labelString += (title.getString() + " ");
        }
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            String s = tag.getString("author");

            if (!StringUtil.isNullOrEmpty(s)) {
                labelString += (new TranslatableComponent("canvas.byAuthor", s)).getString() + " ";
            }

            generation = tag.getInt("generation");
//            if(generation > 0){
//                labelString += (new TranslatableComponent("canvas.generation." + (generation - 1))).getString();
//            }
        }
        TextComponent label = new TextComponent(labelString);
        if(generation == 1){
            label.withStyle(ChatFormatting.YELLOW);
        }
        else if(generation >= 3){
            label.withStyle(ChatFormatting.GRAY);
        }
        return label;
    }

    @Nullable
    public static Component getCustomTitle(@Nonnull ItemStack stack){
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null){
                String s = tag.getString("title");
                if (!StringUtil.isNullOrEmpty(s)) {
                    return new TextComponent(s);
                }
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        Component comp = getCustomTitle(stack);
        if(comp != null){
            return comp;
        }
        return super.getName(stack);
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
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
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
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
            return Level.isInSpawnableBounds(posIn) && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        }
        else{
            return !directionIn.getAxis().isVertical() && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        }
    }
}
