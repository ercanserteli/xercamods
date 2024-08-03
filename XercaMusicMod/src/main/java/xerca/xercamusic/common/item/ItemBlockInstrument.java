package xerca.xercamusic.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.packets.TripleNoteClientPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class ItemBlockInstrument extends BlockItem implements IItemInstrument {
    private ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> sounds;
    private IItemInstrument.InsSound[] insSounds;
    public final boolean isLong;
    private final int minOctave;
    private final int maxOctave;

    private final int instrumentId;
    public ItemBlockInstrument(boolean isLong, int instrumentId, int minOctave, int maxOctave, Block block) {
        this(isLong, instrumentId, minOctave, maxOctave, new Properties(), block);
    }

    public ItemBlockInstrument(boolean isLong, int instrumentId, int minOctave, int maxOctave, Properties properties, Block block) {
        super(block, properties);
        this.isLong = isLong;
        this.instrumentId = instrumentId;
        this.minOctave = minOctave;
        this.maxOctave = maxOctave;
    }

    @Override
    public int getMinOctave() {
        return minOctave;
    }

    @Override
    public int getMaxOctave() {
        return maxOctave;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        final ItemStack heldItem = playerIn.getItemInHand(handIn);
        ItemStack off = playerIn.getOffhandItem();
        if (handIn == InteractionHand.MAIN_HAND && off.getItem() == Items.MUSIC_SHEET.get()) {
            if (!worldIn.isClientSide) {
                IItemInstrument.playMusic(worldIn, playerIn, true);
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
        BlockState blockState = world.getBlockState(blockpos);
        if (blockState.getBlock() == Blocks.MUSIC_BOX.get() && !blockState.getValue(BlockMusicBox.HAS_INSTRUMENT)) {
            ItemStack itemstack = context.getItemInHand();
            if (!world.isClientSide) {
                BlockMusicBox.insertInstrument(world, blockpos, blockState, itemstack.getItem());

                if(context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild){
                    itemstack.shrink(1);
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            return super.useOn(context);
        }
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, LivingEntity attacker) {
        Level world = attacker.level();
        if (!world.isClientSide) {
            int note1 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);
            int note2 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);
            int note3 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);

            PacketDistributor.PacketTarget networkTarget = PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 24.0D, target.level().dimension()));
            TripleNoteClientPacket packet = new TripleNoteClientPacket(note1, note2, note3, this, target);
            XercaMusic.NETWORK_HANDLER.send(networkTarget, packet);
        }
        return true;
    }
    public void setSounds(ArrayList<IItemInstrument.Pair<Integer, SoundEvent>> sounds){
        this.sounds = sounds;
        insSounds = new IItemInstrument.InsSound[totalNotes];
        for(int i=0; i<totalNotes; i++){
            int note = IItemInstrument.idToNote(i);
            int index = getClosest(note);
            if(index < 0 || index >= sounds.size()){
                XercaMusic.LOGGER.error("Invalid sound index in Instrument construction");
            }
            int octave = i/12;
            if(octave >= minOctave && octave <= maxOctave){
                float pitch = (float)Math.pow(1.05946314465679, note - sounds.get(index).first());
                insSounds[i] = new IItemInstrument.InsSound(sounds.get(index).second(), pitch);
            }
        }
    }

    private int getClosest(int note) {
        int minDiff = 100;
        int bestIndex = -1;
        for(int i=0; i<sounds.size(); i++){
            int diff = Math.abs(sounds.get(i).first() - note);
            if(diff < minDiff){
                minDiff = diff;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    @Nullable
    public IItemInstrument.InsSound getSound(int note) {
        int id = IItemInstrument.noteToId(note);
        if(id >= 0 && id < totalNotes) {
            return insSounds[id];
        }
        XercaMusic.LOGGER.warn("Requested invalid note from Instrument getSound: {}", note);
        return null;
    }
}
