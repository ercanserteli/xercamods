package xerca.xercamod.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xerca.xercamod.common.SoundEvents;

public class ItemGavel extends Item {

    public ItemGavel() {
        super(new Item.Properties().group(ItemGroup.TOOLS));
        this.setRegistryName("item_gavel");
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext useContext) {
        World worldIn = useContext.getWorld();
        BlockPos pos = useContext.getPos();
        PlayerEntity playerIn = useContext.getPlayer();

        if (worldIn.getBlockState(pos).isSolid()) {
            worldIn.playSound(playerIn, pos, SoundEvents.GAVEL, SoundCategory.PLAYERS, 1.0F, worldIn.rand.nextFloat() * 0.2F + 0.8F);
        }
        return ActionResultType.SUCCESS;
    }

}
