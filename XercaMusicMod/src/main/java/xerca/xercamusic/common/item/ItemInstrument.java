package xerca.xercamusic.common.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.entity.EntityMusicSpirit;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemInstrument extends Item {
    private SoundEvent[] sounds;
    public boolean shouldCutOff;

    private final int instrumentId;

    ItemInstrument(String name, boolean shouldCutOff, int instrumentId) {
        super(new Properties().group(Items.musicTab));
        this.setRegistryName(name);
        this.shouldCutOff = shouldCutOff;
        this.instrumentId = instrumentId;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public SoundEvent getSound(int i) {
        return sounds[i];
    }

    // Should be called from the server side
    public void playMusic(World worldIn, PlayerEntity playerIn, boolean canStop){
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesWithinAABB(EntityMusicSpirit.class, playerIn.getBoundingBox().grow(3.0), entity -> entity.getBody().isEntityEqual(playerIn));
        if(musicSpirits.size() == 0){
            worldIn.addEntity(new EntityMusicSpirit(worldIn, playerIn, (ItemInstrument) playerIn.getHeldItemMainhand().getItem()));
        }
        else if(canStop){
            musicSpirits.forEach(spirit -> spirit.setPlaying(false));
        }
    }

    public void setSounds(SoundEvent[] sounds){
        this.sounds = sounds;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        final ItemStack heldItem = playerIn.getHeldItem(handIn);
        ItemStack off = playerIn.getHeldItemOffhand();
        if (handIn == Hand.MAIN_HAND && off.getItem() == Items.MUSIC_SHEET) {
            if (!worldIn.isRemote) {
                playMusic(worldIn, playerIn, true);
            }
        } else {
            XercaMusic.proxy.showInstrumentGui();
        }
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState iblockstate = world.getBlockState(blockpos);
        if (iblockstate.getBlock() == Blocks.MUSIC_BOX && !iblockstate.get(BlockMusicBox.HAS_INSTRUMENT)) {
            ItemStack itemstack = context.getItem();
            if (!world.isRemote) {
                BlockMusicBox.insertInstrument(world, blockpos, iblockstate, itemstack.getItem());

                if(context.getPlayer() != null && !context.getPlayer().abilities.isCreativeMode){
                    itemstack.shrink(1);
                }
            }

            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.world;
        if (!world.isRemote) {
            for (int i = 0; i < 3; i++) {
                world.playSound(null, target.getPosition(), sounds[world.rand.nextInt(48)], SoundCategory.PLAYERS, 3.0f, 1.0f);
            }
        }
        return true;
    }
}
