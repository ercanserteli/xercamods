package xerca.xercamusic.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.packets.SingleNotePacket;

@OnlyIn(Dist.CLIENT)
public class GuiInstrument extends Screen {
    private static final ResourceLocation insGuiTextures = new ResourceLocation(XercaMusic.MODID, "textures/gui/instrument_gui.png");

    private int guiBaseX = 45;
    private int guiBaseY = 80;
    private int pushedButton = -1;
    private int currentKeyboardOctave = 2;

    private static final int guiHeight = 105;
    private static final int guiWidth = 401;
    private static final int guiMarginWidth = 7;
    private static final int guiNoteWidth = 8;
    private static final int guiOctaveWidth = guiNoteWidth * 12 + 1;
    private static final int guiOctaveHighlightY = 111;
    private static final int guiOctaveHighlightWidth = 98;
    private static final int guiOctaveHighlightHeight = 92;

    private final Player player;
    private final ItemInstrument instrument;
    private NoteSound lastPlayed = null;

    GuiInstrument(Player player, ItemInstrument instrument, Component title) {
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
//        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        Minecraft.getInstance().getTextureManager().bind(insGuiTextures);
        RenderSystem.setShaderTexture(0, insGuiTextures);

        blit(matrixStack, guiBaseX, guiBaseY, this.getBlitOffset(), 0, 0, guiWidth, guiHeight, 512, 512);

        if(pushedButton >= 0 && pushedButton < 48){
            int pushedOctave = pushedButton / 12;
            int x = guiBaseX + guiMarginWidth + pushedButton*guiNoteWidth + pushedOctave;
            int y = guiBaseY + 11;
            blit(matrixStack, x, y, this.getBlitOffset(), 402, 11, 7, 82, 512, 512);
        }

        int octaveHighlightX = guiBaseX + guiMarginWidth + currentKeyboardOctave * guiOctaveWidth - 1;
        int octaveHighlightY = guiBaseY + 3;
        blit(matrixStack, octaveHighlightX, octaveHighlightY, this.getBlitOffset(), 0, guiOctaveHighlightY, guiOctaveHighlightWidth, guiOctaveHighlightHeight, 512, 512);
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
        lastPlayed = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientStuff.playNote(noteSound, player.getX(), player.getY(), player.getZ()));
        player.level.addParticle(ParticleTypes.NOTE, player.getX(), player.getY() + 2.2D, player.getZ(), note / 24.0D, 0.0D, 0.0D);

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

        if (scanCode >= 16 && scanCode <= 27) {
            int playNote = scanCode - 16 + 12 * currentKeyboardOctave;

            if(playNote >= 0 && playNote < 48 && pushedButton != playNote){
                playSound(playNote);
                pushedButton = playNote;
            }
        }
        if (scanCode >= 30 && scanCode <= 33) {
            currentKeyboardOctave = scanCode - 30;
        }
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers){
        pushedButton = -1;
        return true;
    }

}
