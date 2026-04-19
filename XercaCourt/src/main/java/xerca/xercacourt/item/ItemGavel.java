package xerca.xercacourt.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercacourt.SoundEvents;

public class ItemGavel extends Item {
    public ItemGavel() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.getBlockState(pos).canOcclude()) {
            level.playSound(player, pos, SoundEvents.GAVEL, SoundSource.PLAYERS, 1.0F, level.random.nextFloat() * 0.2F + 0.8F);
        }
        return InteractionResult.SUCCESS;
    }
}
