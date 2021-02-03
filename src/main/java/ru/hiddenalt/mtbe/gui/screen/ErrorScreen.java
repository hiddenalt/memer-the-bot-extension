package ru.hiddenalt.mtbe.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.awt.*;

public class ErrorScreen extends Screen {
    private Screen parent;
    private String contents;

    public ErrorScreen(Screen parent, String contents) {
        super(new TranslatableText("error.title"));
        this.contents = contents;
        this.parent = parent;
    }

    protected void init(){
        int buttonOffset = 20 * 2;
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - buttonOffset, 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(this.parent);
        }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, Color.red.getRGB());

        String[] array = contents.split("\n");
        int lineHeight = 10;
        int height = array.length * lineHeight;

        for (int i = 0; i < array.length; i++) {
            drawCenteredString(matrices, this.textRenderer, array[i], this.width / 2, this.height / 2 - height / 2 + i * lineHeight, 16777215);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

}
