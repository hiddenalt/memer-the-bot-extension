package ru.hiddenalt.mtbe.gui.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ButtonWidgetTexturedFix extends ButtonWidget {

    public ButtonWidgetTexturedFix(int x, int y, int width, int height, Text message, PressAction onPress, Identifier texture, int offsetX, int offsetY, int textureWidth, int textureHeight) {
        super(x, y, width, height, message, onPress);
        this.texture = texture;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public ButtonWidgetTexturedFix(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier, Identifier texture, int offsetX, int offsetY, int textureWidth, int textureHeight) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
        this.texture = texture;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    private Identifier texture;
    private final int offsetX;
    private final int offsetY;
    private final int textureWidth;
    private final int textureHeight;

    public void setTexture(Identifier texture){
        this.texture = texture;
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//                super.render(matrices, mouseX, mouseY, delta);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.getTextureManager().bindTexture(this.texture);

        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, (float)this.offsetX, (float) offsetY, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered()) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }
    }
}
