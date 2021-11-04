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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;

import java.util.List;

public abstract class BlockInstrument extends Block {
    public BlockInstrument(Properties properties) {
        super(properties);
    }

    public abstract ItemInstrument getItemInstrument();

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(new Vec3(pos.getX()+0.5, pos.getY()-0.5, pos.getZ()+0.5).distanceTo(player.position()) > 4){
            return InteractionResult.PASS;
        }
        ItemStack handStack = player.getItemInHand(hand);
        if(handStack.getItem() instanceof ItemMusicSheet){
            playMusic(worldIn, player, true, pos);
            return InteractionResult.SUCCESS;
        }
        else{
            ItemStack offhandStack = player.getItemInHand(InteractionHand.values()[(hand.ordinal() + 1)%2]);
            if(!(offhandStack.getItem() instanceof ItemMusicSheet)){
                if (worldIn.isClientSide) {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientStuff.showInstrumentGui(getItemInstrument(), pos));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private void playMusic(Level worldIn, Player playerIn, boolean canStop, BlockPos pos){
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesOfClass(EntityMusicSpirit.class, playerIn.getBoundingBox().inflate(3.0), entity -> entity.getBody().is(playerIn));
        if(musicSpirits.size() == 0){
            worldIn.addFreshEntity(new EntityMusicSpirit(worldIn, playerIn, pos, getItemInstrument()));
        }
        else if(canStop){
            musicSpirits.forEach(spirit -> spirit.setPlaying(false));
        }
    }
}
