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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamusic.client.ClientStuff;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.block.BlockMusicBox;
import xerca.xercamusic.common.block.Blocks;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.packets.TripleNoteClientPacket;

import javax.annotation.Nonnull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class ItemInstrument extends Item {
    private ArrayList<AbstractMap.SimpleImmutableEntry<Integer, SoundEvent>> sounds;
    private InsSound[] insSounds;
    public static final int totalNotes = 96;
    public static final int minNote = 21;
    public static final int maxNote = 117;
    public boolean isLong;
    public int minOctave;
    public int maxOctave;

    private final int instrumentId;

    public ItemInstrument(String name, boolean isLong, int instrumentId, int minOctave, int maxOctave) {
        super(new Properties().group(Items.musicTab));
        
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
    static public void playMusic(World worldIn, PlayerEntity playerIn, boolean canStop){
        List<EntityMusicSpirit> musicSpirits = worldIn.getEntitiesWithinAABB(EntityMusicSpirit.class, playerIn.getBoundingBox().grow(3.0), entity -> entity.getBody().isEntityEqual(playerIn));
        if(musicSpirits.size() == 0){
            worldIn.addEntity(new EntityMusicSpirit(worldIn, playerIn, (ItemInstrument) playerIn.getHeldItemMainhand().getItem()));
        }
        else if(canStop){
            musicSpirits.forEach(spirit -> spirit.setPlaying(false));
        }
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
            if (worldIn.isRemote) {
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientStuff::showInstrumentGui);
            }
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
            int note1 = minNote + minOctave*12 + world.rand.nextInt((maxOctave+1)*12 - minOctave*12);
            int note2 = minNote + minOctave*12 + world.rand.nextInt((maxOctave+1)*12 - minOctave*12);
            int note3 = minNote + minOctave*12 + world.rand.nextInt((maxOctave+1)*12 - minOctave*12);

            PacketDistributor.PacketTarget networkTarget = PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getPosX(), target.getPosY(), target.getPosZ(), 24.0D, target.world.getDimensionKey()));
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

    public void setSounds(ArrayList<AbstractMap.SimpleImmutableEntry<Integer, SoundEvent>> sounds){
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
                float pitch = (float)Math.pow(1.05946314465679, note - sounds.get(index).getKey());
                insSounds[i] = new InsSound(sounds.get(index).getValue(), pitch);
            }
        }
    }

    private int getClosest(int note) {
        int minDiff = 100;
        int bestIndex = -1;
        for(int i=0; i<sounds.size(); i++){
            int diff = Math.abs(sounds.get(i).getKey() - note);
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
}
