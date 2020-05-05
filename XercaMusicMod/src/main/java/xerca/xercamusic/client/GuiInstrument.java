package xerca.xercamusic.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.packets.MusicUpdatePacket;
import xerca.xercamusic.common.packets.SingleNotePacket;

@OnlyIn(Dist.CLIENT)
public class GuiInstrument extends Screen {
    private static final ResourceLocation insGuiTextures = new ResourceLocation(XercaMusic.MODID, "textures/gui/instrument_gui.png");

    private int guiBaseX = 45;
    private int guiBaseY = 80;
    private int pushedButton = -1;

    private static final int guiHeight = 105;
    private static final int guiWidth = 401;
    private static final int guiMarginWidth = 7;
    private static final int guiNoteWidth = 8;
    private static final int guiOctaveWidth = guiNoteWidth * 12 + 1;

    private final PlayerEntity player;
    private final ItemInstrument instrument;
    private NoteSound lastPlayed = null;

    GuiInstrument(PlayerEntity player, ItemInstrument instrument, ITextComponent title) {
        super(title);
        this.player = player;
        this.instrument = instrument;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        guiBaseX = (this.width - guiWidth) / 2;
        guiBaseY = (this.height - guiHeight) / 2;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getInstance().getTextureManager().bindTexture(insGuiTextures);

        blit(guiBaseX, guiBaseY, this.getBlitOffset(), 0, 0, guiWidth, guiHeight, 512, 512);

        if(pushedButton >= 0 && pushedButton < 48){
            int pushedOctave = pushedButton / 12;
            int x = guiBaseX + guiMarginWidth + pushedButton*guiNoteWidth + pushedOctave;
            int y = guiBaseY + 11;
            blit(x, y, this.getBlitOffset(), 402, 11, 7, 82, 512, 512);
        }
    }

    @Override
    public boolean mouseClicked(double dmouseX, double dmouseY, int mouseButton) {
        //System.out.println("Mouse clicked: "+mouseX+", "+mouseY+" with "+mouseButton);
        int mouseX = (int)Math.round(dmouseX);
        int mouseY = (int)Math.round(dmouseY);

        int buttonBaseX = guiBaseX + guiMarginWidth;

        if(mouseX >= buttonBaseX && mouseX <= buttonBaseX + guiWidth && mouseY >= guiBaseY + 9 && mouseY <= guiBaseY + guiHeight - 10){
            int octave = (mouseX - buttonBaseX) / (guiOctaveWidth);
            int note = ((mouseX - buttonBaseX) % guiOctaveWidth) / guiNoteWidth;
            if(note < 12){
                int playNote = octave * 12 + note;
                if(playNote >= 0 && playNote < 48){
                    playSound(playNote);
                    pushedButton = playNote;
                }
            }

        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        pushedButton = -1;

        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void playSound(int note){
        if(instrument.shouldCutOff && lastPlayed != null){
            lastPlayed.stopSound();
        }

        SoundEvent noteSound = instrument.getSound(note);
        lastPlayed = XercaMusic.proxy.playNote(noteSound, player.getPosX(), player.getPosY(), player.getPosZ());
        player.world.addParticle(ParticleTypes.NOTE, player.getPosX() + 0.5D, player.getPosY() + 2.2D, player.getPosZ() + 0.5D, note / 24.0D, 0.0D, 0.0D);

        SingleNotePacket pack = new SingleNotePacket(note, instrument);
        XercaMusic.NETWORK_HANDLER.sendToServer(pack);
    }

    @Override
    public boolean mouseDragged(double posX, double posY, int mouseButton, double deltaX, double deltaY) {
        return super.mouseDragged(posX, posY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        setFocused(null);
        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void removed() {

    }

}
