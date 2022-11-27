package xerca.xercafood.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercafood.common.data.BlockTags;
import xerca.xercamod.common.entity.EntityConfettiBall;
import xerca.xercafood.common.entity.EntityTomato;
import xerca.xercafood.common.item.Items;

import javax.annotation.Nonnull;

public class XercaFood {
    public static final String MODID = "xercafood";
    public static final String NAME = "Xerca Food";

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static final Logger LOGGER = LogManager.getLogger();

    public XercaFood() {
        registerTriggers();

        // Making tomato dispensable by dispenser
        DispenserBlock.registerBehavior(Items.ITEM_TOMATO, new AbstractProjectileDispenseBehavior()
        {
            @Nonnull
            protected Projectile getProjectile(@Nonnull Level worldIn, @Nonnull Position position, @Nonnull ItemStack stackIn)
            {
                return new EntityTomato(worldIn, position.x(), position.y(), position.z());
            }
        });

        Items.registerCompostables();
        registerPotions(event);
    }

    private void registerPotions(FMLCommonSetupEvent event) {
        event.enqueueWork( () -> {
            BrewingRecipeRegistry.addRecipe(
                    Ingredient.of(PotionUtils.setPotion(new ItemStack(net.minecraft.world.item.Items.POTION), Potions.WATER)),
                    Ingredient.of(new ItemStack(Items.COLA_POWDER)),
                    new ItemStack(Items.COLA_EXTRACT));
        } );
    }

    private void registerTriggers() {
        for (int i = 0; i < Triggers.TRIGGER_ARRAY.length; i++)
        {
            CriteriaTriggers.register(Triggers.TRIGGER_ARRAY[i]);
        }

        CriteriaTriggers.register(Triggers.CONFIG_CHECK);
    }

    public static class RegistrationHandler {
        public static void registerLootModifiers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
            event.getRegistry().register(new SeedLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"rice_seeds")));
            event.getRegistry().register(new SeedLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"tomato_seeds")));
            event.getRegistry().register(new SeedLootModifier.Serializer().setRegistryName(new ResourceLocation(MODID,"tea_seeds")));
        }

        public static void registerDataEvent(final GatherDataEvent event) {
            event.getGenerator().addProvider(new BlockTags(event.getGenerator(), event.getExistingFileHelper()));
        }
    }
}
