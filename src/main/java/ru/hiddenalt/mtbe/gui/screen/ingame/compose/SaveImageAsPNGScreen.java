package ru.hiddenalt.mtbe.gui.screen.ingame.compose;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TranslatableText;
import ru.hiddenalt.mtbe.gui.screen.ErrorScreen;
import ru.hiddenalt.mtbe.gui.screen.TextInputDialogScreen;
import ru.hiddenalt.mtbe.settings.SettingsManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveImageAsPNGScreen extends TextInputDialogScreen {

    private Screen parent;
    private BufferedImage image;

    public SaveImageAsPNGScreen(Screen parent, BufferedImage image) {
        super(parent, "composeSchematic.saveAsPng.title",
                new TranslatableText("composeSchematic.saveAsPng.save"));

        this.parent = parent;
        this.image = image;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        this.setTextFieldText(new TranslatableText("imported image "+dtf.format(now)+".png"));


    }

    @Override
    protected void onProceed(ButtonWidget buttonWidget) {

        TextFieldWidget input = getInput();
        try{
            String filename = input.getText();
            File file = new File(SettingsManager.getImportedPNGFolder() + filename);
            ImageIO.write(this.image, "png", file);
        } catch (IOException e){
            e.printStackTrace();
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, "Unable to save:\n"+e.getMessage()));
        }

        super.onProceed(buttonWidget);
    }
}
