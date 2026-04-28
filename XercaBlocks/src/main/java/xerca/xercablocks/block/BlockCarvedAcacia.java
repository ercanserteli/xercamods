package xerca.xercablocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class BlockCarvedAcacia extends BlockCarvedLog {
    public static final MapCodec<BlockCarvedAcacia> CODEC = simpleCodec(properties -> new BlockCarvedAcacia());

    public BlockCarvedAcacia() {
        super(Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .ignitedByLava()
                .sound(SoundType.WOOD)
                .strength(2.0F)
                .noOcclusion());
    }

    @Override
    protected MapCodec<? extends BlockCarvedLog> codec() {
        return CODEC;
    }
}
