package ru.hiddenalt.mtbe.gui.screen.options;

import java.awt.Color;
import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget.Entry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import ru.hiddenalt.mtbe.gui.ui.ColorPickerWidget;
import ru.hiddenalt.mtbe.gui.ui.NumberFieldWidget;
import ru.hiddenalt.mtbe.schematic.SchematicBlock;
import ru.hiddenalt.mtbe.settings.SettingsEntity;
import ru.hiddenalt.mtbe.settings.SettingsManager;

@Environment(EnvType.CLIENT)
public class EditColorScreen extends Screen {
    private final Screen parent;
    private ButtonWidget closeButton;
    private ButtonWidget doneButton;
    private ButtonWidget deleteEntryButton;
    private Color color;
    private SchematicBlock block;
    private EditColorScreen.BlockSelectionWidget blockSelectionWidget;
    private int colorPickerX;
    private int colorPickerY;
    private ColorPickerWidget colorPicker;

    public EditColorScreen(Screen parent) {
        this(parent, new Color(200, 200, 200), new SchematicBlock(new Identifier("minecraft:stone")));
    }

    public EditColorScreen(Screen parent, Color color, SchematicBlock block) {
        super(new TranslatableText("menu.color.definition.table.edit.color.title"));
        this.colorPickerX = 0;
        this.colorPickerY = 0;
        this.color = color;
        this.block = block;
        this.parent = parent;
    }

    protected void init() {
        this.closeButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width - 25, 5, 20, 20, Text.of("X"), (button) -> {
            assert this.client != null;

            this.client.openScreen(this.parent);
        }));
        this.doneButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, new TranslatableText("menu.color.definition.table.edit.color.add"), (button) -> {
            EditColorScreen.BlockSelectionWidget.BlockSelectionEntry selectedEntry = (EditColorScreen.BlockSelectionWidget.BlockSelectionEntry)this.blockSelectionWidget.getSelected();
            if (selectedEntry != null) {
                SettingsEntity settings = SettingsManager.getSettings();
                HashMap<Color, String> colormap = settings.getColormap();
                colormap.put(this.colorPicker.getColor(), selectedEntry.block.getIdentifier().getPath());

                assert this.client != null;

                this.client.openScreen(this.parent);
            }
        }));
        this.deleteEntryButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 38, 150, 20, new TranslatableText("menu.color.definition.table.edit.color.delete"), (button) -> {
            SettingsManager.getSettings().getColormap().remove(this.color);
            this.client.openScreen(this.parent);
        }));
        this.deleteEntryButton.active = false;
        this.colorPickerX = this.width / 2 - 150;
        this.colorPickerY = 35;
        this.colorPicker = new ColorPickerWidget(this.color, this.client, this, this.textRenderer, this.colorPickerX, this.colorPickerY, 300, 85);
        this.addChild(this.colorPicker);
        this.addChild(this.colorPicker.getResetButton());
        this.addChild(this.colorPicker.getBlueField());
        this.addChild(this.colorPicker.getGreenField());
        this.addChild(this.colorPicker.getRedField());
        this.blockSelectionWidget = new EditColorScreen.BlockSelectionWidget(this.client, this);
        this.children.add(this.blockSelectionWidget);
        super.init();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.blockSelectionWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 16777215);
        this.colorPicker.renderButton(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void tick() {
        TextFieldWidget blueField = this.colorPicker.getBlueField();
        TextFieldWidget greenField = this.colorPicker.getGreenField();
        TextFieldWidget redField = this.colorPicker.getRedField();
        blueField.tick();
        greenField.tick();
        redField.tick();
    }

    public void resize(MinecraftClient client, int width, int height) {
        NumberFieldWidget blueField = this.colorPicker.getBlueField();
        NumberFieldWidget greenField = this.colorPicker.getGreenField();
        NumberFieldWidget redField = this.colorPicker.getRedField();
        Color color = this.colorPicker.getColor();
        String blueFieldText = blueField.getText();
        String greenFieldText = greenField.getText();
        String redFieldText = redField.getText();
        this.init(client, width, height);
        this.colorPicker.getBlueField().setText(blueFieldText);
        this.colorPicker.getGreenField().setText(greenFieldText);
        this.colorPicker.getRedField().setText(redFieldText);
        this.colorPicker.setColor(color);
    }

    @Environment(EnvType.CLIENT)
    public class BlockSelectionWidget extends AlwaysSelectedEntryListWidget<EditColorScreen.BlockSelectionWidget.BlockSelectionEntry> {
        protected ColorDefinitionTableScreen screen;
        public static final int rowHeight = 28;
        protected MinecraftClient client;

        public BlockSelectionWidget(MinecraftClient client, EditColorScreen screen) {
            super(client, screen.width, screen.height, 128, screen.height - 65 + 4, 32);
            this.client = client;
            this.addEntry(new EditColorScreen.BlockSelectionWidget.BlockSelectionEntry(EditColorScreen.this, new SchematicBlock(new Identifier("minecraft:air"))));
            Registry.BLOCK.getIds().forEach((identifier) -> {
                SchematicBlock block = new SchematicBlock(identifier);
                if (!block.getBlockInstance().asItem().getTranslationKey().equals("block.minecraft.air")) {
                    EditColorScreen.BlockSelectionWidget.BlockSelectionEntry entry = new EditColorScreen.BlockSelectionWidget.BlockSelectionEntry(EditColorScreen.this, block);
                    this.addEntry(entry);
                    if (block.getIdentifier().equals(EditColorScreen.this.block.getIdentifier())) {
                        this.setSelected(entry);
                    }

                }
            });
            if (this.getSelected() != null) {
                this.centerScrollOn((EditColorScreen.BlockSelectionWidget.BlockSelectionEntry)this.getSelected());
            }

        }

        protected int getScrollbarPositionX() {
            return super.getScrollbarPositionX() + 20;
        }

        public int getRowWidth() {
            return super.getRowWidth() + 10;
        }

        public void setSelected(@Nullable EditColorScreen.BlockSelectionWidget.BlockSelectionEntry blockEntry) {
            super.setSelected(blockEntry);
        }

        protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
            super.renderList(matrices, x, y, mouseX, mouseY, delta);
            EditColorScreen screen = EditColorScreen.this;
            EditColorScreen.BlockSelectionWidget.BlockSelectionEntry selectedBlock = (EditColorScreen.BlockSelectionWidget.BlockSelectionEntry)EditColorScreen.this.blockSelectionWidget.getSelected();
            if (selectedBlock == null) {
                screen.doneButton.active = false;
            }

            if (SettingsManager.getSettings().getColormap().get(screen.colorPicker.getColor()) != null) {
                screen.doneButton.setMessage(new TranslatableText("menu.color.definition.table.edit.color.edit"));
                screen.deleteEntryButton.active = true;
            } else {
                screen.doneButton.setMessage(new TranslatableText("menu.color.definition.table.edit.color.add"));
                screen.deleteEntryButton.active = false;
            }

        }

        protected void renderBackground(MatrixStack matrices) {
            super.renderBackground(matrices);
        }

        protected boolean isFocused() {
            return false;
        }

        @Environment(EnvType.CLIENT)
        public class BlockSelectionEntry extends Entry<EditColorScreen.BlockSelectionWidget.BlockSelectionEntry> {
            protected Screen screen;
            protected SchematicBlock block;

            public BlockSelectionEntry(Screen screen, SchematicBlock block) {
                this.screen = screen;
                this.setBlock(block);
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                Block blockInstance = this.block.getBlockInstance();
                if (blockInstance != null && BlockSelectionWidget.this.client != null) {
                    String string = I18n.translate(blockInstance.asItem().getTranslationKey(), new Object[0]);
                    int maxSize = 30;
                    if (string.length() > maxSize) {
                        string = string.substring(0, maxSize - 1) + "...";
                    }

                    int fontSize = 10;
                    float half = 14.0F;
                    float posX = (float)(x + 26);
                    float posY = (float)y + half - (float)(fontSize / 2);
                    BlockSelectionWidget.this.client.getItemRenderer().renderInGui(blockInstance.asItem().getDefaultStack(), x + 3, (int)((float)y + half - 8.0F));
                    EditColorScreen.this.textRenderer.drawWithShadow(matrices, string, posX, posY, 16777215, true);
                }
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    this.onPressed();
                    return true;
                } else {
                    return false;
                }
            }

            private void onPressed() {
                BlockSelectionWidget.this.setSelected(this);
            }

            public SchematicBlock getBlock() {
                return this.block;
            }

            public void setBlock(SchematicBlock block) {
                this.block = block;
            }
        }
    }
}
