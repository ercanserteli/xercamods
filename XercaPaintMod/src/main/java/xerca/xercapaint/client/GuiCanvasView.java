package xerca.xercapaint.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xerca.xercapaint.common.CanvasType;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class GuiCanvasView extends Screen {
    private int canvasX; // = 140;
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
    private int generation = 0;

    protected GuiCanvasView(CompoundNBT canvasTag, ITextComponent title, CanvasType canvasType) {
        super(title);

        this.canvasType = canvasType;
        this.canvasPixelScale = canvasType == CanvasType.SMALL ? 10 : 5;
        this.canvasPixelWidth = CanvasType.getWidth(canvasType);
        this.canvasPixelHeight = CanvasType.getHeight(canvasType);
        int canvasPixelArea = canvasPixelHeight*canvasPixelWidth;
        this.canvasWidth = this.canvasPixelWidth * this.canvasPixelScale;
        this.canvasHeight = this.canvasPixelHeight * this.canvasPixelScale;

        if (canvasTag != null && !canvasTag.isEmpty()) {
            int[] nbtPixels = canvasTag.getIntArray("pixels");
            this.authorName = canvasTag.getString("author");
            this.canvasTitle = canvasTag.getString("title");
            this.name = canvasTag.getString("name");
            this.version = canvasTag.getInt("v");
            this.generation = canvasTag.getInt("generation");

            this.pixels =  Arrays.copyOfRange(nbtPixels, 0, canvasPixelArea);
        } else {
            this.isSigned = false;
        }
    }

    @Override
    public void init() {
        canvasX = (this.width - canvasWidth) / 2;
        if(canvasType.equals(CanvasType.LONG)){
            canvasY += 40;
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
        
        if(generation > 0 && !canvasTitle.isEmpty()){
            String title = canvasTitle + " " + I18n.format("canvas.byAuthor", authorName);
            String gen = "(" + I18n.format("canvas.generation." + (generation - 1)) + ")";

            int titleWidth = this.font.getStringWidth(title);
            int genWidth = this.font.getStringWidth(gen);

            float titleX = (canvasX + (canvasWidth - titleWidth) / 2.0f);
            float genX = (canvasX + (canvasWidth - genWidth) / 2.0f);
            float minX = Math.min(genX, titleX);
            float maxX = Math.max(genX + genWidth, titleX + titleWidth);

            fill((int)(minX - 10), canvasY - 30, (int)(maxX + 10), canvasY - 4, 0xFFEEEEEE);

            this.font.drawString(title, titleX, canvasY - 25, 0xFF111111);
            this.font.drawString(gen, genX, canvasY - 14, 0xFF444444);
        }
    }
}