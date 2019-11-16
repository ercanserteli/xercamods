package xerca.xercamod.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IItemProvider;
import xerca.xercamod.common.item.Items;

class BlockTomatoPlant extends CropsBlock implements IGrowable {

    BlockTomatoPlant() {
        super(Block.Properties.create(Material.PLANTS).sound(SoundType.PLANT).hardnessAndResistance(0.0f).tickRandomly().doesNotBlockMovement());
        this.setRegistryName("block_tomato_plant");
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return Items.ITEM_TOMATO_SEEDS;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

}
