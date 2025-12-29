package xerca.xercamusic.common.item;

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
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockInstrument extends BlockItem implements IItemInstrument {
    private final int minOctave;
    private final int maxOctave;
    private final int instrumentId;
    private IItemInstrument.InsSound[] insSounds;

    public ItemBlockInstrument(int instrumentId, int minOctave, int maxOctave, Block block) {
        this(instrumentId, minOctave, maxOctave, new Properties(), block);
    }

    public ItemBlockInstrument(int instrumentId, int minOctave, int maxOctave, Properties properties, Block block) {
        super(block, properties);
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

    @Override
    public int getInstrumentId() {
        return instrumentId;
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        return ItemInstrument.useInstrument(worldIn, playerIn, handIn);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@NotNull UseOnContext context) {
        if (ItemInstrument.useInstrumentOn(context)) {
            return InteractionResult.SUCCESS;
        } else {
            return super.useOn(context);
        }
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        ItemInstrument.hurtEnemyWithInstrument(target, attacker, minOctave, maxOctave, this);
        return true;
    }

    @SuppressWarnings("java:S1854")  // gives false positives
    @Override
    public void setSounds(List<Pair<Integer, SoundEvent>> sounds) {
        insSounds = new IItemInstrument.InsSound[TOTAL_NOTES];

        for (int i = 0; i < TOTAL_NOTES; i++) {
            int octave = i / 12;
            if (octave < minOctave || octave > maxOctave) {
                continue;
            }

            int note = IItemInstrument.idToNote(i);
            int index = ItemInstrument.getClosest(note, sounds);

            if (index < 0 || index >= sounds.size()) {
                Mod.LOGGER.error("Invalid sound index in Instrument construction");
            } else {
                IItemInstrument.Pair<Integer, SoundEvent> base = sounds.get(index);
                float pitch = (float) Math.pow(1.05946314465679, note - base.first());
                insSounds[i] = new IItemInstrument.InsSound(base.second(), pitch);
            }
        }
    }

    @Nullable
    @Override
    public IItemInstrument.InsSound getSound(int note) {
        int id = IItemInstrument.noteToId(note);
        if (id >= 0 && id < TOTAL_NOTES) {
            return insSounds[id];
        }
        Mod.LOGGER.warn("Requested invalid note from Instrument getSound: {}", note);
        return null;
    }
}
