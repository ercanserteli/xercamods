package xerca.xercamusic.common.item;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.packets.clientbound.TripleNoteClientPacket;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

import static xerca.xercamusic.common.XercaMusic.onlyRunOnClient;
import static xerca.xercamusic.common.XercaMusic.sendToClient;

public class ItemInstrument extends Item implements IItemInstrument{
    private ArrayList<Pair<Integer, SoundEvent>> sounds;
    private InsSound[] insSounds;
    public final int minOctave;
    public final int maxOctave;

    private final int instrumentId;

    public ItemInstrument(int instrumentId, int minOctave, int maxOctave) {
        this(instrumentId, minOctave, maxOctave, new Properties());
    }

    public ItemInstrument( int instrumentId, int minOctave, int maxOctave, Properties properties) {
        super(properties);
        this.instrumentId = instrumentId;
        this.minOctave = minOctave;
        this.maxOctave = maxOctave;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        final ItemStack heldItem = playerIn.getItemInHand(handIn);
        ItemStack off = playerIn.getOffhandItem();
        if (handIn == InteractionHand.MAIN_HAND && off.getItem() == Items.MUSIC_SHEET) {
            if (!worldIn.isClientSide) {
                IItemInstrument.playMusic(worldIn, playerIn, true);
            }
        } else {
            if (worldIn.isClientSide) {
                onlyRunOnClient(() -> ClientStuff::showInstrumentGui);
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
        if (blockState.getBlock() == Blocks.MUSIC_BOX && !blockState.getValue(BlockMusicBox.HAS_INSTRUMENT)) {
            ItemStack itemstack = context.getItemInHand();
            if (!world.isClientSide) {
                BlockMusicBox.insertInstrument(world, blockpos, blockState, itemstack.getItem());

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
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, LivingEntity attacker) {
        Level world = attacker.level();
        if (!world.isClientSide) {
            int note1 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);
            int note2 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);
            int note3 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);

            Collection<ServerPlayer> players = PlayerLookup.around((ServerLevel) target.level(), target.position(), 24.D);
            TripleNoteClientPacket packet = new TripleNoteClientPacket(note1, note2, note3, this, target);
            for (ServerPlayer player : players) {
                sendToClient(player, packet);
            }
        }
        return true;
    }

    @Override
    public void setSounds(ArrayList<Pair<Integer, SoundEvent>> sounds){
        this.sounds = sounds;
        insSounds = new InsSound[totalNotes];
        for(int i=0; i<totalNotes; i++){
            int note = IItemInstrument.idToNote(i);
            int index = getClosest(note);
            if(index < 0 || index >= sounds.size()){
                XercaMusic.LOGGER.error("Invalid sound index in Instrument construction");
            }
            int octave = i/12;
            if(octave >= minOctave && octave <= maxOctave){
                float pitch = (float)Math.pow(1.05946314465679, note - sounds.get(index).first());
                insSounds[i] = new InsSound(sounds.get(index).second(), pitch);
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

    @Override
    public InsSound getSound(int note) {
        int id = IItemInstrument.noteToId(note);
        if(id >= 0 && id < totalNotes) {
            return insSounds[id];
        }
        XercaMusic.LOGGER.warn("Requested invalid note from Instrument getSound: {}", note);
        return null;
    }

    @Override
    public int getMinOctave() {
        return minOctave;
    }

    @Override
    public int getMaxOctave() {
        return maxOctave;
    }
}
