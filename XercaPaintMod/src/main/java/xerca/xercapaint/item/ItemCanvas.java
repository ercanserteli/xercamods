package xerca.xercapaint.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.Item;
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
        super(Entities.CANVAS, new Item.Properties().stacksTo(1));
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
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (player != null) {
            if (!this.mayPlace(player, direction, itemstack, pos)) {
                if (context.getLevel().isClientSide) {
                    ModClient.showCanvasGui(player);
                }
            } else {
                Level world = context.getLevel();

                String canvasId = itemstack.get(Items.CANVAS_ID);
                List<Integer> canvasPixles = itemstack.get(Items.CANVAS_PIXELS);
                if (canvasId == null || canvasPixles == null) {
                    if (context.getLevel().isClientSide) {
                        ModClient.showCanvasGui(player);
                    }
                    return InteractionResult.SUCCESS;
                }

                int rotation = getRotation(direction, blockpos, player);

                if (!world.isClientSide) {
                    EntityCanvas entityCanvas = new EntityCanvas(world, itemstack, pos, direction, canvasType, rotation);

                    if (entityCanvas.survives()) {
                        entityCanvas.playPlacementSound();
                        world.addFreshEntity(entityCanvas);
                        itemstack.shrink(1);
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static int getRotation(Direction direction, BlockPos blockpos, Player player) {
        int rotation = 0;
        if (direction.getAxis() == Direction.Axis.Y) {
            double xDiff = blockpos.getX() - player.getX();
            double zDiff = blockpos.getZ() - player.getZ();
            if (Math.abs(xDiff) > Math.abs(zDiff)) {
                if (xDiff > 0) {
                    rotation = 1;
                } else {
                    rotation = 3;
                }
            } else {
                if (zDiff > 0) {
                    rotation = 2;
                }
            }
            if (direction == Direction.DOWN && Math.abs(xDiff) < Math.abs(zDiff)) {
                rotation += 2;
            }
        }
        return rotation;
    }

    public static boolean hasTitle(@Nonnull ItemStack stack){
        return !StringUtil.isNullOrEmpty(stack.get(Items.CANVAS_TITLE));
    }

    public static Component getFullLabel(@Nonnull ItemStack stack){
        String labelString = "";
        Component title = getCustomTitle(stack);
        if(title != null){
            labelString += (title.getString() + " ");
        }
        String author = stack.get(Items.CANVAS_AUTHOR);

        if (!StringUtil.isNullOrEmpty(author)) {
            labelString += (Component.translatable("canvas.byAuthor", author)).getString() + " ";
        }

        int generation = stack.getOrDefault(Items.CANVAS_GENERATION, 0);
        MutableComponent label = Component.literal(labelString);
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
        String s = stack.get(Items.CANVAS_TITLE);
        if (!StringUtil.isNullOrEmpty(s)) {
            return Component.literal(s);
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        List<Integer> pixels = stack.get(Items.CANVAS_PIXELS);
        if (pixels != null) {
            String author = stack.get(Items.CANVAS_AUTHOR);

            if (!StringUtil.isNullOrEmpty(author)) {
                tooltipComponents.add(Component.translatable("canvas.byAuthor", author));
            }

            int generation = stack.getOrDefault(Items.CANVAS_GENERATION, 0);
            // generation = 0 means empty, 1 means original, more means copy
            if(generation > 0){
                tooltipComponents.add((Component.translatable("canvas.generation." + (generation - 1))).withStyle(ChatFormatting.GRAY));
            }
        }else{
            tooltipComponents.add(Component.translatable("canvas.empty").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0;
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
