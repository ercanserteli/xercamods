package xerca.xercafood.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xerca.xercafood.common.SoundEvents;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemGlass extends Item {
    public ItemGlass() {
        super((new Item.Properties()));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        return getCarbonatedWater(ctx) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    public static boolean getCarbonatedWater(UseOnContext ctx){
        BlockPos pos = ctx.getClickedPos();
        BlockState state = ctx.getLevel().getBlockState(pos);
        if(ctx.getPlayer() != null && state.getBlock() == Blocks.SOUL_SAND &&
                ctx.getClickedFace().equals(Direction.UP) &&
                ctx.getLevel().getBlockState(pos.above()).getBlock() == Blocks.BUBBLE_COLUMN
        ) {
            ctx.getItemInHand().shrink(1);
            ctx.getPlayer().addItem(new ItemStack(Items.CARBONATED_WATER));
            ctx.getLevel().playSound(ctx.getPlayer(), ctx.getClickedPos(), SoundEvents.FIZZY, SoundSource.PLAYERS,
                    1.0f, 0.9f + ctx.getLevel().random.nextFloat()*0.2f);
            return true;
        }
        return false;
    }
}
