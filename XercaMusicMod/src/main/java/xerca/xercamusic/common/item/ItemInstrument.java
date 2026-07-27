package xerca.xercamusic.common.item;

import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
import org.jetbrains.annotations.Nullable;
import xerca.xercamusic.client.ModClient;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.packets.clientbound.TripleNoteClientPacket;

import java.util.List;

import static xerca.xercamusic.common.Mod.onlyRunOnClient;

public class ItemInstrument extends Item implements IItemInstrument {
    public final int minOctave;
    public final int maxOctave;
    private final int instrumentId;
    private InsSound @Nullable [] insSounds;

    public ItemInstrument(int instrumentId, int minOctave, int maxOctave) {
        this(instrumentId, minOctave, maxOctave, new Properties());
    }

    public ItemInstrument(int instrumentId, int minOctave, int maxOctave, Properties properties) {
        super(properties);
        this.instrumentId = instrumentId;
        this.minOctave = minOctave;
        this.maxOctave = maxOctave;
    }

    @Override
    public int getInstrumentId() {
        return instrumentId;
    }

    public static InteractionResultHolder<ItemStack> useInstrument(Level worldIn, Player playerIn, InteractionHand handIn) {
        final ItemStack heldItem = playerIn.getItemInHand(handIn);
        ItemStack off = playerIn.getOffhandItem();
        if (handIn == InteractionHand.MAIN_HAND && off.getItem() == Items.MUSIC_SHEET) {
            if (!worldIn.isClientSide) {
                IItemInstrument.playMusic(worldIn, playerIn, true);
            }
        } else {
            if (worldIn.isClientSide) {
                onlyRunOnClient(() -> ModClient::showInstrumentGui);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    public static boolean useInstrumentOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockpos);
        if (blockState.getBlock() == Blocks.MUSIC_BOX &&
                blockState.hasProperty(BlockMusicBox.HAS_INSTRUMENT) &&
                !blockState.getValue(BlockMusicBox.HAS_INSTRUMENT)) {
            ItemStack itemstack = context.getItemInHand();
            if (!world.isClientSide) {
                BlockMusicBox.insertInstrument(world, blockpos, blockState, itemstack.getItem());
                Player player = context.getPlayer();
                if (player != null && !player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            }
            return true;
        }
        return false;
    }

    public static void hurtEnemyWithInstrument(LivingEntity target, LivingEntity attacker, int minOctave, int maxOctave, IItemInstrument instrument) {
        Level world = attacker.level();
        if (!world.isClientSide) {
            int note1 = MIN_NOTE + minOctave * 12 + world.random.nextInt((maxOctave + 1) * 12 - minOctave * 12);
            int note2 = MIN_NOTE + minOctave * 12 + world.random.nextInt((maxOctave + 1) * 12 - minOctave * 12);
            int note3 = MIN_NOTE + minOctave * 12 + world.random.nextInt((maxOctave + 1) * 12 - minOctave * 12);

            TripleNoteClientPacket packet = new TripleNoteClientPacket(note1, note2, note3, instrument, target);
            PacketDistributor.sendToPlayersNear((ServerLevel) target.level(), null, target.getX(), target.getY(), target.getZ(), 24.0D, packet);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        return useInstrument(worldIn, playerIn, handIn);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (useInstrumentOn(context)) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        hurtEnemyWithInstrument(target, attacker, minOctave, maxOctave, this);
        return true;
    }

    @Override
    public void setSounds(List<Pair<Integer, SoundEvent>> sounds) {
        insSounds = new InsSound[TOTAL_NOTES];
        for (int i = 0; i < TOTAL_NOTES; i++) {
            int note = IItemInstrument.idToNote(i);
            int index = getClosest(note, sounds);
            if (index < 0 || index >= sounds.size()) {
                Mod.LOGGER.error("Invalid sound index in Instrument construction");
            }
            int octave = i / 12;
            if (octave >= minOctave && octave <= maxOctave) {
                float pitch = (float) Math.pow(1.05946314465679, note - (double) sounds.get(index).first());
                insSounds[i] = new InsSound(sounds.get(index).second(), pitch);
            }
        }
    }

    public static int getClosest(int note, List<Pair<Integer, SoundEvent>> sounds) {
        int minDiff = 100;
        int bestIndex = -1;
        for (int i = 0; i < sounds.size(); i++) {
            int diff = Math.abs(sounds.get(i).first() - note);
            if (diff < minDiff) {
                minDiff = diff;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    @Nullable
    @Override
    public InsSound getSound(int note) {
        int id = IItemInstrument.noteToId(note);
        if (insSounds != null && id >= 0 && id < TOTAL_NOTES) {
            return insSounds[id];
        }
        Mod.LOGGER.warn("Requested invalid note from Instrument getSound: {}", note);
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
