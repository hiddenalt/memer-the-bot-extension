package ru.hiddenalt.mtbe.settings;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsEntity implements Serializable {
    protected String mount = "";
    protected HashMap<Color, String> colormap = new HashMap();
    protected SchematicAlign schematicAlign;
    protected ArrayList<Integer> bankIds;

    public SettingsEntity() {
        this.schematicAlign = SchematicAlign.VERTICAL;
        this.bankIds = new ArrayList();
    }

    public String getMount() {
        return this.mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

    public HashMap<Color, String> getColormap() {
        return this.colormap;
    }

    public void setColormap(HashMap<Color, String> colormap) {
        this.colormap = colormap;
    }

    public SchematicAlign getSchematicAlign() {
        return this.schematicAlign;
    }

    public void setSchematicAlign(SchematicAlign schematicAlign) {
        this.schematicAlign = schematicAlign;
    }

    public ArrayList<Integer> getBankIds() {
        return this.bankIds;
    }
}
