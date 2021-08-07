package xerca.xercamod.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.block.BlockCushion;
import xerca.xercamod.common.entity.EntityCushion;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemCushion extends Item {
    private BlockCushion block;

    public ItemCushion(Properties properties, BlockCushion block) {
        super(properties);
        this.block = block;
    }

    public BlockCushion getBlock() {
        return block;
    }
    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction enumfacing = context.getClickedFace();
        if (enumfacing == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {
            Level world = context.getLevel();
            BlockPlaceContext blockitemusecontext = new BlockPlaceContext(context);
            BlockPos blockpos = blockitemusecontext.getClickedPos();
            if (blockitemusecontext.canPlace()) {
                double d0 = (double)blockpos.getX();
                double d1 = (double)blockpos.getY();
                double d2 = (double)blockpos.getZ();
                List<Entity> list = world.getEntities(null, new AABB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
                if (!list.isEmpty()) {
                    return InteractionResult.FAIL;
                } else {
                    ItemStack itemstack = context.getItemInHand();
                    if (!world.isClientSide) {
                        world.removeBlock(blockpos, false);
                        EntityCushion entityCushion = new EntityCushion(world, d0 + 0.5D, d1, d2 + 0.5D, this);
                        float f = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                        entityCushion.moveTo(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
                        world.addFreshEntity(entityCushion);
                        world.playSound(null, entityCushion.getX(), entityCushion.getY(), entityCushion.getZ(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                    }

                    itemstack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isCushionEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        TranslatableComponent text = new TranslatableComponent("xercamod.cushion_tooltip");
        tooltip.add(text.withStyle(ChatFormatting.BLUE));
    }
}
