package xerca.xercapaint.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import xerca.xercapaint.client.ClientStuff;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.entity.EntityCanvas;

import java.util.List;
import java.util.function.Consumer;

public class ItemCanvas extends Item {
    public static final String TAG_PIXELS = "pixels";
    public static final String TAG_CANVAS_ID = "name";
    public static final String TAG_VERSION = "v";
    public static final String TAG_TITLE = "title";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_GENERATION = "generation";
    public static final String TAG_SIDES_ACTIVE = "sidesActive";
    public static final String TAG_SIDE_PIXELS = "sidePixels";

    public static final int SIGNED_STACK_SIZE = 16;

    private static final int ORIGINAL_GENERATION = 1;
    private static final int COPY_GENERATION = 3;
    private final CanvasType canvasType;
    private final boolean glass;

    ItemCanvas(CanvasType canvasType) {
        this(canvasType, false);
    }

    ItemCanvas(CanvasType canvasType, boolean glass) {
        super(new Properties().stacksTo(1));
        this.canvasType = canvasType;
        this.glass = glass;
    }

    public boolean isGlass() {
        return glass;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        if (worldIn.isClientSide) {
            ClientStuff.showCanvasGui(playerIn);
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
                    ClientStuff.showCanvasGui(player);
                }
            } else {
                CompoundTag tag = itemstack.getTag();
                if (tag == null || !tag.contains(TAG_PIXELS) || !tag.contains(TAG_CANVAS_ID)) {
                    if (context.getLevel().isClientSide) {
                        ClientStuff.showCanvasGui(player);
                    }
                    return InteractionResult.SUCCESS;
                }

                int rotation = getRotation(direction, blockpos, player);

                if (!context.getLevel().isClientSide) {
                    EntityCanvas entityCanvas = new EntityCanvas(context.getLevel(), tag, pos, direction, canvasType, glass, rotation);

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
        CompoundTag tag = stack.getTag();
        return tag != null && !StringUtil.isNullOrEmpty(tag.getString(TAG_TITLE));
    }

    public static int getGeneration(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : tag.getInt(TAG_GENERATION);
    }

    public static Component getFullLabel(ItemStack stack) {
        String labelString = "";
        Component title = getCustomTitle(stack);
        if (title != null) {
            labelString += title.getString() + " ";
        }
        CompoundTag tag = stack.getTag();
        String author = tag == null ? null : tag.getString(TAG_AUTHOR);

        if (!StringUtil.isNullOrEmpty(author)) {
            labelString += Component.translatable("canvas.byAuthor", author).getString() + " ";
        }

        int generation = getGeneration(stack);
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
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            String s = tag.getString(TAG_TITLE);
            if (!StringUtil.isNullOrEmpty(s)) {
                return Component.literal(s);
            }
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
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_PIXELS)) {
            String author = tag.getString(TAG_AUTHOR);

            if (!StringUtil.isNullOrEmpty(author)) {
                tooltip.add(Component.translatable("canvas.byAuthor", author));
            }

            int generation = tag.getInt(TAG_GENERATION);
            // generation = 0=empty, 1=original, 2=copy of org, 3=copy of copy
            if (generation > 0) {
                tooltip.add(Component.translatable("canvas.generation." + (generation - 1))
                        .withStyle(generation == 1 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(Component.translatable("canvas.empty").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return getGeneration(stack) > 0;
    }

    /**
     * Signed canvases that are the same can be stacked.
     */
    public static boolean isSigned(ItemStack stack) {
        return getGeneration(stack) > 0;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return isSigned(stack) ? SIGNED_STACK_SIZE : super.getMaxStackSize(stack);
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
        if (canvasType == CanvasType.SMALL) {
            return Level.isInSpawnableBounds(posIn) && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        } else {
            return !directionIn.getAxis().isVertical() && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(RenderProp.INSTANCE);
    }

    public static String generateName(Player player) {
        return player.getUUID() + "_" + System.currentTimeMillis() / 100;
    }

    public static Item canvasItemFor(CanvasType type, boolean glass) {
        return switch (type) {
            case SMALL -> glass ? Items.ITEM_CANVAS_GLASS.get() : Items.ITEM_CANVAS.get();
            case LONG -> glass ? Items.ITEM_CANVAS_GLASS_LONG.get() : Items.ITEM_CANVAS_LONG.get();
            case TALL -> glass ? Items.ITEM_CANVAS_GLASS_TALL.get() : Items.ITEM_CANVAS_TALL.get();
            case LARGE -> glass ? Items.ITEM_CANVAS_GLASS_LARGE.get() : Items.ITEM_CANVAS_LARGE.get();
        };
    }
}
