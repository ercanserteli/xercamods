package xerca.xercacushion.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import xerca.xercacushion.entity.EntityCushion;

import java.util.List;
import java.util.function.Consumer;

public class ItemCushion extends Item {
    private final int variant;

    public ItemCushion(Properties properties, int variant) {
        super(properties);
        this.variant = variant;
    }

    public int getVariant() {
        return variant;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction face = context.getClickedFace();
        if (face == Direction.DOWN) {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos pos = clickedState.canBeReplaced(placeContext) ? clickedPos : clickedPos.relative(face);
        if (!level.isEmptyBlock(pos) && !level.getBlockState(pos).canBeReplaced(placeContext)) {
            return InteractionResult.FAIL;
        }

        AABB box = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0D, pos.getY() + 2.0D, pos.getZ() + 1.0D);
        List<Entity> entities = level.getEntities(null, box);
        if (!entities.isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            EntityCushion cushion = new EntityCushion(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, variant);
            float yaw = Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
            cushion.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, yaw, 0.0F);
            level.addFreshEntity(cushion);
            level.playSound(null, cushion.getX(), cushion.getY(), cushion.getZ(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("xercacushion.cushion_tooltip").withStyle(ChatFormatting.BLUE));
    }
}
