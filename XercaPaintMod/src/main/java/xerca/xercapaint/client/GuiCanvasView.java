package xerca.xercapaint.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.CanvasType;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class GuiCanvasView extends Screen {
    private int canvasX = 140;
    private int canvasY = 40;
    private int canvasWidth;
    private int canvasHeight;
    private int canvasPixelScale;
    private int canvasPixelWidth;
    private int canvasPixelHeight;
    private CanvasType canvasType;

    private boolean isSigned = false;
    private int[] pixels;
    private String authorName = "";
    private String canvasTitle = "";
    private String name = "";
    private int version = 0;

    protected GuiCanvasView(CompoundNBT canvasTag, ITextComponent title, CanvasType canvasType) {
        super(title);

        this.canvasType = canvasType;
        this.canvasPixelScale = canvasType == CanvasType.SMALL ? 10 : 5;
        this.canvasPixelWidth = CanvasType.getWidth(canvasType);
        this.canvasPixelHeight = CanvasType.getHeight(canvasType);
        int canvasPixelArea = canvasPixelHeight*canvasPixelWidth;
        this.canvasWidth = this.canvasPixelWidth * this.canvasPixelScale;
        this.canvasHeight = this.canvasPixelHeight * this.canvasPixelScale;
        if(canvasType.equals(CanvasType.LONG)){
            this.canvasY += 40;
        }
        if(canvasType.equals(CanvasType.TALL)){
            this.canvasX += 40;
        }

        if (canvasTag != null && !canvasTag.isEmpty()) {
            int[] nbtPixels = canvasTag.getIntArray("pixels");
            this.authorName = canvasTag.getString("author");
            this.canvasTitle = canvasTag.getString("title");
            this.name = canvasTag.getString("name");
            this.version = canvasTag.getInt("v");

            this.pixels =  Arrays.copyOfRange(nbtPixels, 0, canvasPixelArea);
        } else {
            this.isSigned = false;
        }
    }

    private int getPixelAt(int x, int y){
        return (this.pixels == null) ? 0xFFF9FFFE : this.pixels[y*canvasPixelWidth + x];
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float f) {
        for(int i=0; i<canvasPixelHeight; i++){
            for(int j=0; j<canvasPixelWidth; j++){
                int x = canvasX + j*canvasPixelScale;
                int y = canvasY + i*canvasPixelScale;
                fill(x, y, x+canvasPixelScale, y+canvasPixelScale, getPixelAt(j, i));
            }
        }
    }
}