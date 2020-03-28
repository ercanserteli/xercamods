package xerca.xercamod.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.BlockCushion;
import xerca.xercamod.common.entity.EntityCushion;

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
    public ActionResultType onItemUse(ItemUseContext context) {
        Direction enumfacing = context.getFace();
        if (enumfacing == Direction.DOWN) {
            return ActionResultType.FAIL;
        } else {
            World world = context.getWorld();
            BlockItemUseContext blockitemusecontext = new BlockItemUseContext(context);
            BlockPos blockpos = blockitemusecontext.getPos();
            if (blockitemusecontext.canPlace()) {
                double d0 = (double)blockpos.getX();
                double d1 = (double)blockpos.getY();
                double d2 = (double)blockpos.getZ();
                List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
                if (!list.isEmpty()) {
                    return ActionResultType.FAIL;
                } else {
                    ItemStack itemstack = context.getItem();
                    if (!world.isRemote) {
                        world.removeBlock(blockpos, false);
                        EntityCushion entityCushion = new EntityCushion(world, d0 + 0.5D, d1, d2 + 0.5D, this);
                        float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlacementYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                        entityCushion.setLocationAndAngles(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
                        world.addEntity(entityCushion);
                        world.playSound(null, entityCushion.getPosX(), entityCushion.getPosY(), entityCushion.getPosZ(), SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                    }

                    itemstack.shrink(1);
                    return ActionResultType.SUCCESS;
                }
            } else {
                return ActionResultType.FAIL;
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isCushionEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }
}
