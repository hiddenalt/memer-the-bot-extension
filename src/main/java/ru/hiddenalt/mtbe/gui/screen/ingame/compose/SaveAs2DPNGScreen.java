package ru.hiddenalt.mtbe.gui.screen.ingame.compose;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TranslatableText;
import ru.hiddenalt.mtbe.gui.screen.ErrorScreen;
import ru.hiddenalt.mtbe.gui.screen.TextInputDialogScreen;
import ru.hiddenalt.mtbe.schematic.Schematic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveAs2DPNGScreen extends TextInputDialogScreen {

    private Screen parent;
    private Schematic schematic;

    public SaveAs2DPNGScreen(Screen parent, Schematic schematic) {
        super(parent, "composeSchematic.saveAsPng.title",
                new TranslatableText("composeSchematic.saveAsPng.save"));

        this.parent = parent;
        this.schematic = schematic;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        this.setTextFieldText(new TranslatableText("export schematic2d "+dtf.format(now)+".png"));


    }

    @Override
    protected void onProceed(ButtonWidget buttonWidget) {

        TextFieldWidget input = getInput();
        try{
            this.schematic.saveAs2DPNG(input.getText());
        } catch (IOException e){
            e.printStackTrace();
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, "Unable to save:\n"+e.getMessage()));
        }

        super.onProceed(buttonWidget);
    }
}
