package xerca.xercablocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class BlockCarvedNetherLog extends BlockCarvedLog {
    public static final MapCodec<BlockCarvedNetherLog> CODEC = simpleCodec(properties -> new BlockCarvedNetherLog());

    public BlockCarvedNetherLog() {
        super(Properties.of()
                .sound(SoundType.STEM)
                .mapColor(MapColor.NETHER)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(2.0F));
    }

    @Override
    protected MapCodec<? extends BlockCarvedLog> codec() {
        return CODEC;
    }
}
