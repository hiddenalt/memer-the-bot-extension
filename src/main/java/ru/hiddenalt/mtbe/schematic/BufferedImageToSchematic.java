package ru.hiddenalt.mtbe.schematic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.imgscalr.Scalr;
import ru.hiddenalt.mtbe.settings.SchematicAlign;
import ru.hiddenalt.mtbe.settings.SettingsEntity;
import ru.hiddenalt.mtbe.settings.SettingsManager;

public class BufferedImageToSchematic {
    private BufferedImage image;
    private Schematic schematic;
    protected SchematicAlign schematicAlign;

    public BufferedImageToSchematic(BufferedImage image) {
        this.schematicAlign = SchematicAlign.VERTICAL;
        this.image = image;
    }

    public void setCustomImageSize(int width, int height) {
        this.image = Scalr.resize(this.image, width, height, new BufferedImageOp[0]);
    }

    public Schematic generateSchematic() throws Exception {
        Schematic schematic = new Schematic();
        switch(this.schematicAlign) {
            case VERTICAL:
                schematic.setSize(this.image.getWidth(), this.image.getHeight(), 1);
                break;
            case HORIZONTAL:
                schematic.setSize(this.image.getWidth(), 1, this.image.getHeight());
        }

        for(int x = 0; x < this.image.getWidth(); ++x) {
            for(int y = 0; y < this.image.getHeight(); ++y) {
                Color pixel = new Color(this.image.getRGB(x, y), true);
                SchematicBlock block = getSuitableBlockFromColor(pixel);
                switch(this.schematicAlign) {
                    case VERTICAL:
                        block.setPos(x, y, 0);
                        break;
                    case HORIZONTAL:
                        block.setPos(x, 0, y);
                }

                schematic.insertBlock(block);
            }
        }

        this.schematic = schematic;
        return schematic;
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
    }

    public Schematic getSchematic() {
        return this.schematic;
    }

    public static SchematicBlock getSuitableBlockFromColor(Color color) throws Exception {
        SettingsEntity settings = SettingsManager.getSettings();
        HashMap<Color, String> colormap = settings.getColormap();
        if (colormap.size() <= 0) {
            throw new Exception((new TranslatableText("id.error.colormap-is-null")).getString());
        } else {
            String suitableID = "";
            Integer smallestDiff = 2147483647;
            Iterator var5 = colormap.entrySet().iterator();

            while(var5.hasNext()) {
                Entry<Color, String> entry = (Entry)var5.next();
                Color entryColor = (Color)entry.getKey();
                String entryID = (String)entry.getValue();
                Integer currentDiff = Math.toIntExact(Math.round(Math.pow((double)(entryColor.getRed() - color.getRed()), 2.0D) + Math.pow((double)(entryColor.getBlue() - color.getBlue()), 2.0D) + Math.pow((double)(entryColor.getGreen() - color.getGreen()), 2.0D)));
                if (currentDiff < smallestDiff) {
                    smallestDiff = currentDiff;
                    suitableID = entryID;
                }
            }

            return new SchematicBlock(new Identifier(suitableID));
        }
    }
}
