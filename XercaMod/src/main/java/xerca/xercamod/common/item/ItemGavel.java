package xerca.xercamod.common.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.SoundEvents;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemGavel extends Item {

    public ItemGavel() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
        this.setRegistryName("item_gavel");
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public InteractionResult useOn(UseOnContext useContext) {
        Level worldIn = useContext.getLevel();
        BlockPos pos = useContext.getClickedPos();
        Player playerIn = useContext.getPlayer();

        if (worldIn.getBlockState(pos).canOcclude()) {
            worldIn.playSound(playerIn, pos, SoundEvents.GAVEL, SoundSource.PLAYERS, 1.0F, worldIn.random.nextFloat() * 0.2F + 0.8F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isCourtroomEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }
}
