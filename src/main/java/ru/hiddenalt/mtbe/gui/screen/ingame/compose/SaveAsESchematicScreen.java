package ru.hiddenalt.mtbe.gui.screen.ingame.compose;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import ru.hiddenalt.mtbe.gui.screen.ErrorScreen;
import ru.hiddenalt.mtbe.gui.screen.TextInputDialogScreen;
import ru.hiddenalt.mtbe.schematic.Schematic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveAsESchematicScreen extends TextInputDialogScreen {

    private Screen parent;
    private Schematic schematic;

    public SaveAsESchematicScreen(Screen parent, Schematic schematic) {
        super(parent, "composeSchematic.saveAsESchematic.title",
                new TranslatableText("composeSchematic.saveAsPng.save"));

        this.parent = parent;
        this.schematic = schematic;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        this.setTextFieldText(new TranslatableText("export "  + dtf.format(now) + ".eschematic"));
    }

    @Override
    protected void onProceed(ButtonWidget buttonWidget) {
        try {
            this.schematic.saveAsESchematic(getInput().getText());
        } catch (IOException e){
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, "Unable to save as ESchematic:\n"+e.getMessage()));
        }

        super.onProceed(buttonWidget);
    }
}
