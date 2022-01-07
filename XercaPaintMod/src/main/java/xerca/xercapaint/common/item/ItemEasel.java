package xerca.xercapaint.common.item;

import net.minecraft.entity.SpawnReason;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.entity.EntityEasel;

public class ItemEasel extends Item {

    public ItemEasel(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        Direction direction = ctx.getClickedFace();
        if (direction == Direction.DOWN) {
            return ActionResultType.FAIL;
        } else {
            World level = ctx.getLevel();
            BlockItemUseContext blockplacecontext = new BlockItemUseContext(ctx);
            BlockPos blockpos = blockplacecontext.getClickedPos();
            ItemStack itemstack = ctx.getItemInHand();
            Vector3d vec3 = Vector3d.atBottomCenterOf(blockpos);
            AxisAlignedBB aabb = Entities.EASEL.getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
            if (level.noCollision(null, aabb, (p_40505_) -> true) && level.getEntities(null, aabb).isEmpty()) {
                if (level instanceof ServerWorld) {
                    ServerWorld serverlevel = (ServerWorld) level;
                    EntityEasel easel = Entities.EASEL.create(serverlevel, itemstack.getTag(), null, ctx.getPlayer(), blockpos, SpawnReason.SPAWN_EGG, true, true);
                    if (easel == null) {
                        return ActionResultType.FAIL;
                    }

                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(ctx.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    easel.moveTo(easel.getX(), easel.getY(), easel.getZ(), f, 0.0F);
                    serverlevel.addFreshEntityWithPassengers(easel);
                    level.playSound(null, easel.getX(), easel.getY(), easel.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                }

                itemstack.shrink(1);
                return ActionResultType.sidedSuccess(level.isClientSide);
            } else {
                return ActionResultType.FAIL;
            }
        }
    }
}
