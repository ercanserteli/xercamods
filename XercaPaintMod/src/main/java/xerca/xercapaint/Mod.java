package xerca.xercapaint;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.packets.*;

public class Mod implements ModInitializer {
    public static final String modId = "xercapaint";
    public static final Logger LOGGER = LogManager.getLogger(modId);

    public static final ResourceLocation CANVAS_UPDATE_PACKET_ID = new ResourceLocation(Mod.modId, "canvas_update");
    public static final ResourceLocation CANVAS_MINI_UPDATE_PACKET_ID = new ResourceLocation(Mod.modId, "canvas_mini_update");
    public static final ResourceLocation CLOSE_GUI_PACKET_ID = new ResourceLocation(Mod.modId, "close_gui");
    public static final ResourceLocation EASEL_LEFT_PACKET_ID = new ResourceLocation(Mod.modId, "easel_left");
    public static final ResourceLocation EXPORT_PAINTING_PACKET_ID = new ResourceLocation(Mod.modId, "export_painting");
    public static final ResourceLocation IMPORT_PAINTING_PACKET_ID = new ResourceLocation(Mod.modId, "import_painting");
    public static final ResourceLocation IMPORT_PAINTING_SEND_PACKET_ID = new ResourceLocation(Mod.modId, "import_painting_send");
    public static final ResourceLocation OPEN_GUI_PACKET_ID = new ResourceLocation(Mod.modId, "open_gui");
    public static final ResourceLocation PALETTE_UPDATE_PACKET_ID = new ResourceLocation(Mod.modId, "palette_update");
    public static final ResourceLocation PICTURE_REQUEST_PACKET_ID = new ResourceLocation(Mod.modId, "picture_request");
    public static final ResourceLocation PICTURE_SEND_PACKET_ID = new ResourceLocation(Mod.modId, "picture_send");
    public static final ResourceLocation ADD_CANVAS_PACKET_ID = new ResourceLocation(Mod.modId, "add_canvas");

    @Override
    public void onInitialize() {
        Items.registerItems();
        Items.registerRecipes();
        Entities.registerEntities();
        SoundEvents.registerSoundEvents();

        ServerPlayNetworking.registerGlobalReceiver(CANVAS_UPDATE_PACKET_ID, new CanvasUpdatePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(CANVAS_MINI_UPDATE_PACKET_ID, new CanvasMiniUpdatePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(EASEL_LEFT_PACKET_ID, new EaselLeftPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(IMPORT_PAINTING_SEND_PACKET_ID, new ImportPaintingSendPacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(PALETTE_UPDATE_PACKET_ID, new PaletteUpdatePacketHandler());
        ServerPlayNetworking.registerGlobalReceiver(PICTURE_REQUEST_PACKET_ID, new PictureRequestPacketHandler());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            CommandImport.register(dispatcher);
            CommandExport.register(dispatcher);
        });
    }
}
