package xerca.xercamod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.HookReturningEvent;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.block.Blocks;
import xerca.xercamod.common.entity.Entities;
import xerca.xercamod.common.entity.EntityHook;
import xerca.xercamod.common.item.ItemGrabHook;
import xerca.xercamod.common.item.ItemScythe;
import xerca.xercamod.common.item.ItemWarhammer;
import xerca.xercamod.common.item.Items;
import xerca.xercamod.common.tile_entity.XercaTileEntities;

public class ClientStuff {
    private static final ResourceLocation blackTexture = new ResourceLocation(XercaMod.MODID, "textures/misc/black.png");
    private static Minecraft mc;

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusSubscriber{
        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            MenuScreens.register(XercaTileEntities.CONTAINER_FUNCTIONAL_BOOKCASE, GuiFunctionalBookcase::new);
            MenuScreens.register(XercaTileEntities.CONTAINER_CARVING_STATION, CarvingStationScreen::new);

//            RenderTypeLookup.setRenderLayer(Blocks.BLOCK_DONER, RenderType.getCutoutMipped());
//            ClientRegistry.bindTileEntityRenderer(XercaTileEntities.DONER, DonerTileEntityRenderer::new);

            ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_TEA_PLANT, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_TOMATO_PLANT, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(Blocks.BLOCK_RICE_PLANT, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_1, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_2, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_3, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_4, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_5, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_6, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_7, RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_8, RenderType.cutoutMipped());

            registerItemModelsProperties();

            mc = Minecraft.getInstance();
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.TOMATO, new RenderTomatoFactory());
            event.registerEntityRenderer(Entities.CONFETTI_BALL, new RenderConfettiBallFactory());
            event.registerEntityRenderer(Entities.HOOK, new RenderHookFactory());
            event.registerEntityRenderer(Entities.CUSHION, new RenderCushionFactory());
            event.registerEntityRenderer(Entities.HEALTH_ORB, new RenderHealthOrbFactory());

            event.registerBlockEntityRenderer(XercaTileEntities.DONER, DonerTileEntityRenderer::new);
        }

        static private void registerItemModelsProperties(){
            ItemPropertyFunction warhammerPull = (stack, worldIn, entityIn, i) -> {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    return ((entityIn.getUseItem().getItem() instanceof ItemWarhammer)) ? (stack.getUseDuration() - entityIn.getUseItemRemainingTicks()) / 20.0F : 0.0F;
                }
            };
            ItemPropertyFunction grabHookPull = (stack, worldIn, entityIn, i) -> {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    return ((entityIn.getUseItem().getItem() instanceof ItemGrabHook)) ? (stack.getUseDuration() - entityIn.getUseItemRemainingTicks()) / 20.0F : 0.0F;
                }
            };
            ItemPropertyFunction scythePull = (stack, worldIn, entityIn, i) -> {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    return ((entityIn.getUseItem().getItem() instanceof ItemScythe)) ? (stack.getUseDuration() - entityIn.getUseItemRemainingTicks()) / 20.0F : 0.0F;
                }
            };
            ItemPropertyFunction pulling = (stack, worldIn, entityIn, i) -> {
                return entityIn != null && entityIn.isUsingItem() && entityIn.getUseItem() == stack ? 1.0F : 0.0F;
            };

            ItemProperties.register(Items.STONE_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.STONE_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.WOODEN_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.WOODEN_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.IRON_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.IRON_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.GOLDEN_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.GOLDEN_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.DIAMOND_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.DIAMOND_SCYTHE, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.NETHERITE_SCYTHE, new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.NETHERITE_SCYTHE, new ResourceLocation("pulling"), pulling);

            ItemProperties.register(Items.ITEM_GRAB_HOOK, new ResourceLocation("pull"), grabHookPull);
            ItemProperties.register(Items.ITEM_GRAB_HOOK, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_GRAB_HOOK, new ResourceLocation("cast"), (stack, worldIn, entityIn, i) -> {
                    if(!stack.hasTag()) return 0.0f;
                    CompoundTag tag = stack.getTag();
                    if(!tag.contains("cast")) return 0.0f;
                    return tag.getBoolean("cast") ? 1.0F : 0.0F;
                }
            );

            ItemProperties.register(Items.ITEM_STONE_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_STONE_WARHAMMER, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_IRON_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_IRON_WARHAMMER, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_GOLD_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_GOLD_WARHAMMER, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_DIAMOND_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_DIAMOND_WARHAMMER, new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_NETHERITE_WARHAMMER, new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_NETHERITE_WARHAMMER, new ResourceLocation("pulling"), pulling);

        }

        @SubscribeEvent
        public static void handleItemColors(ColorHandlerEvent.Item event) {
            if(Items.FLASK != null){
                event.getItemColors().register(
                        (itemStack, colorIndex) -> colorIndex > 0 ? -1 : PotionUtils.getColor(itemStack), Items.FLASK
                );
            }
            if(Items.ENDER_BOW != null){
                event.getItemColors().register(
                        (itemStack, colorIndex) -> colorIndex > 0 ? -1 : PotionUtils.getColor(itemStack), Items.ENDER_BOW
                );
            }
        }
    }

    @Mod.EventBusSubscriber(modid = XercaMod.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    static class ForgeBusSubscriber {
        @SubscribeEvent
        public static void hookReturningEvent(HookReturningEvent ev) {
            EntityHook ent = (EntityHook) ev.getEntity();
            if (ent.level.isClientSide) {
                mc.getSoundManager().play(new HookSound(ent, true));
            }
        }

        @SubscribeEvent
        public static void entityConstEvent(EntityEvent.EntityConstructing ev) {
            Entity ent = ev.getEntity();
            if (ent instanceof EntityHook) {
                EntityHook hook = (EntityHook) ent;
                if (ent.level.isClientSide) {
                    mc.getSoundManager().play(new HookSound(hook, false));
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            XercaMod.LOGGER.debug("ClientLoggedOut Event");
            Config.bakeConfig();
        }

        @SubscribeEvent
        public static void inputUpdateEvent(InputUpdateEvent updateEvent) {
            ItemStack activeItem = updateEvent.getPlayer().getUseItem();
            if(activeItem.getItem() instanceof ItemWarhammer && updateEvent.getPlayer().isUsingItem()){
                int legerityLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_QUICK, activeItem);
                if(legerityLevel > 4){
                    legerityLevel = 4;
                }
                float bonus = 3F + 0.5F*legerityLevel;
                updateEvent.getMovementInput().leftImpulse *= bonus;
                updateEvent.getMovementInput().forwardImpulse *= bonus;
            }
        }
    }
}
