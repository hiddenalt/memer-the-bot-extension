package ru.hiddenalt.mtbe.gui.ui.toolbar;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ToolbarItem {

    protected Text tooltip = Text.of("");
    protected Identifier icon = new Identifier("mtbe:textures/icon.png");
    protected ButtonWidget.PressAction action;

    public ToolbarItem() { }
    public ToolbarItem(Text tooltip, Identifier icon, ButtonWidget.PressAction action){
        this.tooltip = tooltip;
        this.icon = icon;
        this.action = action;
    }

    public Text getTooltip() {
        return tooltip;
    }

    public void setTooltip(Text tooltip) {
        this.tooltip = tooltip;
    }

    public Identifier getIcon() {
        return icon;
    }

    public void setIcon(Identifier icon) {
        this.icon = icon;
    }

    public ButtonWidget.PressAction getAction() {
        return action;
    }

    public void setAction(ButtonWidget.PressAction action) {
        this.action = action;
    }
}
