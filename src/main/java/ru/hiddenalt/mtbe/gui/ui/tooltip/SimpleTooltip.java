package ru.hiddenalt.mtbe.gui.ui.tooltip;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SimpleTooltip implements ButtonWidget.TooltipSupplier {

    private final Text text;
    private TextRenderer textRenderer;

    public SimpleTooltip(TextRenderer textRenderer, Text text) {
        this.text = text;
        this.textRenderer = textRenderer;
    }

    @Override
    public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
        int x_offset = 6;
        int y_offset = 6;


        MinecraftClient client = MinecraftClient.getInstance();
        String[] lines = text.getString().split("\n");

        int maxWidth = 0;
        for(int i = 0; i < lines.length; i++)
            maxWidth = Math.max(client.textRenderer.getWidth(lines[i]), maxWidth);

        maxWidth += 10;

        int x = ((mouseX + maxWidth <= client.currentScreen.width) ? mouseX : client.currentScreen.width - maxWidth) + x_offset;
        int y = mouseY + y_offset;

        for(int i = 0; i < lines.length; i++)
            DrawableHelper.drawTextWithShadow(matrices, textRenderer, Text.of(lines[i]), x, y + i * 10,16777215);
    }
}
