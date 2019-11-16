package xerca.xercamod.common.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.entity.EntityHook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemGrabHook extends FishingRodItem {

    public ItemGrabHook() {
        super((new Item.Properties()).group(ItemGroup.COMBAT).defaultMaxDamage(210));
        this.setRegistryName("item_grab_hook");
        this.addPropertyOverride(new ResourceLocation(XercaMod.MODID, "cast"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            @Override
            public float call(@Nonnull ItemStack itemStack, @Nullable World world, @Nullable LivingEntity entityLivingBase) {
                if(!itemStack.hasTag()) return 0.0f;
                CompoundNBT tag = itemStack.getTag();
                if(!tag.contains("cast")) return 0.0f;

                return tag.getBoolean("cast") ? 1.0F : 0.0F;//);
            }
        });
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        playerIn.getCooldownTracker().setCooldown(this, 40);
        heldItem.damageItem(1, playerIn, (p) -> p.sendBreakAnimation(hand));
        if (!worldIn.isRemote) {
            worldIn.addEntity(new EntityHook(worldIn, playerIn, heldItem));
        }

        playerIn.swingArm(hand);

        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment.type == EnchantmentType.BREAKABLE || enchantment == Items.ENCHANTMENT_GRAPPLING;
    }
}
