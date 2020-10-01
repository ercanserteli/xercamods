package xerca.xercamod.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.packets.ConfettiParticlePacket;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;


public class ItemConfetti extends Item {

    ItemConfetti() {
        super(new Item.Properties().group(ItemGroup.MISC));
        this.setRegistryName("item_confetti");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.CONFETTI, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.8F);
        if(!worldIn.isRemote){
            Vector3d pos = playerIn.getPositionVec().add(playerIn.getLookVec()).add(0d, 1d, 0d);
            ConfettiParticlePacket pack = new ConfettiParticlePacket(32, pos.x, pos.y, pos.z);
            XercaMod.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, 64.0D, playerIn.world.getDimensionKey())), pack);
        }

        final ItemStack heldItem = playerIn.getHeldItem(hand);
        if (!playerIn.isCreative()) {
            heldItem.shrink(1);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isConfettiEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }
}
