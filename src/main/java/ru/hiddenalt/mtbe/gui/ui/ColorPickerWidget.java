package ru.hiddenalt.mtbe.gui.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ColorPickerWidget extends AbstractButtonWidget implements Drawable, Element {
    private final int colorPickerWidth = 96;
    private int paletteWidth = 20;
    private int paletteHeight = 20;
    private Identifier palette = new Identifier("mtbe:textures/menu/color_scheme.png");
    private final MinecraftClient client;
    private ButtonWidget resetButton;
    private NumberFieldWidget redField;
    private NumberFieldWidget greenField;
    private NumberFieldWidget blueField;
    private Screen screen;
    private Color color;
    private Color initialColor;
    public static final int TYPE_RED = 1;
    public static final int TYPE_GREEN = 2;
    public static final int TYPE_BLUE = 3;

    public ColorPickerWidget(Color color, MinecraftClient client, Screen screen, TextRenderer textRenderer, int x, int y, int width, int height) {
        super(x, y, width, height, Text.of(""));
        this.client = client;
        int fieldsWidth = 40;
        int offsetBorder = 10;
        this.redField = new NumberFieldWidget(textRenderer, x + this.width - (offsetBorder + fieldsWidth), y + 5 + 0, fieldsWidth, 20) {
            public boolean charTyped(char chr, int keyCode) {
                super.charTyped(chr, keyCode);
                ColorPickerWidget.this.updateColor(this, 1);
                return true;
            }

            public void write(String string) {
                super.write(string);
                ColorPickerWidget.this.updateColor(this, 1);
            }

            public void eraseCharacters(int characterOffset) {
                super.eraseCharacters(characterOffset);
                ColorPickerWidget.this.updateColor(this, 1);
            }
        };
        this.greenField = new NumberFieldWidget(textRenderer, x + this.width - (offsetBorder + fieldsWidth), y + 5 + 23, fieldsWidth, 20) {
            public boolean charTyped(char chr, int keyCode) {
                super.charTyped(chr, keyCode);
                ColorPickerWidget.this.updateColor(this, 2);
                return true;
            }

            public void write(String string) {
                super.write(string);
                ColorPickerWidget.this.updateColor(this, 2);
            }

            public void eraseCharacters(int characterOffset) {
                super.eraseCharacters(characterOffset);
                ColorPickerWidget.this.updateColor(this, 2);
            }
        };
        this.blueField = new NumberFieldWidget(textRenderer, x + this.width - (offsetBorder + fieldsWidth), y + 5 + 46, fieldsWidth, 20) {
            public boolean charTyped(char chr, int keyCode) {
                super.charTyped(chr, keyCode);
                ColorPickerWidget.this.updateColor(this, 3);
                return true;
            }

            public void write(String string) {
                super.write(string);
                ColorPickerWidget.this.updateColor(this, 3);
            }

            public void eraseCharacters(int characterOffset) {
                super.eraseCharacters(characterOffset);
                ColorPickerWidget.this.updateColor(this, 3);
            }
        };
        int buttonSize = 40;
        this.resetButton = new ButtonWidget(this.x + this.width / 2 - buttonSize / 2, this.height / 2 - 10, buttonSize, 20, new TranslatableText("menu.color.definition.table.edit.color.reset"), (button) -> {
            this.setColor(this.initialColor);
        });
        this.screen = screen;
        this.color = color;
        this.initialColor = this.color;
        this.setColor(color);
        this.paletteWidth = this.width / 3;
        this.paletteHeight = this.height;
    }

    private void updateColor(NumberFieldWidget field, int type) {
        int red = this.color.getRed();
        int green = this.color.getGreen();
        int blue = this.color.getBlue();
        switch(type) {
            case 1:
                red = Integer.parseInt(this.redField.getText());
                break;
            case 2:
                green = Integer.parseInt(this.greenField.getText());
                break;
            case 3:
                blue = Integer.parseInt(this.blueField.getText());
        }

        this.color = new Color(red, green, blue);
    }

    public NumberFieldWidget getRedField() {
        return this.redField;
    }

    public NumberFieldWidget getGreenField() {
        return this.greenField;
    }

    public NumberFieldWidget getBlueField() {
        return this.blueField;
    }

    public ButtonWidget getResetButton() {
        return this.resetButton;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
        this.redField.setText("" + color.getRed());
        this.greenField.setText("" + color.getGreen());
        this.blueField.setText("" + color.getBlue());
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int colorSize = (int)Math.floor((double)this.height / 1.4D);
        int borderSize = 1;
        fill(matrices, this.x + this.width / 2 - colorSize / 2 - borderSize, this.y + this.height / 2 - colorSize / 2 - borderSize, this.x + this.width / 2 + colorSize / 2 + borderSize, this.y + this.height / 2 + colorSize / 2 + borderSize, this.color.getRGB() > (new Color(63, 63, 63)).getRGB() ? this.color.darker().darker().getRGB() : this.color.brighter().brighter().getRGB());
        fill(matrices, this.x + this.width / 2 - colorSize / 2, this.y + this.height / 2 - colorSize / 2, this.x + this.width / 2 + colorSize / 2, this.y + this.height / 2 + colorSize / 2, this.color.getRGB());

        assert this.client != null;

        this.client.getTextureManager().bindTexture(this.palette);
        drawTexture(matrices, this.x, this.y, this.paletteWidth, this.paletteHeight, 0.0F, 0.0F, 16, 128, 16, 128);
        this.resetButton.renderButton(matrices, mouseX, mouseY, delta);
        this.redField.renderButton(matrices, mouseX, mouseY, delta);
        this.greenField.renderButton(matrices, mouseX, mouseY, delta);
        this.blueField.renderButton(matrices, mouseX, mouseY, delta);
    }

    protected boolean clicked(double mouseX, double mouseY) {
        if (mouseX >= (double)this.x && mouseX <= (double)(this.x + this.paletteWidth) && mouseY >= (double)this.y && mouseY <= (double)(this.y + this.paletteHeight)) {
            try {
                InputStream stream = this.client.getResourceManager().getResource(this.palette).getInputStream();
                BufferedImage imBuff = ImageIO.read(stream);
                int x = (int)Math.round((mouseX - (double)this.x) / (double)this.paletteWidth * (double)imBuff.getWidth());
                int y = (int)Math.round((mouseY - (double)this.y) / (double)this.paletteHeight * (double)imBuff.getHeight());
                if (x > 0 && y > 0 && x < imBuff.getWidth() && y < imBuff.getHeight()) {
                    int rgb = imBuff.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    if (color.getAlpha() >= 255) {
                        this.setColor(color);
                    }
                }
            } catch (IOException var11) {
                System.out.println("Click event failed! " + var11.getLocalizedMessage());
            }
        }

        return false;
    }
}
