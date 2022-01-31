package xerca.xercamusic.common.block;


import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;

public abstract class BlockInstrument extends Block {
    public BlockInstrument(Properties properties) {
        super(properties);
    }

    public abstract ItemInstrument getItemInstrument();

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(new Vector3d(pos.getX()+0.5, pos.getY()-0.5, pos.getZ()+0.5).distanceTo(player.getPositionVec()) > 4){
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
                if (worldIn.isRemote) {
                    ClientStuff.showInstrumentGui(getItemInstrument(), pos);
                }
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
