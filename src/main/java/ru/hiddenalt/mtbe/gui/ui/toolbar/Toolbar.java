package ru.hiddenalt.mtbe.gui.ui.toolbar;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.hiddenalt.mtbe.gui.ui.ButtonWidgetTexturedFix;
import ru.hiddenalt.mtbe.gui.ui.tooltip.SimpleTooltip;

import java.util.ArrayList;
import java.util.Arrays;

public class Toolbar {
    protected ArrayList<ToolbarRow> rows = new ArrayList<>();
    protected Screen screen;
    protected MinecraftClient client;

    public enum Direction {
        VERTICAL,
        HORIZONTAL
    }

    protected int itemsOffset = 5;
    protected int rowsOffset = 5;
    protected Direction direction = Direction.VERTICAL;
    protected int x = 0;
    protected int y = 0;
    protected int itemWidth = 16;
    protected int itemHeight = 16;

    public Toolbar(MinecraftClient client, Screen screen) {
        this.screen = screen;
        this.client = client;
    }

    public Toolbar(MinecraftClient client, Screen screen, ArrayList<ToolbarRow> rows) {
        this(client, screen);
        this.rows = rows;
    }

    public ArrayList<ToolbarRow> getRows() {
        return rows;
    }

    public void setRows(ArrayList<ToolbarRow> rows) {
        this.rows = rows;
    }

    public int getItemsOffset() {
        return itemsOffset;
    }

    public void setItemsOffset(int itemsOffset) {
        this.itemsOffset = itemsOffset;
    }

    public int getRowsOffset() {
        return rowsOffset;
    }

    public void setRowsOffset(int rowsOffset) {
        this.rowsOffset = rowsOffset;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void addRow(ToolbarRow ...rows){
        if(rows.length > 0)
            this.rows.addAll(Arrays.asList(rows));
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public ArrayList<ButtonWidgetTexturedFix> make() {
        int _x = 0;
        int _y = 0;

        ArrayList<ButtonWidgetTexturedFix> list = new ArrayList<>();

        for (ToolbarRow row : this.rows) {
            if (direction == Direction.HORIZONTAL) _x = 0; else _y = 0;
            for(ToolbarItem item : row.items){
                list.add(
                    new ButtonWidgetTexturedFix(
                        this.x + (direction == Direction.HORIZONTAL ? itemWidth + itemsOffset : itemWidth + rowsOffset ) * _x,
                        this.y + (direction == Direction.HORIZONTAL ? itemWidth + rowsOffset  : itemWidth + itemsOffset) * _y,
                        this.itemWidth,
                        this.itemHeight,
                        Text.of(""),
                        item.action,
                        new SimpleTooltip(this.client.textRenderer, item.tooltip),
                        item.icon,
                        0,
                        0,
                        this.itemWidth,
                        this.itemHeight
                    )
                );
                if (direction == Direction.HORIZONTAL) _x++; else _y++;
            }
            if (direction == Direction.HORIZONTAL) _y++; else _x++;
        }

        return list;
    }
}
