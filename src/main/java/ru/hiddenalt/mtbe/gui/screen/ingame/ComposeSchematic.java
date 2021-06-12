package ru.hiddenalt.mtbe.gui.screen.ingame;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.selection.ISelectionManager;
import baritone.api.utils.BetterBlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.imgscalr.Scalr;
import ru.hiddenalt.mtbe.gui.screen.ErrorScreen;
import ru.hiddenalt.mtbe.gui.screen.ingame.compose.SaveAs2DPNGScreen;
import ru.hiddenalt.mtbe.gui.screen.ingame.compose.SaveAsESchematicScreen;
import ru.hiddenalt.mtbe.gui.screen.ingame.compose.SaveImageAsPNGScreen;
import ru.hiddenalt.mtbe.gui.ui.ButtonWidgetTexturedFix;
import ru.hiddenalt.mtbe.gui.ui.NumberFieldWidget;
import ru.hiddenalt.mtbe.gui.ui.toolbar.Toolbar;
import ru.hiddenalt.mtbe.gui.ui.toolbar.ToolbarItem;
import ru.hiddenalt.mtbe.gui.ui.toolbar.ToolbarRow;
import ru.hiddenalt.mtbe.gui.ui.tooltip.SimpleTooltip;
import ru.hiddenalt.mtbe.process.BaritoneProcess;
import ru.hiddenalt.mtbe.process.CommandProcess;
import ru.hiddenalt.mtbe.process.ProcessManager;
import ru.hiddenalt.mtbe.schematic.BufferedImageToSchematic;
import ru.hiddenalt.mtbe.schematic.ExtendedMCEditSchematic;
import ru.hiddenalt.mtbe.schematic.Schematic;
import ru.hiddenalt.mtbe.schematic.SchematicBlock;
import ru.hiddenalt.mtbe.settings.SchematicDimension;
import ru.hiddenalt.mtbe.settings.SettingsManager;
import ru.hiddenalt.mtbe.utils.BufferedImageManipulator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class ComposeSchematic extends Screen {
    private final Screen parent;

    private ButtonWidgetTexturedFix layerSwitcher;
    private ButtonWidget closeButton;
    private ButtonWidget dimensionSwitcherButton;

    private NumberFieldWidget widthField;
    private NumberFieldWidget heightField;
    private NumberFieldWidget x;
    private NumberFieldWidget y;
    private NumberFieldWidget z;

    private Toolbar toolbar;

    private SchematicDimension dimension = SchematicDimension.XY;

    public Render render = Render.Image;
    enum Render {
        Image,
        Schematic
    }

    public float zoom = 1;
    public int offsetX = 0;
    public int offsetY = 0;

    private Vec3i startPos;
    private BufferedImage image;
    private BufferedImage modifiedImage;
    private Schematic schematic;
    private String url;

    private int renderedImageHash = 0;
    private Identifier renderedImage = null;

    public ComposeSchematic(MinecraftClient client, Screen parent, String url) {
        super(new TranslatableText("compose.title"));
        this.parent = parent;
        this.client = client;
        this.url = url;
    }

    protected void init() {

        if(this.startPos == null)
            grabPlayerPosition();

        this.closeButton = this.addButton(new ButtonWidget(this.width - 25, 5, 20, 20, Text.of("X"), (button) -> {
            assert this.client != null;
            this.client.openScreen(this.parent);
        }));

        initializeToolbar();

        // -----------------------
        // Resize
        // -----------------------
        int iconWidth = 16;
        int iconHeight = 16;
        int fieldsWidth = 40;
        int fieldsInnerOffset = 10;
        int xPos = Math.round(this.width / 2) - Math.round((fieldsWidth + fieldsInnerOffset + fieldsWidth + fieldsInnerOffset + iconWidth) / 2);
        int yPos = 30;


        this.widthField  = new NumberFieldWidget(textRenderer, xPos, yPos, fieldsWidth, iconHeight, 1, NumberFieldWidget.MAX_NUMBER);
        this.heightField = new NumberFieldWidget(textRenderer, xPos + fieldsWidth + fieldsInnerOffset, yPos, fieldsWidth, iconHeight, 1, NumberFieldWidget.MAX_NUMBER);
        if(modifiedImage != null){
            this.widthField.setValue(modifiedImage.getWidth());
            this.heightField.setValue(modifiedImage.getHeight());
        }

        this.addButton(this.widthField);
        this.addButton(this.heightField);

        this.addButton(
            new ButtonWidgetTexturedFix(
                xPos + fieldsWidth + fieldsInnerOffset + fieldsWidth + fieldsInnerOffset,
                yPos,
                iconWidth,
                iconHeight,
                Text.of(""),
                (buttonWidget) -> {
                    modifiedImage = Scalr.resize(
                        this.modifiedImage,
                        Scalr.Method.QUALITY,
                        Scalr.Mode.FIT_EXACT,
                        this.widthField.getValue(),
                        this.heightField.getValue()
                    );
                    generateSchematic();
                },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.applySize")),
                new Identifier("mtbe:textures/compose_schematic/apply_size.png"),
                0,
                0,
                iconWidth,
                iconHeight
            )
        );

        // -----------------------
        // Bottom panel
        // -----------------------
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableText("composeSchematic.build.fabritone"), (buttonWidget) -> {
            try {
                this.schematic.saveAsTemp();

                File file = new File(
                    new File(MinecraftClient.getInstance().runDirectory, SettingsManager.getSchematicsDir()),
                    this.schematic.getTempFilename()
                );
                ExtendedMCEditSchematic schematic = new ExtendedMCEditSchematic(Objects.requireNonNull(NbtIo.readCompressed(file)));
                Vec3i pos = this.startPos;

                BaritoneProcess p = new BaritoneProcess(schematic, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
                ProcessManager.pushAndStart(p);

                assert this.client != null;
                this.client.openScreen(null);
                this.client.mouse.lockCursor();
            } catch (IOException e) {
                assert this.client != null;
                this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
            }

        }));


        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 65, 200, 20, new TranslatableText("composeSchematic.build.cmd"), (buttonWidget) -> {

            try {
                this.schematic.saveAsTemp();

                CommandProcess commandBuilder = new CommandProcess(this.schematic, 50, "/setblock %X% %Y% %Z% %BLOCK_ID%", this.startPos);
                ProcessManager.pushAndStart(commandBuilder);

                assert this.client != null;
                this.client.openScreen(null);
                this.client.mouse.lockCursor();
            } catch (IOException e) {
                assert this.client != null;
                this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
            }

        }));


        // -----------------------
        // Start cords + position grabber
        // -----------------------
        this.addButton(new ButtonWidget(15, this.height - 25 * 4, 50, 20, new TranslatableText("composeSchematic.grabPlayerPosition"), (buttonWidget) -> {
            grabPlayerPosition();
            Vec3i pos = this.startPos;
            this.x.setValue(pos.getX());
            this.y.setValue(pos.getY());
            this.z.setValue(pos.getZ());
        }));

        this.z = new NumberFieldWidget(textRenderer, 15, this.height - 25 * 1, 50, 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.z.setValue(this.startPos.getZ());
        this.z.setChangedListener(this::positionChanged);

        this.y = new NumberFieldWidget(textRenderer, 15, this.height - 25 * 2, 50, 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.y.setValue(this.startPos.getY());
        this.y.setChangedListener(this::positionChanged);

        this.x = new NumberFieldWidget(textRenderer, 15, this.height - 25 * 3, 50, 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.x.setValue(this.startPos.getX());
        this.x.setChangedListener(this::positionChanged);

        this.addButton(this.x);
        this.addButton(this.y);
        this.addButton(this.z);


        // -----------------------
        // Dimension switcher button
        this.dimensionSwitcherButton = this.addButton(new ButtonWidget(this.width - 25 - 60 - 5, 5, 60, 20, Text.of("Dimension"), (button) -> {
            switch(this.dimension){
                case XY:
                    this.dimension = SchematicDimension.XZ;
                    break;

                case XZ:
                    this.dimension = SchematicDimension.ZY;
                    break;

                case ZY:
                    this.dimension = SchematicDimension.XY;
                    break;
            }
            generateSchematic();
        }));


        // If not loaded
        if(this.image == null){
            downloadImage();
            generateSchematic();
        }
    }

    protected void initializeToolbar() {
        Toolbar toolbar = new Toolbar(this.client, this);
        toolbar.setX(5);
        toolbar.setY(5);
        toolbar.setDirection(Toolbar.Direction.HORIZONTAL);
        toolbar.setRowsOffset(2);
        toolbar.setItemsOffset(2);
        toolbar.setItemHeight(16);
        toolbar.setItemWidth(16);

        ToolbarRow viewTools = new ToolbarRow();
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.switchLayer"),
                new Identifier("mtbe:textures/compose_schematic/map_to_image.png"),
                (buttonWidget) -> {
                    switchLayer();

                    ButtonWidgetTexturedFix bw = (ButtonWidgetTexturedFix) buttonWidget;
                    switch(this.render){
                        case Schematic:
                            bw.setTexture(new Identifier("mtbe:textures/compose_schematic/map_to_image.png"));
                            break;

                        case Image:
                            bw.setTexture(new Identifier("mtbe:textures/compose_schematic/image_to_map.png"));
                            break;
                    }
                }
        ));
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.saveImageAsPng"),
                new Identifier("mtbe:textures/compose_schematic/save.png"),
                (buttonWidget) -> {
                    assert this.client != null;
                    this.client.openScreen(new SaveImageAsPNGScreen(this, this.modifiedImage));
                }
        ));
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.export2DSchematicAsPng"),
                new Identifier("mtbe:textures/compose_schematic/save_as_png.png"),
                (buttonWidget) -> {
                    assert this.client != null;
                    this.client.openScreen(new SaveAs2DPNGScreen(this, this.schematic));
                }
        ));
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.saveAsESchematic"),
                new Identifier("mtbe:textures/compose_schematic/save_as_eschematic.png"),
                (buttonWidget) -> {
                    assert this.client != null;
                    this.client.openScreen(new SaveAsESchematicScreen(this, this.schematic));
                }
        ));
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.saveAsSchematic"),
                new Identifier("mtbe:textures/compose_schematic/save_as_schematic.png"),
                (buttonWidget) -> {

                }
        ));
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.zoomIn"),
                new Identifier("mtbe:textures/compose_schematic/zoom_in.png"),
                (buttonWidget) -> {
                    this.zoom(2F);
                }
        ));
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.zoomOut"),
                new Identifier("mtbe:textures/compose_schematic/zoom_out.png"),
                (buttonWidget) -> {
                    this.zoom((float) 1/2);
                }
        ));
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.center"),
                new Identifier("mtbe:textures/compose_schematic/center.png"),
                (buttonWidget) -> {
                    this.zoom = 1;
                    this.offsetX = 0;
                    this.offsetY = 0;
                }
        ));
        viewTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.showPreview"),
                new Identifier("mtbe:textures/compose_schematic/preview.png"),
                (buttonWidget) -> {
                    IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();

                    int x1 = this.x.getValue();
                    int y1 = this.y.getValue();
                    int z1 = this.z.getValue();

                    int x2 = this.x.getValue() + this.schematic.getWidth() - 1;
                    int y2 = this.y.getValue() + this.schematic.getHeight() - 1;
                    int z2 = this.z.getValue() + this.schematic.getLength() - 1;

                    ISelectionManager selectionManager = bar.getSelectionManager();

                    selectionManager.removeAllSelections();
                    selectionManager.addSelection(
                            BetterBlockPos.from(new BlockPos(x1, y1, z1)),
                            BetterBlockPos.from(new BlockPos(x2, y2, z2))
                    );
                }
        ));

        ToolbarRow imageTools = new ToolbarRow();
        imageTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.redownloadImage"),
                new Identifier("mtbe:textures/compose_schematic/redownload.png"),
                (buttonWidget) -> {
                    downloadImage();
                    generateSchematic();
                }
        ));
        imageTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.regenerateImage"),
                new Identifier("mtbe:textures/compose_schematic/regenerate.png"),
                (buttonWidget) -> {
                    generateSchematic();
                }
        ));
        imageTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.loadFromCache"),
                new Identifier("mtbe:textures/compose_schematic/no_effects.png"),
                (buttonWidget) -> {
                    this.modifiedImage = this.image;
                    this.renderedImageHash = 0; // force redraw image
                    generateSchematic();
                }
        ));
        imageTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.verticalFlip"),
                new Identifier("mtbe:textures/compose_schematic/vertical_flip.png"),
                (buttonWidget) -> {
                    modifiedImage = Scalr.rotate(this.modifiedImage, Scalr.Rotation.FLIP_VERT);
                    generateSchematic();
                }
        ));
        imageTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.horizontalFlip"),
                new Identifier("mtbe:textures/compose_schematic/horizontal_flip.png"),
                (buttonWidget) -> {
                    modifiedImage = Scalr.rotate(this.modifiedImage, Scalr.Rotation.FLIP_HORZ);
                    generateSchematic();
                }
        ));
        imageTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.rotate"),
                new Identifier("mtbe:textures/compose_schematic/rotate.png"),
                (buttonWidget) -> {
                    modifiedImage = Scalr.rotate(this.modifiedImage, Scalr.Rotation.CW_90);
                    generateSchematic();
                }
        ));
        imageTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.image.enlarge"),
                new Identifier("mtbe:textures/compose_schematic/enlarge_image.png"),
                (buttonWidget) -> {
                    modifiedImage = Scalr.resize(
                            this.modifiedImage,
                            Scalr.Method.QUALITY,
                            Scalr.Mode.FIT_EXACT,
                            (int) Math.round(this.modifiedImage.getWidth() * 1.1),
                            (int) Math.round(this.modifiedImage.getHeight() * 1.1)
                    );
                    generateSchematic();
                }
        ));
        imageTools.add(new ToolbarItem(
                new TranslatableText("composeSchematic.image.decrease"),
                new Identifier("mtbe:textures/compose_schematic/decrease_image.png"),
                (buttonWidget) -> {
                    modifiedImage = Scalr.resize(
                            this.modifiedImage,
                            Scalr.Method.QUALITY,
                            Scalr.Mode.FIT_EXACT,
                            (int) Math.round(this.modifiedImage.getWidth() * 0.9),
                            (int) Math.round(this.modifiedImage.getHeight() * 0.9)
                    );
                    generateSchematic();
                }
        ));

        ToolbarRow filters = new ToolbarRow();
        filters.add(new ToolbarItem(
                new TranslatableText("composeSchematic.grayscale"),
                new Identifier("mtbe:textures/compose_schematic/grayscale.png"),
                (buttonWidget) -> {
                    if (this.modifiedImage == null) return;
                    this.modifiedImage = (new BufferedImageManipulator(this.modifiedImage)).makeGrayscale().getBufferedImage();
                    generateSchematic();
                }
        ));
        filters.add(new ToolbarItem(
                new TranslatableText("composeSchematic.sharpen"),
                new Identifier("mtbe:textures/compose_schematic/sharpen.png"),
                (buttonWidget) -> {
                    if (this.modifiedImage == null) return;
                    this.modifiedImage = (new BufferedImageManipulator(this.modifiedImage)).makeSharpen().getBufferedImage();
                    generateSchematic();
                }
        ));
        filters.add(new ToolbarItem(
                new TranslatableText("composeSchematic.makeNegative"),
                new Identifier("mtbe:textures/compose_schematic/negative.png"),
                (buttonWidget) -> {
                    if (this.modifiedImage == null) return;
                    this.modifiedImage = (new BufferedImageManipulator(this.modifiedImage)).makeNegative().getBufferedImage();
                    generateSchematic();
                }
        ));


        toolbar.addRow(viewTools, imageTools, filters);

        for(ButtonWidgetTexturedFix button : toolbar.make())
            this.addButton(button);

        this.toolbar = toolbar;
    }

    protected void downloadImage() {
        try {
            this.image = ImageIO.read(new URL(this.url).openStream());
            if(this.image == null) throw new Exception("File is unsupported");
            this.modifiedImage = this.image;

            this.widthField.setValue(this.modifiedImage.getWidth());
            this.heightField.setValue(this.modifiedImage.getHeight());
        } catch (IOException e) {
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.cant-load-image-to-schematic", this.url, e.getLocalizedMessage()).getString()));
        } catch (Exception e){
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
        }
    }

    protected void generateSchematic() {
        try {
            if(modifiedImage == null) return;

            BufferedImageToSchematic generator = new BufferedImageToSchematic(modifiedImage);
            generator.setSchematicDimension(this.dimension);
            this.schematic = generator.generateSchematic();
        } catch (Exception e){
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
        }
    }

    protected void grabPlayerPosition() {
        assert this.client != null;
        assert this.client.player != null;
        Vec3d vec = this.client.player.getPos();

        int x = (int)vec.getX();
        int y = (int)vec.getY();
        int z = (int)vec.getZ();

        this.startPos = new Vec3i(
            (x < 0) ? (x - 1) : (x),
            (y < 0) ? (y - 1) : (y),
            (z < 0) ? (z - 1) : (z)
        );
    }

    protected void switchLayer() {
        switch(this.render){
            case Image:
                this.render = Render.Schematic;
                break;

            case Schematic:
                this.render = Render.Image;
                break;
        }
    }

    public void positionChanged(String s){
        this.startPos = new Vec3i(this.x.getValue(), this.y.getValue(), this.z.getValue());
    }

    public void filesDragged(List<Path> paths) {
        if(paths.size() <= 0) return;

        this.url = "file:///" + paths.get(0).toString();
        downloadImage();
        generateSchematic();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String widthFieldText = this.widthField.getText();
        String heightFieldText = this.heightField.getText();
        String xText = this.x.getText();
        String yText = this.y.getText();
        String zText = this.z.getText();

        this.init(client, width, height);

        this.widthField.setText(widthFieldText);
        this.heightField.setText(heightFieldText);
        this.x.setText(xText);
        this.y.setText(yText);
        this.z.setText(zText);
    }

    public void tick() {
        this.widthField.tick();
        this.heightField.tick();

        this.x.tick();
        this.y.tick();
        this.z.tick();
    }

    public void zoom(float k){
        if(k > 1 && this.zoom <= 8) {
            this.zoom *= k;
        }
        if(k < 1 && this.zoom >= 0.5) {
            this.zoom *= k;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(amount > 0) this.zoom((float) 2);
        if(amount < 0) this.zoom((float) 1/2);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.offsetX -= deltaX;
        this.offsetY -= deltaY;
        return false;
    }









    protected float blockSize = 2F;

    public void makeImageToTexture(){
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(modifiedImage, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            NativeImage i = NativeImage.read(is);
            assert this.client != null;
            this.renderedImage = this.client.getTextureManager().registerDynamicTexture("tmp", new NativeImageBackedTexture(i));
        } catch (IOException e) {
            this.renderedImage = new Identifier("mtbe:textures/menu/unknown.png");
        }
    }

    public void renderImage(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // If no image loaded
        if(modifiedImage == null) {
            drawCenteredText(matrices, this.textRenderer, new TranslatableText("compose.image-error"), this.width / 2, this.height / 2, Color.red.getRGB());
            return;
        }

        float blockSize = this.blockSize;
        float z = this.zoom;
        blockSize *= z;

        int paddingLeft = Math.round((modifiedImage.getWidth() * blockSize) / 2) + offsetX;
        int paddingBottom = Math.round((modifiedImage.getHeight() * blockSize) / 2) + offsetY;

        //Regenerate image if changed
        if(renderedImageHash != modifiedImage.hashCode()){
            renderedImageHash = modifiedImage.hashCode();
            makeImageToTexture();
        }

        assert this.client != null;
        this.client.getTextureManager().bindTexture(this.renderedImage);
        drawTexture(
            matrices,
            Math.round(this.width / 2 - paddingLeft),
            Math.round(this.height / 2 - paddingBottom),
            Math.round(modifiedImage.getWidth() * blockSize),
            Math.round(modifiedImage.getHeight() * blockSize),
            0.0F,
            0.0F,
            16,
            128,
            16,
            128
        );

    }

    public void renderSchematic2D(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(schematic == null) {
            drawCenteredText(
                matrices,
                this.textRenderer,
                new TranslatableText("compose.generating-schematic"),
                this.width / 2,
                this.height / 2,
                Color.yellow.getRGB()
            );
            return;
        }

        // TODO: make a png of blockmap to reduce lags

        SchematicBlock[][][] blocks = this.schematic.getBlocks();

        assert this.client != null;
        TextureManager manager = this.client.getTextureManager();

        float blockSize = this.blockSize;
        float z = this.zoom;
        blockSize *= z;

        int paddingLeft     = Math.round((blocks.length * blockSize) / 2) + offsetX;
        int paddingBottom   = Math.round((blocks[0].length * blockSize) / 2) + offsetY;

        for (int x = 0; x < this.schematic.getWidth(); x++) {
            for (int y = 0; y < this.schematic.getHeight(); y++) {
                if (blocks[x][y][0] == null) continue;
                SchematicBlock block = blocks[x][y][0];
                if (block.getIdentifier().equals(new Identifier("minecraft:air"))) continue;

                int x_pos = Math.round(this.width / 2 - paddingLeft + x * blockSize);
                int y_pos = Math.round(this.height / 2 - paddingBottom + y * blockSize);

                if(x_pos + blockSize <= 0) continue;
                if(y_pos + blockSize <= 0) continue;

                if(x_pos - blockSize >= this.width) continue;
                if(y_pos - blockSize >= this.height) continue;

                manager.bindTexture(new Identifier(block.getIdentifier().getNamespace() + ":textures/block/" + block.getIdentifier().getPath() + ".png"));
                drawTexture(
                    matrices,
                    x_pos,
                    y_pos,
                    Math.round(blockSize),
                    Math.round(blockSize),
                    0.0F,
                    0.0F,
                    16,
                    16,
                    16,
                    16
                );
            }
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        switch (this.render){
            case Image:
                renderImage(matrices, mouseX, mouseY, delta);
                break;

            case Schematic:
                renderSchematic2D(matrices, mouseX, mouseY, delta);
                break;
        }


        // Blocks required
        if(schematic != null) {
            int blocksCount = this.schematic.getWidth() * this.schematic.getHeight() * this.schematic.getLength();

            drawCenteredText(
                matrices,
                this.textRenderer,
                Text.of("Blocks required: " + blocksCount),
                Math.round(this.width / 2),
                Math.round(50),
                (blocksCount > 9 * 4 * 64) ? (Color.red.getRGB()) : ( (blocksCount > 9 * 64) ? Color.yellow.getRGB() : Color.green.getRGB())
            );
        }


        // Schematic info
        if(modifiedImage != null && this.schematic != null){

            int start_x = this.x.getValue();
            int start_y = this.y.getValue();
            int start_z = this.z.getValue();

            int end_x   = start_x + this.schematic.getWidth()  - 1;
            int end_y   = start_y + this.schematic.getHeight() - 1;
            int end_z   = start_z + this.schematic.getLength() - 1;

            String[] strings = new String[]{
                "Start = left-bottom corner",
                "XYZ: [" + start_x + "; "+ start_y + "; " + start_z + "]",
                "End = right-top corner",
                "XYZ: [" + end_x + "; " + end_y + "; " + end_z + "]",
                "",
                "Dimensions: " + this.dimension.name()
            };

            int startY = 30;

            for (int i = 0; i < strings.length; i++) {
                String s = strings[i];
                int width = this.textRenderer.getWidth(s);
                drawCenteredText(
                    matrices,
                    this.textRenderer,
                    Text.of(s),
                    this.width - 5 - width / 2,
                    startY + i * 10,
                    Color.white.getRGB()
                );
            }
        }


        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, Color.white.getRGB());

        this.widthField.renderButton(matrices, mouseX, mouseY, delta);
        this.heightField.renderButton(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, this.textRenderer, Text.of("X:"), 7, this.height + 7 - 25 * 3, Color.white.getRGB());
        drawCenteredText(matrices, this.textRenderer, Text.of("Y:"), 7, this.height + 7 - 25 * 2, Color.white.getRGB());
        drawCenteredText(matrices, this.textRenderer, Text.of("Z:"), 7, this.height + 7 - 25 * 1, Color.white.getRGB());

        this.x.renderButton(matrices, mouseX, mouseY, delta);
        this.y.renderButton(matrices, mouseX, mouseY, delta);
        this.z.renderButton(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
    }
}
