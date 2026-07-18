package xerca.xercapaint.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static xerca.xercapaint.client.PaintClientTests.check;

/**
 * Regression test for tooltip component hiding: the author/generation/empty lines must respect
 * the stack's {@code tooltip_display} component.
 */
public final class CanvasTooltipClientTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(client -> {
            ItemStack painted = new ItemStack(Items.ITEM_CANVAS);
            ItemCanvas itemCanvas = (ItemCanvas) painted.getItem();
            int pixelCount = itemCanvas.getWidth() * itemCanvas.getHeight();
            painted.set(Items.CANVAS_PIXELS, new ArrayList<>(Collections.nCopies(pixelCount, 0)));
            painted.set(Items.CANVAS_AUTHOR, "TestAuthor");
            painted.set(Items.CANVAS_GENERATION, 1);

            check(hasLine(painted, TooltipDisplay.DEFAULT, "canvas.byAuthor"), "Author line must show by default");
            check(hasLine(painted, TooltipDisplay.DEFAULT, "canvas.generation.0"), "Generation line must show by default");

            TooltipDisplay hidden = TooltipDisplay.DEFAULT
                    .withHidden(Items.CANVAS_AUTHOR, true)
                    .withHidden(Items.CANVAS_GENERATION, true);
            check(!hasLine(painted, hidden, "canvas.byAuthor"), "Hidden author component must suppress the author line");
            check(!hasLine(painted, hidden, "canvas.generation.0"), "Hidden generation component must suppress the generation line");

            ItemStack empty = new ItemStack(Items.ITEM_CANVAS);
            check(hasLine(empty, TooltipDisplay.DEFAULT, "canvas.empty"), "Empty line must show by default");
            TooltipDisplay hiddenAuthor = TooltipDisplay.DEFAULT.withHidden(Items.CANVAS_AUTHOR, true);
            check(!hasLine(empty, hiddenAuthor, "canvas.empty"), "Hidden author component must suppress the empty line");
        });
    }

    private static boolean hasLine(ItemStack stack, TooltipDisplay display, String translationKey) {
        List<Component> lines = new ArrayList<>();
        stack.getItem().appendHoverText(stack, Item.TooltipContext.EMPTY, display, lines::add, TooltipFlag.NORMAL);
        return lines.stream().anyMatch(line -> line.getContents() instanceof TranslatableContents contents
                && contents.getKey().equals(translationKey));
    }
}
