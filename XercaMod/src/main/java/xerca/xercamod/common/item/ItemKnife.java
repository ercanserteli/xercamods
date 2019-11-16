package xerca.xercamod.common.item;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.Triggers;
import xerca.xercamod.common.packets.KnifeAttackPacket;

import javax.annotation.Nonnull;

public class ItemKnife extends Item {
    private static final float weaponDamage = 2.0f;
    private static final int maxDamage = 240;

    ItemKnife() {
        super(new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1).defaultMaxDamage(maxDamage));
        this.setRegistryName("item_knife");
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity e1, LivingEntity e2) {
        float damage = 0.0F;
        if (e2.isSneaking() && MathHelper.abs(MathHelper.wrapDegrees(e1.rotationYaw) - MathHelper.wrapDegrees(e2.rotationYaw)) < 65.0F) {
            if(!e1.world.isRemote){
                SAnimateHandPacket packetOut = new SAnimateHandPacket(e1, 4);
                ((ServerWorld)e1.world).getChunkProvider().sendToTrackingAndSelf(e2, packetOut);

            }
            e2.world.playSound(null, e1.posX, e1.posY + 0.5d, e1.posZ, SoundEvents.SNEAK_HIT, SoundCategory.PLAYERS, 1.0f, e2.world.rand.nextFloat() * 0.2F + 0.8F);

            damage += 8.0F;
            PlayerEntity player = (PlayerEntity) e2;
            DamageSource damagesource = DamageSource.causePlayerDamage(player);
            e1.attackEntityFrom(damagesource, damage);
            if(e1.getHealth() <= 0f) {
                Triggers.ASSASSINATE.trigger((ServerPlayerEntity) player);
            }
        }
        stack.damageItem(1, e2, (p) -> p.sendBreakAnimation(Hand.MAIN_HAND));

        return true;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        if (hand == Hand.OFF_HAND) {
            playerIn.swingArm(hand);
            if (worldIn.isRemote) {
                Minecraft mine = Minecraft.getInstance();
                if (mine.objectMouseOver != null && mine.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                    Entity target = ((EntityRayTraceResult) mine.objectMouseOver).getEntity();
                    KnifeAttackPacket pack = new KnifeAttackPacket(false, target.getEntityId());
                    XercaMod.NETWORK_HANDLER.sendToServer(pack);
                }
            }
            playerIn.getCooldownTracker().setCooldown(this, 15);
            return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
        }
        return new ActionResult<>(ActionResultType.PASS, heldItem);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EquipmentSlotType.MAINHAND || slot == EquipmentSlotType.OFFHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", weaponDamage, AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return stack.getItem() == this;
    }
}
