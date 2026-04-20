package xerca.xercatools.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercatools.item.ItemScythe;
import xerca.xercatools.item.ItemWarhammer;
import xerca.xercatools.item.Items;

import java.lang.reflect.Method;

public final class XercaToolsClient implements ClientModInitializer {
    private static final ResourceLocation PULLING = ResourceLocation.fromNamespaceAndPath("minecraft", "pulling");
    private static final ResourceLocation PULL = ResourceLocation.fromNamespaceAndPath("minecraft", "pull");

    @Override
    public void onInitializeClient() {
        registerBowLikeProperties(Items.WOODEN_SCYTHE);
        registerBowLikeProperties(Items.STONE_SCYTHE);
        registerBowLikeProperties(Items.IRON_SCYTHE);
        registerBowLikeProperties(Items.GOLDEN_SCYTHE);
        registerBowLikeProperties(Items.DIAMOND_SCYTHE);
        registerBowLikeProperties(Items.NETHERITE_SCYTHE);
        registerBowLikeProperties(Items.ITEM_STONE_WARHAMMER);
        registerBowLikeProperties(Items.ITEM_IRON_WARHAMMER);
        registerBowLikeProperties(Items.ITEM_GOLD_WARHAMMER);
        registerBowLikeProperties(Items.ITEM_DIAMOND_WARHAMMER);
        registerBowLikeProperties(Items.ITEM_NETHERITE_WARHAMMER);
    }

    private static void registerBowLikeProperties(Item item) {
        registerProperty(item, PULLING, (stack, level, entity, seed) -> isUsingThisStack(entity, stack) ? 1.0F : 0.0F);
        registerProperty(item, PULL, (stack, level, entity, seed) -> {
            if (!isUsingThisStack(entity, stack)) {
                return 0.0F;
            }

            int remaining = entity.getUseItemRemainingTicks();
            float useTime = stack.getUseDuration(entity) - remaining;
            float fullUseTime = stack.getItem() instanceof ItemWarhammer
                    ? ItemWarhammer.getFullUseSeconds(entity.level().registryAccess(), stack) * 20.0F
                    : ItemScythe.getFullUseSeconds(stack) * 20.0F;
            return Mth.clamp(useTime / fullUseTime, 0.0F, 1.0F);
        });
    }

    private static boolean isUsingThisStack(LivingEntity entity, ItemStack stack) {
        return entity != null && entity.isUsingItem() && ItemStack.isSameItemSameComponents(entity.getUseItem(), stack);
    }

    private static void registerProperty(Item item, ResourceLocation id, ClampedItemPropertyFunction function) {
        try {
            Method method = ItemProperties.class.getDeclaredMethod("register", Item.class, ResourceLocation.class, ClampedItemPropertyFunction.class);
            method.setAccessible(true);
            method.invoke(null, item, id, function);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to register item property " + id + " for " + item, e);
        }
    }
}
