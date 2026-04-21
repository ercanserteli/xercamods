package xerca.xercatools.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xerca.xercatools.Mod;
import xerca.xercatools.entity.EntityGrabHook;
import xerca.xercatools.item.*;

public final class XercaToolsClient implements ClientModInitializer {
    private static final ResourceLocation PULLING = ResourceLocation.fromNamespaceAndPath("minecraft", "pulling");
    private static final ResourceLocation PULL = ResourceLocation.fromNamespaceAndPath("minecraft", "pull");
    private static final ResourceLocation CAST = Mod.id("cast");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Mod.HOOK, RenderGrabHook::new);
        EntityRendererRegistry.register(Mod.HEALTH_ORB, RenderHealthOrb::new);
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof EntityGrabHook hook) {
                Minecraft.getInstance().getSoundManager().play(new HookSound(hook));
            }
        });
        registerGrabHookProperties(Items.GRAB_HOOK);
        registerBowLikeProperties(Items.WOODEN_SCYTHE);
        registerBowLikeProperties(Items.STONE_SCYTHE);
        registerBowLikeProperties(Items.IRON_SCYTHE);
        registerBowLikeProperties(Items.GOLDEN_SCYTHE);
        registerBowLikeProperties(Items.DIAMOND_SCYTHE);
        registerBowLikeProperties(Items.NETHERITE_SCYTHE);
        registerBowLikeProperties(Items.STONE_WARHAMMER);
        registerBowLikeProperties(Items.IRON_WARHAMMER);
        registerBowLikeProperties(Items.GOLD_WARHAMMER);
        registerBowLikeProperties(Items.DIAMOND_WARHAMMER);
        registerBowLikeProperties(Items.NETHERITE_WARHAMMER);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ItemFlask.getPotionContents(stack).getColor(), Items.FLASK, Items.ENDER_BOW);
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

    private static void registerGrabHookProperties(Item item) {
        registerBowLikeProperties(item);
        registerProperty(item, CAST, (stack, level, entity, seed) -> ItemGrabHook.isCast(stack) ? 1.0F : 0.0F);
    }

    private static boolean isUsingThisStack(LivingEntity entity, ItemStack stack) {
        return entity != null && entity.isUsingItem() && ItemStack.isSameItemSameComponents(entity.getUseItem(), stack);
    }

    private static void registerProperty(Item item, ResourceLocation id, ClampedItemPropertyFunction function) {
        ItemProperties.register(item, id, function);
    }
}
