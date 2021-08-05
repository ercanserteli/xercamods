package xerca.xercamusic.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import xerca.xercamusic.client.ClientStuff;
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
        super(new Properties().tab(Items.musicTab));
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
    public void playMusic(Level worldIn, Player playerIn, boolean canStop){
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesOfClass(EntityMusicSpirit.class, playerIn.getBoundingBox().inflate(3.0), entity -> entity.getBody().is(playerIn));
        if(musicSpirits.size() == 0){
            worldIn.addFreshEntity(new EntityMusicSpirit(worldIn, playerIn, (ItemInstrument) playerIn.getMainHandItem().getItem()));
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
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        final ItemStack heldItem = playerIn.getItemInHand(handIn);
        ItemStack off = playerIn.getOffhandItem();
        if (handIn == InteractionHand.MAIN_HAND && off.getItem() == Items.MUSIC_SHEET) {
            if (!worldIn.isClientSide) {
                playMusic(worldIn, playerIn, true);
            }
        } else {
            if (worldIn.isClientSide) {
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientStuff::showInstrumentGui);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState iblockstate = world.getBlockState(blockpos);
        if (iblockstate.getBlock() == Blocks.MUSIC_BOX && !iblockstate.getValue(BlockMusicBox.HAS_INSTRUMENT)) {
            ItemStack itemstack = context.getItemInHand();
            if (!world.isClientSide) {
                BlockMusicBox.insertInstrument(world, blockpos, iblockstate, itemstack.getItem());

                if(context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild){
                    itemstack.shrink(1);
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level world = attacker.level;
        if (!world.isClientSide) {
            for (int i = 0; i < 3; i++) {
                world.playSound(null, target.blockPosition(), sounds[world.random.nextInt(48)], SoundSource.PLAYERS, 3.0f, 1.0f);
            }
        }
        return true;
    }
}
