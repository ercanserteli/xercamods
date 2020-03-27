package xerca.xercamusic.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import xerca.xercamusic.common.Proxy;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.entity.Entities;
import xerca.xercamusic.common.entity.EntityMusicSpirit;
import xerca.xercamusic.common.item.ItemMusicSheet;
import xerca.xercamusic.common.packets.MusicEndedPacket;

public class ClientProxy implements Proxy {

    public void preInit() {
        RenderingRegistry.registerEntityRenderingHandler(Entities.MUSIC_SPIRIT, new RenderNothingFactory());
    }

    public void init() {

    }

    public void showMusicGui(){
        ClientPlayerEntity player = Minecraft.getInstance().player;
        ItemStack heldItem = player.getHeldItemMainhand();
        if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemMusicSheet){
            Minecraft.getInstance().displayGuiScreen(new GuiMusicSheet(player, heldItem.getTag(), new TranslationTextComponent("item.xercamusic.music_sheet")));
        }
    }

    @Override
    public NoteSound playNote(SoundEvent event, double x, double y, double z) {
        return playNote(event, x, y, z, SoundCategory.PLAYERS, 3.5f, 1.0f);
    }

    @Override
    public NoteSound playNote(SoundEvent event, double x, double y, double z, SoundCategory category, float volume, float pitch) {
        NoteSound sound = new NoteSound(event, category, (float)x, (float)y, (float)z, volume, pitch);
        Minecraft.getInstance().getSoundHandler().play(sound);
        return sound;
    }

    @Override
    public void endMusic(int spiritID, int playerID) {
        if (playerID == Minecraft.getInstance().player.getEntityId()) {
            MusicEndedPacket pack = new MusicEndedPacket(spiritID);
            XercaMusic.NETWORK_HANDLER.sendToServer(pack);
        }
    }
}
