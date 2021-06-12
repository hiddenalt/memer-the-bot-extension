package ru.hiddenalt.mtbe.gui.ui.toolbar;


import java.util.ArrayList;
import java.util.Arrays;

public class ToolbarRow {

    protected ArrayList<ToolbarItem> items = new ArrayList<>();

    public ToolbarRow() {}

    public ToolbarRow(ArrayList<ToolbarItem> items) {
        this.items = items;
    }

    public ArrayList<ToolbarItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ToolbarItem> items) {
        this.items = items;
    }

    public void add(ToolbarItem ...items){
        if(items.length > 0)
            this.items.addAll(Arrays.asList(items));
    }

}
