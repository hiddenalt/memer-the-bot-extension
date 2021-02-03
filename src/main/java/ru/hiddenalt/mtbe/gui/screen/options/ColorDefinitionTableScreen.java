package ru.hiddenalt.mtbe.gui.screen.options;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.Nullable;
import ru.hiddenalt.mtbe.schematic.SchematicBlock;
import ru.hiddenalt.mtbe.settings.SettingsEntity;
import ru.hiddenalt.mtbe.settings.SettingsManager;

@Environment(EnvType.CLIENT)
public class ColorDefinitionTableScreen extends Screen {
    private final Screen parent;
    private static final Text WARNING_TEXT;
    private ColorDefinitionTableScreen.ColorDefinitionTableWidget blocksTableSelectionWidget;
    private ButtonWidget closeButton;
    private ButtonWidget clearButton;
    private ButtonWidget newEntryButton;
    private ButtonWidget doneButton;
    private ButtonWidget editBlockButton;
    private SettingsEntity backupSettings;

    public ColorDefinitionTableScreen(Screen parent) {
        super(new TranslatableText("menu.color.definition.table.title"));
        this.parent = parent;
        this.backupSettings = (SettingsEntity)SerializationUtils.clone(SettingsManager.getSettings());
    }

    protected void init() {
        this.blocksTableSelectionWidget = new ColorDefinitionTableScreen.ColorDefinitionTableWidget(this.client, this);
        this.children.add(this.blocksTableSelectionWidget);
        this.closeButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width - 25, 5, 20, 20, Text.of("X"), (button) -> {
            SettingsManager.setSettings(this.backupSettings);

            assert this.client != null;

            this.client.openScreen(this.parent);
        }));
        this.clearButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38 - 25, 150, 20, (new LiteralText("")).append(new TranslatableText("menu.color.definition.table.clear")).formatted(Formatting.RED), (button) -> {
            SettingsManager.getSettings().getColormap().clear();
            this.init();
        }));
        this.newEntryButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 38 - 25, 150, 20, new TranslatableText("menu.color.definition.table.create"), (button) -> {
            assert this.client != null;

            this.client.openScreen(new EditColorScreen(this, new Color(100, 100, 100), new SchematicBlock(1)));
        }));
        this.doneButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, new TranslatableText("menu.color.definition.table.save"), (button) -> {
            SettingsManager.save();

            assert this.client != null;

            this.client.openScreen(this.parent);
        }));
        this.editBlockButton = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 38, 150, 20, new TranslatableText("menu.color.definition.table.edit"), (button) -> {
            ColorDefinitionTableScreen.ColorDefinitionTableWidget.ColorDefinitionEntry selected = (ColorDefinitionTableScreen.ColorDefinitionTableWidget.ColorDefinitionEntry)this.blocksTableSelectionWidget.getSelected();
            if (this.client != null && selected != null) {
                this.client.openScreen(new EditColorScreen(this, selected.color, selected.block));
            }
        }));
        this.editBlockButton.active = false;
        super.init();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.blocksTableSelectionWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 16, 16777215);
        drawCenteredText(matrices, this.textRenderer, WARNING_TEXT, this.width / 2, this.height - 56 - 25, 8421504);
        super.render(matrices, mouseX, mouseY, delta);
    }

    static {
        WARNING_TEXT = (new LiteralText("")).append(new TranslatableText("menu.color.definition.table.hint")).formatted(Formatting.GOLD);
    }

    @Environment(EnvType.CLIENT)
    public class ColorDefinitionTableWidget extends AlwaysSelectedEntryListWidget<ColorDefinitionTableScreen.ColorDefinitionTableWidget.ColorDefinitionEntry> {
        protected ColorDefinitionTableScreen screen;
        public static final int rowHeight = 28;

        public ColorDefinitionTableWidget(MinecraftClient client, ColorDefinitionTableScreen screen) {
            super(client, screen.width, screen.height, 28, screen.height - 65 + 4 - 25, 32);
            SettingsEntity settings = SettingsManager.getSettings();
            HashMap<Color, String> colormap = settings.getColormap();
            Iterator var6 = colormap.entrySet().iterator();

            for(Map.Entry<Color, String> c : colormap.entrySet()){
                ColorDefinitionTableScreen.ColorDefinitionTableWidget.ColorDefinitionEntry blockEntry =
                        new ColorDefinitionTableScreen.ColorDefinitionTableWidget.ColorDefinitionEntry(screen, (Color)c.getKey(), new SchematicBlock(new Identifier((String)c.getValue())));
                this.addEntry(blockEntry);
            }

            if (this.getSelected() != null) {
                this.centerScrollOn((ColorDefinitionTableScreen.ColorDefinitionTableWidget.ColorDefinitionEntry)this.getSelected());
            }

        }

        protected int getScrollbarPositionX() {
            return super.getScrollbarPositionX() + 20;
        }

        public int getRowWidth() {
            return super.getRowWidth() + 10;
        }

        public void setSelected(@Nullable ColorDefinitionTableScreen.ColorDefinitionTableWidget.ColorDefinitionEntry blockEntry) {
            super.setSelected(blockEntry);
            ColorDefinitionTableScreen.this.editBlockButton.active = true;
        }

        protected void renderBackground(MatrixStack matrices) {
            super.renderBackground(matrices);
        }

        protected boolean isFocused() {
            return false;
        }

        @Environment(EnvType.CLIENT)
        public class ColorDefinitionEntry extends net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget.Entry<ColorDefinitionTableScreen.ColorDefinitionTableWidget.ColorDefinitionEntry> {
            protected ColorDefinitionTableScreen screen;
            protected Color color;
            protected SchematicBlock block;

            public ColorDefinitionEntry(ColorDefinitionTableScreen screen, Color color, SchematicBlock block) {
                this.screen = screen;
                this.setColor(color);
                this.setBlock(block);
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                Block blockInstance = this.block.getBlockInstance();
                if (blockInstance != null && ColorDefinitionTableScreen.this.client != null) {
                    String string = I18n.translate(blockInstance.asItem().getTranslationKey(), new Object[0]);
                    int maxSize = 30;
                    if (string.length() > maxSize) {
                        string = string.substring(0, maxSize - 1) + "...";
                    }

                    int fontSize = 10;
                    float half = 14.0F;
                    float posX = (float)(x + 46);
                    float posY = (float)y + half - (float)(fontSize / 2);
                    ColorDefinitionTableWidget.this.client.getItemRenderer().renderInGui(blockInstance.asItem().getDefaultStack(), x + 3, (int)((float)y + half - 8.0F));
                    int colorSize = 20;
                    DrawableHelper.fill(matrices, x + 3 + 16 + 3, (int)((float)y + half - (float)(colorSize / 2)), x + 3 + 16 + 3 + colorSize, (int)((float)y + half - (float)(colorSize / 2) + (float)colorSize), this.color.getRGB());
                    this.screen.textRenderer.drawWithShadow(matrices, string, posX, posY, 16777215, true);
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
                ColorDefinitionTableWidget.this.setSelected(this);
            }

            public Color getColor() {
                return this.color;
            }

            public void setColor(Color color) {
                this.color = color;
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
