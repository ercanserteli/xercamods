package xerca.xercapaint.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = XercaPaint.MODID)
class EventHandler {
    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        CommandImport.register(commandDispatcher);
        CommandExport.register(commandDispatcher);
    }
}