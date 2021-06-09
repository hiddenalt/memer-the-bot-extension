package ru.hiddenalt.mtbe.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.imgscalr.Scalr;
import ru.hiddenalt.mtbe.gui.screen.options.OptionsScreen;
import ru.hiddenalt.mtbe.gui.ui.ButtonWidgetTexturedFix;
import ru.hiddenalt.mtbe.gui.ui.tooltip.SimpleTooltip;

import java.awt.image.BufferedImageOp;
import java.nio.file.Path;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget customImageURLField;
    private String imageURL = "";

    private int topButtonsOffset = 40;
    private int buttonOffset = 20 * 2;
    private int innerOffset = 25;
    private ButtonWidgetTexturedFix buildImage;
    private ComposeSchematic lastScreen;

    public ActionsScreen(Screen parent) {
        super(new TranslatableText("actions.title"));
        this.parent = parent;
    }

    protected void init() {


        // Build section
        this.addButton(new ButtonWidget(this.width / 2 - 100, topButtonsOffset + innerOffset * 0, 200, 20, new TranslatableText("actions.build.meme.demotivational.poster"), (buttonWidget) -> {
//            assert this.client != null;
//            this.client.openScreen(this.parent);
        }));

        this.addButton(new ButtonWidget(this.width / 2 - 100, topButtonsOffset + innerOffset * 1, 200, 20, new TranslatableText("actions.build.meme.when"), (buttonWidget) -> {
//            assert this.client != null;
//            this.client.openScreen(this.parent);
        }));

        this.addButton(new ButtonWidget(this.width / 2 - 100, topButtonsOffset + innerOffset * 2, 200, 20, new TranslatableText("actions.build.meme.comics"), (buttonWidget) -> {
//            assert this.client != null;
//            this.client.openScreen(this.parent);
        }));

        this.customImageURLField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                topButtonsOffset + innerOffset * 4 + 12,
                200 - 25,
                20,
                Text.of("")
        );
        this.customImageURLField.setSelected(false);
        this.customImageURLField.setMaxLength(99999);
        this.customImageURLField.setText(imageURL);
        this.customImageURLField.setChangedListener(this::onAddressFieldChange);
        this.children.add(this.customImageURLField);



        // this.width / 2 + 100 - 20
        // topButtonsOffset + innerOffset * 5 + 12
        buildImage = new ButtonWidgetTexturedFix(this.width / 2 + 100 - 20, topButtonsOffset + innerOffset * 4 + 12, 20, 20, Text.of(""), (buttonWidget) -> {
            assert this.client != null;
            this.lastScreen = new ComposeSchematic(this.client,this, this.customImageURLField.getText());
            this.client.openScreen(this.lastScreen);
        },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.build.image")),
                new Identifier("mtbe:textures/open_file.png"), 0, 0, 20, 20);
        this.addButton(buildImage);


        if(this.lastScreen != null) {
            this.addButton(new ButtonWidgetTexturedFix(this.width / 2 - 100 - 25, topButtonsOffset + innerOffset * 4 + 12, 20, 20, Text.of(""), (buttonWidget) -> {
                if(this.lastScreen == null) return;
                assert this.client != null;
                this.client.openScreen(this.lastScreen);
            },
                new SimpleTooltip(textRenderer, new TranslatableText("actions.build.reopenLastScreen")),
                new Identifier("mtbe:textures/menu/reopen_last_screen.png"), 0, 0, 20, 20));
        }



        // "Done" button
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - buttonOffset, 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(this.parent);
        }));

        // Options button
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - buttonOffset - innerOffset, 200, 20, new TranslatableText("actions.open.options"), (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new OptionsScreen(this));
        }));
    }

    public void filesDragged(List<Path> paths) {
        if(paths.size() > 0){
            this.customImageURLField.setText("file:///"+paths.get(0).toString());
            buildImage.onPress();
        }
    }


    private void onAddressFieldChange(String s) {
        imageURL = s;
    }

    public void tick() {
        this.customImageURLField.tick();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String imageURL = this.customImageURLField.getText();

        this.init(client, width, height);

        this.customImageURLField.setText(imageURL);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);

        drawStringWithShadow(matrices, this.textRenderer, I18n.translate("actions.section.build", new Object[0]), this.width / 2 - 100, topButtonsOffset - 10, 10526880);

        drawCenteredString(matrices, this.textRenderer, I18n.translate("actions.or", new Object[0]), this.width / 2, topButtonsOffset + innerOffset * 4 - innerOffset / 2, 16777215);
        drawStringWithShadow(matrices, this.textRenderer, I18n.translate("actions.image.url", new Object[0]), this.width / 2 - 100, topButtonsOffset + innerOffset * 4, 10526880);


        this.customImageURLField.render(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
    }

}
