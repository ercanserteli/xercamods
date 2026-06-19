package xerca.xercamusic.common.block;


import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xerca.xercamusic.client.ModClient;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;

import java.util.List;

import static xerca.xercamusic.common.Mod.onlyRunOnClient;

public abstract class BlockInstrument extends Block {
    protected BlockInstrument(Properties properties) {
        super(properties);
    }

    public abstract IItemInstrument getItemInstrument();

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (new Vec3(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5).distanceTo(player.position()) > 4) {
            return InteractionResult.FAIL;
        }
        ItemStack handStack = player.getItemInHand(hand);
        if (handStack.getItem() instanceof ItemMusicSheet) {
            playMusic(level, player, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (new Vec3(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5).distanceTo(player.position()) > 4) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            onlyRunOnClient(() -> () -> ModClient.showInstrumentGui(getItemInstrument(), pos));
        }
        return InteractionResult.SUCCESS;
    }

    private void playMusic(Level worldIn, Player playerIn, BlockPos pos) {
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesOfClass(EntityMusicSpirit.class, playerIn.getBoundingBox().inflate(3.0), entity -> playerIn.equals(entity.getBody()));
        if (musicSpirits.isEmpty()) {
            worldIn.addFreshEntity(new EntityMusicSpirit(worldIn, playerIn, pos, getItemInstrument()));
        } else {
            musicSpirits.forEach(spirit -> spirit.setPlaying(false));
        }
    }
}
