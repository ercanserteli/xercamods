package xerca.xercafood.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

import javax.annotation.Nonnull;

public class ItemKnife extends Item implements FabricItem {
    private static final float weaponDamage = 2.0f;
    private static final int maxDamage = 240;
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    ItemKnife() {
        super(new Item.Properties().stacksTo(1).defaultDurability(maxDamage));

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", weaponDamage, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Nonnull
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@Nonnull EquipmentSlot slot) {
        return (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public int getEnchantmentValue()
    {
        return Tiers.IRON.getEnchantmentValue();
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        ItemStack ret = new ItemStack(this);
        ret.setTag(stack.getTag());

        ret.setDamageValue(stack.getDamageValue() + 1);
        if(ret.getDamageValue() >= ret.getMaxDamage()){
            return ItemStack.EMPTY;
        }
        return ret;
    }
}
