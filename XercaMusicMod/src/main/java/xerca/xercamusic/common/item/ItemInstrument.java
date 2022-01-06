package xerca.xercamusic.common.item;

import net.minecraft.core.BlockPos;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.packets.TripleNoteClientPacket;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemInstrument extends Item {
    private ArrayList<Pair<Integer, SoundEvent>> sounds;
    private InsSound[] insSounds;
    public static final int totalNotes = 96;
    public static final int minNote = 21;
    public static final int maxNote = 117;
    public boolean isLong;
    public int minOctave;
    public int maxOctave;

    private final int instrumentId;

    public ItemInstrument(String name, boolean isLong, int instrumentId, int minOctave, int maxOctave) {
        super(new Properties().tab(Items.musicTab));
        this.setRegistryName(name);
        this.isLong = isLong;
        this.instrumentId = instrumentId;
        this.minOctave = minOctave;
        this.maxOctave = maxOctave;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    // Should be called from the server side
    static public void playMusic(Level worldIn, Player playerIn, boolean canStop){
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesOfClass(EntityMusicSpirit.class, playerIn.getBoundingBox().inflate(3.0), entity -> entity.getBody().is(playerIn));
        if(musicSpirits.size() == 0){
            worldIn.addFreshEntity(new EntityMusicSpirit(worldIn, playerIn, (ItemInstrument) playerIn.getMainHandItem().getItem()));
        }
        else if(canStop){
            musicSpirits.forEach(spirit -> spirit.setPlaying(false));
        }
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
            int note1 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);
            int note2 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);
            int note3 = minNote + minOctave*12 + world.random.nextInt((maxOctave+1)*12 - minOctave*12);

            PacketDistributor.PacketTarget networkTarget = PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getX(), target.getY(), target.getZ(), 24.0D, target.level.dimension()));
            TripleNoteClientPacket packet = new TripleNoteClientPacket(note1, note2, note3, this, target);
            XercaMusic.NETWORK_HANDLER.send(networkTarget, packet);
        }
        return true;
    }

    public static int idToNote(int id){
        return id + minNote;
    }

    public static int noteToId(int note){
        return note - minNote;
    }

    public void setSounds(ArrayList<Pair<Integer, SoundEvent>> sounds){
        this.sounds = sounds;
        insSounds = new InsSound[totalNotes];
        for(int i=0; i<totalNotes; i++){
            int note = idToNote(i);
            int index = getClosest(note);
            if(index < 0 || index >= sounds.size()){
                XercaMusic.LOGGER.error("Invalid sound index in Instrument construction");
            }
            int octave = i/12;
            if(octave >= minOctave && octave <= maxOctave){
                float pitch = (float)Math.pow(1.05946314465679, note - sounds.get(index).first);
                insSounds[i] = new InsSound(sounds.get(index).second, pitch);
            }
        }
    }

    private int getClosest(int note) {
        int minDiff = 100;
        int bestIndex = -1;
        for(int i=0; i<sounds.size(); i++){
            int diff = Math.abs(sounds.get(i).first - note);
            if(diff < minDiff){
                minDiff = diff;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public InsSound getSound(int note) {
        int id = noteToId(note);
        if(id >= 0 && id < totalNotes) {
            return insSounds[id];
        }
        XercaMusic.LOGGER.warn("Requested invalid note from Instrument getSound: " + note);
        return null;
    }

    static public class InsSound {
        public SoundEvent sound;
        public float pitch;

        public InsSound(SoundEvent sound, float pitch){
            this.sound = sound;
            this.pitch = pitch;
        }
    }

    public record Pair<F, S>(F first, S second) {
        public static <F, S> Pair<F, S> of(F first, S second) {
            if (first == null || second == null) {
                throw new IllegalArgumentException("Pair.of requires non null values.");
            }
            return new Pair<>(first, second);
        }

        @Override
        public int hashCode() {
            return first.hashCode() * 37 + second.hashCode();
        }
    }

}
