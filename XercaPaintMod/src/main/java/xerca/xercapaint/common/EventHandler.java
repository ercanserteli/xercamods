package xerca.xercapaint.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.entity.EntityEasel;

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
    public static void onAttributeCreationEvent(EntityAttributeCreationEvent event) {
        event.put(Entities.EASEL, EntityEasel.createLivingAttributes().build());
    }
}
