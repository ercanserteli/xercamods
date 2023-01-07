package xerca.xercamod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
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
import xerca.xercamod.common.tile_entity.TileEntities;

import static xerca.xercamod.common.XercaMod.LOGGER;

public class ClientStuff {
    private static Minecraft mc;

    @SuppressWarnings("removal")
    @Mod.EventBusSubscriber(modid = XercaMod.MODID, value=Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ModBusSubscriber{
        private static void pizzaRenderLayers(){
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_PEPPERONI_PEPPERONI_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_PEPPERONI_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_MUSHROOM_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_MUSHROOM_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_PEPPERONI_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MUSHROOM_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MUSHROOM_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MEAT_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MEAT_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MEAT_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_PEPPERONI_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MUSHROOM_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MUSHROOM_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MEAT_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MEAT_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MEAT_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH_FISH.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_PEPPERONI_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MUSHROOM_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MUSHROOM_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MEAT_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MEAT_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MEAT_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH_FISH.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_FISH.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN_CHICKEN.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_PEPPERONI_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH_FISH.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_FISH.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN_CHICKEN.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_PEPPERONI.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MUSHROOM.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_MEAT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_FISH.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA_CHICKEN.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA.get(), RenderType.cutoutMipped());
        }

        @SubscribeEvent
        public static void clientSetupHandler(final FMLClientSetupEvent event) {
            event.enqueueWork(()->{
                MenuScreens.register(TileEntities.CONTAINER_FUNCTIONAL_BOOKCASE.get(), GuiFunctionalBookcase::new);
                MenuScreens.register(TileEntities.CONTAINER_CARVING_STATION.get(), CarvingStationScreen::new);

                ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_1.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_2.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_3.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_4.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_5.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_6.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_7.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.CARVED_ACACIA_8.get(), RenderType.cutoutMipped());

                ItemBlockRenderTypes.setRenderLayer(Blocks.OMNI_CHEST.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.VAT.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.VAT_CHEESE.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(Blocks.VAT_MILK.get(), RenderType.cutoutMipped());

                pizzaRenderLayers();

                registerItemModelsProperties();
            });
            mc = Minecraft.getInstance();
        }

        @SubscribeEvent
        public static void onModelBakeEvent(ModelEvent.ModifyBakingResult event) {
            bakeCrimsonModel(Blocks.CARVED_CRIMSON_1.get(), event);
            bakeCrimsonModel(Blocks.CARVED_CRIMSON_2.get(), event);
            bakeCrimsonModel(Blocks.CARVED_CRIMSON_3.get(), event);
            bakeCrimsonModel(Blocks.CARVED_CRIMSON_4.get(), event);
            bakeCrimsonModel(Blocks.CARVED_CRIMSON_5.get(), event);
            bakeCrimsonModel(Blocks.CARVED_CRIMSON_6.get(), event);
            bakeCrimsonModel(Blocks.CARVED_CRIMSON_7.get(), event);
            bakeCrimsonModel(Blocks.CARVED_CRIMSON_8.get(), event);
        }

        private static void bakeCrimsonModel(Block crimson, ModelEvent.ModifyBakingResult event){
            for (BlockState blockState : crimson.getStateDefinition().getPossibleStates()) {
                ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
                BakedModel existingModel = event.getModels().get(variantMRL);
                if (existingModel == null) {
                    LOGGER.warn("Did not find the expected vanilla baked model(s) for CarvedCrimsonBakedModel in registry");
                } else if (existingModel instanceof CarvedCrimsonBakedModel) {
                    LOGGER.warn("Tried to replace CarvedCrimsonBakedModel twice");
                } else {
                    CarvedCrimsonBakedModel customModel = new CarvedCrimsonBakedModel(existingModel);
                    event.getModels().put(variantMRL, customModel);
                }
            }
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.TOMATO.get(), new RenderTomatoFactory());
            event.registerEntityRenderer(Entities.CONFETTI_BALL.get(), new RenderConfettiBallFactory());
            event.registerEntityRenderer(Entities.HOOK.get(), new RenderHookFactory());
            event.registerEntityRenderer(Entities.CUSHION.get(), new RenderCushionFactory());
            event.registerEntityRenderer(Entities.HEALTH_ORB.get(), new RenderHealthOrbFactory());

            event.registerBlockEntityRenderer(TileEntities.DONER.get(), DonerTileEntityRenderer::new);
            event.registerBlockEntityRenderer(TileEntities.OMNI_CHEST.get(), OmniChestTileEntityRenderer::new);
        }

        static private void registerItemModelsProperties(){
            ItemPropertyFunction warhammerPull = (stack, worldIn, entityIn, i) -> {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    return ((entityIn.getUseItem().getItem() instanceof ItemWarhammer warhammer)) ? (stack.getUseDuration() - entityIn.getUseItemRemainingTicks()) / (20.0F * warhammer.getFullUseSeconds(stack)) : 0.0F;
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
            ItemPropertyFunction pulling = (stack, worldIn, entityIn, i) -> entityIn != null && entityIn.isUsingItem() && entityIn.getUseItem() == stack ? 1.0F : 0.0F;

            ItemProperties.register(Items.STONE_SCYTHE.get(), new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.STONE_SCYTHE.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.WOODEN_SCYTHE.get(), new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.WOODEN_SCYTHE.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.IRON_SCYTHE.get(), new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.IRON_SCYTHE.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.GOLDEN_SCYTHE.get(), new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.GOLDEN_SCYTHE.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.DIAMOND_SCYTHE.get(), new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.DIAMOND_SCYTHE.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.NETHERITE_SCYTHE.get(), new ResourceLocation("pull"), scythePull);
            ItemProperties.register(Items.NETHERITE_SCYTHE.get(), new ResourceLocation("pulling"), pulling);

            ItemProperties.register(Items.ITEM_GRAB_HOOK.get(), new ResourceLocation("pull"), grabHookPull);
            ItemProperties.register(Items.ITEM_GRAB_HOOK.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_GRAB_HOOK.get(), new ResourceLocation("cast"), (stack, worldIn, entityIn, i) -> {
                        if(!stack.hasTag() || stack.getTag() == null) return 0.0f;
                        CompoundTag tag = stack.getTag();
                        if(!tag.contains("cast")) return 0.0f;
                        return tag.getBoolean("cast") ? 1.0F : 0.0F;
                    }
            );

            ItemProperties.register(Items.ITEM_STONE_WARHAMMER.get(), new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_STONE_WARHAMMER.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_IRON_WARHAMMER.get(), new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_IRON_WARHAMMER.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_GOLD_WARHAMMER.get(), new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_GOLD_WARHAMMER.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_DIAMOND_WARHAMMER.get(), new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_DIAMOND_WARHAMMER.get(), new ResourceLocation("pulling"), pulling);
            ItemProperties.register(Items.ITEM_NETHERITE_WARHAMMER.get(), new ResourceLocation("pull"), warhammerPull);
            ItemProperties.register(Items.ITEM_NETHERITE_WARHAMMER.get(), new ResourceLocation("pulling"), pulling);

        }

        @SubscribeEvent
        public static void handleItemColors(RegisterColorHandlersEvent.Item event) {
            if(Items.FLASK.isPresent()){
                event.getItemColors().register(
                        (itemStack, colorIndex) -> colorIndex > 0 ? -1 : PotionUtils.getColor(itemStack), Items.FLASK.get()
                );
            }
            if(Items.ENDER_BOW.isPresent()){
                event.getItemColors().register(
                        (itemStack, colorIndex) -> colorIndex > 0 ? -1 : PotionUtils.getColor(itemStack), Items.ENDER_BOW.get()
                );
            }
        }

//        @SubscribeEvent
//        public static void handleTextureStitch(TextureStitchEvent event) {
//            if(event.getAtlas().location().equals(Sheets.CHEST_SHEET)){
//                event.addSprite(OmniChestTileEntityRenderer.texture);
//            }
//        }
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
            if (ent instanceof EntityHook hook) {
                if (ent.level.isClientSide) {
                    mc.getSoundManager().play(new HookSound(hook, false));
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
            LOGGER.debug("ClientLoggedOut Event");
            Config.bakeConfig();
        }

        @SubscribeEvent
        public static void inputUpdateEvent(MovementInputUpdateEvent updateEvent) {
            ItemStack activeItem = updateEvent.getEntity().getUseItem();
            if(activeItem.getItem() instanceof ItemWarhammer && updateEvent.getEntity().isUsingItem()){
                int legerityLevel = EnchantmentHelper.getItemEnchantmentLevel(Items.ENCHANTMENT_QUICK.get(), activeItem);
                if(legerityLevel > 4){
                    legerityLevel = 4;
                }
                float bonus = 3F + 0.5F*legerityLevel;
                updateEvent.getInput().leftImpulse *= bonus;
                updateEvent.getInput().forwardImpulse *= bonus;
            }
        }
    }
}
