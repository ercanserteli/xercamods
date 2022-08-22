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
import org.lwjgl.system.NonnullDefault;
import xerca.xercapaint.client.ClientStuff;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.entity.EntityCanvas;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.world.item.Item.Properties;

@NonnullDefault
public class ItemCanvas extends Item {
    private final CanvasType canvasType;

    ItemCanvas(CanvasType canvasType) {
        super(new Properties().tab(Items.paintTab).stacksTo(1));
        this.canvasType = canvasType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        if(worldIn.isClientSide){
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
                Level world = context.getLevel();

                CompoundTag tag = itemstack.getTag();
                if (tag == null || !tag.contains("pixels") || !tag.contains("name")) {
                    if (context.getLevel().isClientSide) {
                        ClientStuff.showCanvasGui(player);
                    }
                    return InteractionResult.SUCCESS;
                }

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

                if (!world.isClientSide) {
                    EntityCanvas entityCanvas = new EntityCanvas(world, tag, pos, direction, canvasType, rotation);

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
//        Component label = Component.literal("");
        String labelString = "";
        int generation = 0;
        Component title = getCustomTitle(stack);
        if(title != null){
            labelString += (title.getString() + " ");
        }
        if (stack.hasTag() && stack.getTag() != null) {
            CompoundTag tag = stack.getTag();
            String s = tag.getString("author");

            if (!StringUtil.isNullOrEmpty(s)) {
                labelString += (Component.translatable("canvas.byAuthor", s)).getString() + " ";
            }

            generation = tag.getInt("generation");
        }
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
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if(tag != null){
                String s = tag.getString("title");
                if (!StringUtil.isNullOrEmpty(s)) {
                    return Component.literal(s);
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
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag() != null) {
            CompoundTag tag = stack.getTag();
            String s = tag.getString("author");

            if (!StringUtil.isNullOrEmpty(s)) {
                tooltip.add(Component.translatable("canvas.byAuthor", s));
            }

            int generation = tag.getInt("generation");
            // generation = 0 means empty, 1 means original, more means copy
            if(generation > 0){
                tooltip.add((Component.translatable("canvas.generation." + (generation - 1))).withStyle(ChatFormatting.GRAY));
            }
        }else{
            tooltip.add(Component.translatable("canvas.empty").withStyle(ChatFormatting.GRAY));
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
            return Level.isInSpawnableBounds(posIn) && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        }
        else{
            return !directionIn.getAxis().isVertical() && playerIn.mayUseItemAt(posIn, directionIn, itemStackIn);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(RenderProp.INSTANCE);
    }
}

