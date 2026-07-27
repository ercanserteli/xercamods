package xerca.xercatools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import xerca.xercatools.Mod;
import xerca.xercatools.entity.EntityGrabHook;
import xerca.xercatools.item.ItemFlask;
import xerca.xercatools.item.ItemGrabHook;
import xerca.xercatools.item.ItemScythe;
import xerca.xercatools.item.ItemWarhammer;
import xerca.xercatools.item.Items;

@EventBusSubscriber(modid = Mod.MOD_ID, value = Dist.CLIENT)
public final class ModClient {
    private static final ResourceLocation PULLING = ResourceLocation.fromNamespaceAndPath("minecraft", "pulling");
    private static final ResourceLocation PULL = ResourceLocation.fromNamespaceAndPath("minecraft", "pull");
    private static final ResourceLocation CAST = Mod.id("cast");

    private ModClient() {
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Mod.HOOK, RenderGrabHook::new);
        event.registerEntityRenderer(Mod.HEALTH_ORB, RenderHealthOrb::new);
        event.registerEntityRenderer(Mod.ENTITY_CONFETTI_BALL, new RenderConfettiBallFactory());
    }

    @SubscribeEvent
    static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(Mod.CONFETTI_PARTICLE, ConfettiParticle.Provider::new);
    }

    @SubscribeEvent
    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColor flaskColor = (stack, tintIndex) -> tintIndex > 0 ? -1 : ItemFlask.getPotionContents(stack).getColor();
        event.register(flaskColor, Items.FLASK, Items.ENDER_BOW);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerBowLikeProperties(Items.GRAB_HOOK);
            registerProperty(Items.GRAB_HOOK, CAST, (stack, level, entity, seed) -> ItemGrabHook.isCast(stack) ? 1.0F : 0.0F);
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
        });
    }

    @SubscribeEvent
    static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() && event.getEntity() instanceof EntityGrabHook hook) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(new HookSound(hook));
        }
    }

    private static void registerBowLikeProperties(Item item) {
        registerProperty(item, PULLING, (stack, level, entity, seed) -> entity != null && isUsingThisStack(entity, stack) ? 1.0F : 0.0F);
        registerProperty(item, PULL, (stack, level, entity, seed) -> {
            if (entity == null || !isUsingThisStack(entity, stack)) {
                return 0.0F;
            }

            int remaining = entity.getUseItemRemainingTicks();
            float useTime = (float) stack.getUseDuration(entity) - remaining;
            float fullUseTime = stack.getItem() instanceof ItemWarhammer
                    ? ItemWarhammer.getFullUseSeconds(entity.level().registryAccess(), stack) * 20.0F
                    : ItemScythe.FULL_USE_SECONDS * 20.0F;
            return Mth.clamp(useTime / fullUseTime, 0.0F, 1.0F);
        });
    }

    private static boolean isUsingThisStack(LivingEntity entity, ItemStack stack) {
        return entity.isUsingItem() && ItemStack.isSameItemSameComponents(entity.getUseItem(), stack);
    }

    private static void registerProperty(Item item, ResourceLocation id, ClampedItemPropertyFunction function) {
        ItemProperties.register(item, id, function);
    }
}
