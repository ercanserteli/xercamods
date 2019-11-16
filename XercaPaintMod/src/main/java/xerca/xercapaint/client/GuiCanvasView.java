package xerca.xercapaint.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class GuiCanvasView extends Screen {
    private final static int canvasX = 140;
    private final static int canvasY = 40;

    private boolean isSigned = false;
    private int[] pixels;
    private String authorName = "";
    private String canvasTitle = "";
    private String name = "";
    private int version = 0;

    protected GuiCanvasView(CompoundNBT canvasTag, ITextComponent title) {
        super(title);
        if (canvasTag != null && !canvasTag.isEmpty()) {
            int[] nbtPixels = canvasTag.getIntArray("pixels");
            this.authorName = canvasTag.getString("author");
            this.canvasTitle = canvasTag.getString("title");
            this.name = canvasTag.getString("name");
            this.version = canvasTag.getInt("v");

            this.pixels =  Arrays.copyOfRange(nbtPixels, 0, 256);
        } else {
            this.isSigned = false;
        }
    }

    private int getPixelAt(int x, int y){
        return (this.pixels == null) ? 0xFFF9FFFE : this.pixels[y*16 + x];
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        for(int i=0; i<16; i++){
            for(int j=0; j<16; j++){
                int x = canvasX + i*10;
                int y = canvasY + j*10;
                fill(x, y, x+10, y+10, getPixelAt(i, j));
            }
        }
    }
}