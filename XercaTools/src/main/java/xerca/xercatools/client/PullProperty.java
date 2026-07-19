package xerca.xercatools.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import xerca.xercatools.Mod;
import xerca.xercatools.item.ItemScythe;
import xerca.xercatools.item.ItemWarhammer;

public record PullProperty() implements RangeSelectItemModelProperty {
    public static final Identifier ID = Mod.id("pull");
    public static final MapCodec<PullProperty> MAP_CODEC = MapCodec.unit(new PullProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        LivingEntity entity = owner != null ? owner.asLivingEntity() : null;
        if (entity == null || !entity.isUsingItem() || !ItemStack.isSameItemSameComponents(entity.getUseItem(), stack)) {
            return 0.0F;
        }
        int remaining = entity.getUseItemRemainingTicks();
        float useTime = (float) stack.getUseDuration(entity) - remaining;
        RegistryAccess registryAccess = (level != null ? level : entity.level()).registryAccess();
        float fullUseTime = stack.getItem() instanceof ItemWarhammer
                ? ItemWarhammer.getFullUseSeconds(registryAccess, stack) * 20.0F
                : ItemScythe.FULL_USE_SECONDS * 20.0F;
        return Mth.clamp(useTime / fullUseTime, 0.0F, 1.0F);
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
