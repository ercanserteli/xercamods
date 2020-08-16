package xerca.xercamusic.common.block;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xerca.xercamusic.common.XercaMusic;
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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(new Vec3d(pos.getX()+0.5, pos.getY()-0.5, pos.getZ()+0.5).distanceTo(player.getPositionVec()) > 4){
            return ActionResultType.PASS;
        }
        ItemStack handStack = player.getHeldItem(hand);
        if(handStack.getItem() instanceof ItemMusicSheet){
            playMusic(worldIn, player, true, pos);
            return ActionResultType.SUCCESS;
        }
        else{
            ItemStack offhandStack = player.getHeldItem(Hand.values()[(hand.ordinal() + 1)%2]);
            if(!(offhandStack.getItem() instanceof ItemMusicSheet)){
                XercaMusic.proxy.showInstrumentGui(getItemInstrument());
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    private void playMusic(World worldIn, PlayerEntity playerIn, boolean canStop, BlockPos pos){
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesWithinAABB(EntityMusicSpirit.class, playerIn.getBoundingBox().grow(3.0), entity -> entity.getBody().isEntityEqual(playerIn));
        if(musicSpirits.size() == 0){
            worldIn.addEntity(new EntityMusicSpirit(worldIn, playerIn, pos, getItemInstrument()));
        }
        else if(canStop){
            musicSpirits.forEach(spirit -> spirit.setPlaying(false));
        }
    }
}
