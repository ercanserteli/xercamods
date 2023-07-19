package xerca.xercapaint.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xerca.xercapaint.common.item.Items;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = XercaPaint.MODID)
class EventHandler {
    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        CommandImport.register(commandDispatcher);
        CommandExport.register(commandDispatcher);
    }
}

@Mod.EventBusSubscriber(modid = XercaPaint.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
class EventHandlerMod {
    @SubscribeEvent
    public static void buildContents(CreativeModeTabEvent.Register event) {
        ItemStack fullPalette = new ItemStack(Items.ITEM_PALETTE.get());
        byte[] basicColors = new byte[16];
        Arrays.fill(basicColors, (byte)1);
        fullPalette.getOrCreateTag().putByteArray("basic", basicColors);

        event.registerCreativeModeTab(new ResourceLocation(XercaPaint.MODID, "paint_tab"), builder ->
                builder.title(Component.translatable("item_group." + XercaPaint.MODID + ".paint_tab"))
                        .icon(() -> new ItemStack(Items.ITEM_PALETTE.get()))
                        .displayItems((params, output) -> {
                            output.accept(Items.ITEM_PALETTE.get());
                            output.accept(fullPalette);
                            output.accept(Items.ITEM_CANVAS.get());
                            output.accept(Items.ITEM_CANVAS_LONG.get());
                            output.accept(Items.ITEM_CANVAS_TALL.get());
                            output.accept(Items.ITEM_CANVAS_LARGE.get());
                            output.accept(Items.ITEM_EASEL.get());
                        })
        );
    }
}
