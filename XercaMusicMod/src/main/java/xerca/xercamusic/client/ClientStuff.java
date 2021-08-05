package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.MusicEndedPacket;

public class ClientStuff {
    static public void showMusicGui(){
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack heldItem = player.getMainHandItem();
        if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemMusicSheet){
            Minecraft.getInstance().setScreen(new GuiMusicSheet(player, heldItem.getTag(), new TranslatableComponent("item.xercamusic.music_sheet")));
        }
    }

    static public void showInstrumentGui(){
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack heldItem = player.getMainHandItem();
        if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemInstrument){
            Minecraft.getInstance().setScreen(new GuiInstrument(player, (ItemInstrument) heldItem.getItem(), new TranslatableComponent("item.xercamusic.instrument_gui")));
        }
    }

    static public void showInstrumentGui(ItemInstrument instrument){
        LocalPlayer player = Minecraft.getInstance().player;
        Minecraft.getInstance().setScreen(new GuiInstrument(player, instrument, new TranslatableComponent("item.xercamusic.instrument_gui")));
    }

    static public NoteSound playNote(SoundEvent event, double x, double y, double z) {
        return playNote(event, x, y, z, SoundSource.PLAYERS, 3.5f, 1.0f);
    }

    static public NoteSound playNote(SoundEvent event, double x, double y, double z, SoundSource category, float volume, float pitch) {
        NoteSound sound = new NoteSound(event, category, (float)x, (float)y, (float)z, volume, pitch);
        Minecraft.getInstance().getSoundManager().play(sound);
        return sound;
    }

    static public void endMusic(int spiritID, int playerID) {
        if (playerID == Minecraft.getInstance().player.getId()) {
            MusicEndedPacket pack = new MusicEndedPacket(spiritID);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);
        }
    }

    // Registration for loot modifier (used for Voice of God in desert temples)
    @Mod.EventBusSubscriber(modid = XercaMusic.MODID, value= Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandlerClient {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(Entities.MUSIC_SPIRIT, new RenderNothingFactory());
        }
    }
}
