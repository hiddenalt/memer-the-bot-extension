package ru.hiddenalt.mtbe.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.awt.*;

public class TextInputDialogScreen extends Screen {
    private Screen parent;
    private String contents;
    private TextFieldWidget input;
    private ButtonWidget closeButton;

    private TranslatableText doneButtonText = (TranslatableText) ScreenTexts.DONE;
    private TranslatableText textFieldText = new TranslatableText("");

    public TextInputDialogScreen(Screen parent, String title) {
        super(new TranslatableText(title));
        this.contents = title;
        this.parent = parent;
    }


    public TextInputDialogScreen(Screen parent, String title, TranslatableText doneButtonText) {
        this(parent, title);
        this.doneButtonText = doneButtonText;
    }

    public TextInputDialogScreen(Screen parent, String title, TranslatableText doneButtonText, TranslatableText textFieldText) {
        this(parent, title, doneButtonText);
        this.textFieldText = textFieldText;
    }

    public TextFieldWidget getInput() {
        return input;
    }

    public void setInput(TextFieldWidget input) {
        this.input = input;
    }

    public TranslatableText getDoneButtonText() {
        return doneButtonText;
    }

    public void setDoneButtonText(TranslatableText doneButtonText) {
        this.doneButtonText = doneButtonText;
    }

    public TranslatableText getTextFieldText() {
        return textFieldText;
    }

    public void setTextFieldText(TranslatableText textFieldText) {
        this.textFieldText = textFieldText;
    }

    protected void init(){
        int buttonOffset = 20 * 2;

        this.closeButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width - 25, 5, 20, 20, Text.of("X"), (button) -> {
            assert this.client != null;
            this.client.openScreen(this.parent);
        }));
        this.addButton(this.closeButton);

        this.input = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 20 / 2, 200, 20, this.textFieldText);
        this.input.setSelected(false);
        this.input.setMaxLength(999999);
        this.input.setText(this.textFieldText.getString());
        this.addButton(this.input);

        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 2 - 20 / 2 + 20 + 5, 200, 20, this.doneButtonText, this::onProceed));
    }

    public void tick() {
        this.input.tick();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String inputText = this.input.getText();
        this.init(client, width, height);
        this.input.setText(inputText);
    }

    protected void onProceed(ButtonWidget buttonWidget){
        assert this.client != null;
        this.client.openScreen(this.parent);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        // Title
        String[] array = this.title.getString().split("\n");
        int lineHeight = 10;
        int height = array.length * lineHeight;

        int padding = 10 + 5;
        int textHeight = 5;


        for (int i = 0; i < array.length; i++) {
            drawCenteredString(
                    matrices,
                    this.textRenderer,
                    array[i],
                    this.width / 2,
                    this.height/2 - padding - textHeight - height/2 + i * lineHeight,
                    Color.yellow.getRGB()
            );
        }

        // Input
        this.input.render(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
    }

}
