package xerca.xercapaint.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xerca.xercapaint.CanvasType;
import xerca.xercapaint.client.ModClient;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityCanvas;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class ItemCanvas extends HangingEntityItem {
    private static final int ORIGINAL_GENERATION = 1;
    private static final int COPY_GENERATION = 3;
    private final CanvasType canvasType;
    private final boolean glass;

    ItemCanvas(CanvasType canvasType, boolean glass, Item.Properties properties) {
        super(Entities.CANVAS, properties.stacksTo(1));
        this.canvasType = canvasType;
        this.glass = glass;
    }

    public boolean isGlass() {
        return glass;
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        if (worldIn.isClientSide()) {
            ModClient.showCanvasGui(playerIn);
        }
        return InteractionResult.SUCCESS;
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
                if (context.getLevel().isClientSide()) {
                    ModClient.showCanvasGui(player);
                }
            } else {
                String canvasId = itemstack.get(Items.CANVAS_ID);
                List<Integer> canvasPixels = itemstack.get(Items.CANVAS_PIXELS);
                if (canvasId == null || canvasPixels == null) {
                    if (context.getLevel().isClientSide()) {
                        ModClient.showCanvasGui(player);
                    }
                    return InteractionResult.SUCCESS;
                }

                int rotation = getRotation(direction, blockpos, player);

                if (!context.getLevel().isClientSide()) {
                    EntityCanvas entityCanvas = new EntityCanvas(context.getLevel(), itemstack, pos, direction, canvasType, rotation);

                    if (entityCanvas.survives()) {
                        entityCanvas.playPlacementSound();
                        context.getLevel().addFreshEntity(entityCanvas);
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

    public static boolean hasTitle(ItemStack stack) {
        return !StringUtil.isNullOrEmpty(stack.get(Items.CANVAS_TITLE));
    }

    public static Component getFullLabel(ItemStack stack) {
        String labelString = "";
        Component title = getCustomTitle(stack);
        if (title != null) {
            labelString += title.getString() + " ";
        }
        String author = stack.get(Items.CANVAS_AUTHOR);

        if (!StringUtil.isNullOrEmpty(author)) {
            labelString += Component.translatable("canvas.byAuthor", author).getString() + " ";
        }

        int generation = stack.getOrDefault(Items.CANVAS_GENERATION, 0);
        MutableComponent label = Component.literal(labelString);
        if (generation == ORIGINAL_GENERATION) {
            label.withStyle(ChatFormatting.YELLOW);
        } else if (generation >= COPY_GENERATION) {
            label.withStyle(ChatFormatting.GRAY);
        }
        return label;
    }

    @Nullable
    public static Component getCustomTitle(ItemStack stack) {
        String s = stack.get(Items.CANVAS_TITLE);
        if (!StringUtil.isNullOrEmpty(s)) {
            return Component.literal(s);
        }
        return null;
    }

    @Override
    public Component getName(ItemStack stack) {
        Component comp = getCustomTitle(stack);
        if (comp != null) {
            return comp;
        }
        return super.getName(stack);
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        List<Integer> pixels = stack.get(Items.CANVAS_PIXELS);
        if (pixels != null) {
            String author = stack.get(Items.CANVAS_AUTHOR);

            if (tooltipDisplay.shows(Items.CANVAS_AUTHOR) && !StringUtil.isNullOrEmpty(author)) {
                tooltipComponents.accept(Component.translatable("canvas.byAuthor", author));
            }

            int generation = stack.getOrDefault(Items.CANVAS_GENERATION, 0);
            // generation = 0=empty, 1=original, 2=copy of org, 3=copy of copy
            if (tooltipDisplay.shows(Items.CANVAS_GENERATION) && generation > 0) {
                tooltipComponents.accept(Component.translatable("canvas.generation." + (generation - 1))
                        .withStyle(generation == 1 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
            }
        } else if (tooltipDisplay.shows(Items.CANVAS_AUTHOR)) {
            tooltipComponents.accept(Component.translatable("canvas.empty").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0;
    }

    public static final int SIGNED_STACK_SIZE = 16;

    /**
     * Signed canvases that are the same can be stacked
     */
    public static void updateStackSize(ItemStack stack) {
        if (stack.getOrDefault(Items.CANVAS_GENERATION, 0) > 0) {
            stack.set(DataComponents.MAX_STACK_SIZE, SIGNED_STACK_SIZE);
        } else if (stack.getOrDefault(DataComponents.MAX_STACK_SIZE, 1) > 1) {
            stack.remove(DataComponents.MAX_STACK_SIZE);
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

    @Override
    protected boolean mayPlace(Player playerIn, Direction directionIn, ItemStack itemStackIn, BlockPos posIn) {
        if (canvasType == CanvasType.SMALL) {
            return Level.isInSpawnableBounds(posIn) && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        } else {
            return !directionIn.getAxis().isVertical() && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        }
    }

    public static String generateName(Player player) {
        return player.getUUID() + "_" + System.currentTimeMillis() / 100;
    }

    public static Item canvasItemFor(CanvasType type, boolean glass) {
        return switch (type) {
            case SMALL -> glass ? Items.ITEM_CANVAS_GLASS : Items.ITEM_CANVAS;
            case LONG -> glass ? Items.ITEM_CANVAS_GLASS_LONG : Items.ITEM_CANVAS_LONG;
            case TALL -> glass ? Items.ITEM_CANVAS_GLASS_TALL : Items.ITEM_CANVAS_TALL;
            case LARGE -> glass ? Items.ITEM_CANVAS_GLASS_LARGE : Items.ITEM_CANVAS_LARGE;
        };
    }
}
