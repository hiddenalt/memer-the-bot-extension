package ru.hiddenalt.mtbe.gui.screen.ingame;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.IPlayerContext;
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
import org.imgscalr.Scalr;
import ru.hiddenalt.mtbe.gui.screen.ErrorScreen;
import ru.hiddenalt.mtbe.gui.screen.ingame.compose.SaveAs2DPNGScreen;
import ru.hiddenalt.mtbe.gui.screen.ingame.compose.SaveAsESchematicScreen;
import ru.hiddenalt.mtbe.gui.screen.ingame.compose.SaveImageAsPNGScreen;
import ru.hiddenalt.mtbe.gui.ui.ButtonWidgetTexturedFix;
import ru.hiddenalt.mtbe.gui.ui.NumberFieldWidget;
import ru.hiddenalt.mtbe.gui.ui.tooltip.SimpleTooltip;
import ru.hiddenalt.mtbe.process.BaritoneProcess;
import ru.hiddenalt.mtbe.process.CommandProcess;
import ru.hiddenalt.mtbe.process.ProcessManager;
import ru.hiddenalt.mtbe.schematic.BufferedImageToSchematic;
import ru.hiddenalt.mtbe.schematic.ExtendedMCEditSchematic;
import ru.hiddenalt.mtbe.schematic.Schematic;
import ru.hiddenalt.mtbe.schematic.SchematicBlock;
import ru.hiddenalt.mtbe.settings.SchematicDimension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class ComposeSchematic extends Screen {
    private final Screen parent;
    private ButtonWidget closeButton;
    private NumberFieldWidget widthField;
    private NumberFieldWidget heightField;

    private NumberFieldWidget x;
    private NumberFieldWidget y;
    private NumberFieldWidget z;

    enum Render {
        Image,
        Schematic
    }

    public Render render = Render.Image;
    public float zoom = 1;
    public int offsetX = 0;
    public int offsetY = 0;

    private BufferedImage image;
    private BufferedImage modyfiedImage;
    private Schematic schematic;
    private String url;
    private int imageWidth = 128;
    private int imageHeight = 128;
    private ButtonWidgetTexturedFix layerSwitcher;


    private ButtonWidget dimensionSwitchButton;
    private SchematicDimension dimension = SchematicDimension.XY;


    public ComposeSchematic(MinecraftClient client, Screen parent, String url) {
        super(new TranslatableText("compose.title"));
        this.parent = parent;
        this.client = client;
        this.url = url;
    }

    public void downloadImage(){
        try {
            this.image = ImageIO.read(new URL(this.url).openStream());
            this.modyfiedImage = this.image;

            if(this.image == null) throw new Exception("File is unsupported");

        } catch (IOException e) {
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.cant-load-image-to-schematic", this.url, e.getLocalizedMessage()).getString()));
        } catch (Exception e){
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
        }
    }

    public void generateSchematic(){
        try {
            if(modyfiedImage == null) return;

            BufferedImageToSchematic generator = new BufferedImageToSchematic(modyfiedImage);
            generator.setSchematicDimension(this.dimension);

//            if(imageWidth != -1 && imageHeight != -1)
//                generator.setCustomImageSize(imageWidth, imageHeight);

            this.widthField.setText(""+this.modyfiedImage.getWidth());
            this.heightField.setText(""+this.modyfiedImage.getHeight());

            this.schematic = generator.generateSchematic();


        } catch (Exception e){
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
        }
    }

    private Vec3d startPos;

    protected void grabPlayerPosition(){
        assert this.client != null;
        assert this.client.player != null;
        Vec3d vec = this.client.player.getPos();

        int x = (int)vec.getX();
        int y = (int)vec.getY();
        int z = (int)vec.getZ();

        this.startPos = new Vec3d(
            (x < 0) ? (x - 1) : (x),
            (y < 0) ? (y - 1) : (y),
            (z < 0) ? (z - 1) : (z)
        );
    }

    protected void init(){
        assert this.client != null;
        assert this.client.player != null;

        if(this.startPos == null) grabPlayerPosition();

        this.closeButton = this.addButton(new ButtonWidget(this.width - 25, 5, 20, 20, Text.of("X"), (button) -> {
            assert this.client != null;
            this.client.openScreen(this.parent);
        }));



        // EDITOR BUTTONS:

        int iconWidth = 16;
        int iconHeight = 16;

        int xPos = 10;
        int yPos = 10;
        int offset = 2;

        // Left column
        layerSwitcher = this.addButton(new ButtonWidgetTexturedFix(xPos, yPos, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            switch(this.render){
                case Image:
                    this.render = Render.Schematic;
                    layerSwitcher.setTexture(new Identifier("mtbe:textures/compose_schematic/map_to_image.png"));
                    break;

                case Schematic:
                    this.render = Render.Image;
                    layerSwitcher.setTexture(new Identifier("mtbe:textures/compose_schematic/image_to_map.png"));
                    break;
            }
        },
            new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.switchLayer")),
            new Identifier("mtbe:textures/compose_schematic/map_to_image.png"), 0, 0, iconWidth, iconHeight));


        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 1, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new SaveImageAsPNGScreen(this, this.modyfiedImage));
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.saveImageAsPng")),
                new Identifier("mtbe:textures/compose_schematic/save.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 2, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new SaveAs2DPNGScreen(this, this.schematic));
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.export2DSchematicAsPng")),
                new Identifier("mtbe:textures/compose_schematic/save_as_png.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 3, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            assert this.client != null;
            this.client.openScreen(new SaveAsESchematicScreen(this, this.schematic));
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.saveAsESchematic")),
                new Identifier("mtbe:textures/compose_schematic/save_as_eschematic.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 4, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {

        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.saveAsSchematic")),
                new Identifier("mtbe:textures/compose_schematic/save_as_schematic.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 5, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            this.zoom((float) 2);
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.zoomIn")),
                new Identifier("mtbe:textures/compose_schematic/zoom_in.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 6, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            this.zoom((float) 1/2);
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.zoomOut")),
                new Identifier("mtbe:textures/compose_schematic/zoom_out.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 7, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            this.zoom = 1;
            this.offsetX = 0;
            this.offsetY = 0;
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.center")),
                new Identifier("mtbe:textures/compose_schematic/center.png"), 0, 0, iconWidth, iconHeight));



        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 8, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();
//            WorldRender.showPreview(this.schematic, new BlockPos(vec.x, vec.y, vec.z));

            int x1 = this.x.getValue();
            int y1 = this.y.getValue();
            int z1 = this.z.getValue();

            int x2 = this.x.getValue() + this.schematic.getWidth() - 1;
            int y2 = this.y.getValue() + this.schematic.getHeight() - 1;
            int z2 = this.z.getValue() + this.schematic.getLength() - 1;

            bar.getSelectionManager().removeAllSelections();
            bar.getSelectionManager().addSelection(
                    BetterBlockPos.from(new BlockPos(x1, y1, z1)),
                    BetterBlockPos.from(new BlockPos(x2, y2, z2))
            );
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.showPreview")),
                new Identifier("mtbe:textures/compose_schematic/preview.png"), 0, 0, iconWidth, iconHeight));




        xPos = 10 + iconWidth + 4;
        yPos = 10;
        offset = 2;





        // Left column

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 0, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            downloadImage();
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.redownloadImage")),
                new Identifier("mtbe:textures/compose_schematic/redownload.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 1, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.regenerateImage")),
                new Identifier("mtbe:textures/compose_schematic/regenerate.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 2, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            this.modyfiedImage = this.image;
            this.renderedImageHash = 0; // force redraw image
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.loadFromCache")),
                new Identifier("mtbe:textures/compose_schematic/no_effects.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 3, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            modyfiedImage = Scalr.rotate(this.modyfiedImage, Scalr.Rotation.FLIP_VERT, new BufferedImageOp[0]);
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.verticalFlip")),
                new Identifier("mtbe:textures/compose_schematic/vertical_flip.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 4, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            modyfiedImage = Scalr.rotate(this.modyfiedImage, Scalr.Rotation.FLIP_HORZ, new BufferedImageOp[0]);
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.horizontalFlip")),
                new Identifier("mtbe:textures/compose_schematic/horizontal_flip.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 5, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            modyfiedImage = Scalr.rotate(this.modyfiedImage, Scalr.Rotation.CW_90, new BufferedImageOp[0]);
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.rotate")),
                new Identifier("mtbe:textures/compose_schematic/rotate.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 6, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            modyfiedImage = Scalr.resize(
                    this.modyfiedImage,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.FIT_EXACT,
                    (int) Math.round(this.modyfiedImage.getWidth() * 1.1),
                    (int) Math.round(this.modyfiedImage.getHeight() * 1.1),
                    new BufferedImageOp[0]
            );
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.image.enlarge")),
                new Identifier("mtbe:textures/compose_schematic/enlarge_image.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 7, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            modyfiedImage = Scalr.resize(
                    this.modyfiedImage,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.FIT_EXACT,
                    (int) Math.round(this.modyfiedImage.getWidth() * 0.9),
                    (int) Math.round(this.modyfiedImage.getHeight() * 0.9),
                    new BufferedImageOp[0]
            );
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.image.decrease")),
                new Identifier("mtbe:textures/compose_schematic/decrease_image.png"), 0, 0, iconWidth, iconHeight));



        xPos = 10 + iconWidth + 4 + iconWidth + 4;
        yPos = 10;
        offset = 2;

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 0, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            if (this.modyfiedImage == null) return;

            BufferedImage image = new BufferedImage(modyfiedImage.getWidth(), modyfiedImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics gr = image.getGraphics();
            gr.drawImage(modyfiedImage, 0, 0, null);
            gr.dispose();

            int w = image.getWidth();
            int h = image.getHeight();


            for (int x = 0; x < w; x++){
                for (int y = 0; y < h; y++){
                    int pixel = image.getRGB(x,y);
                    Color col = new Color(pixel, true);

                    int a = col.getAlpha();
                    if(a == 0) continue;
                    int r = col.getRed();
                    int g = col.getGreen();
                    int b = col.getBlue();

                    int avg = (r+g+b)/3;

                    image.setRGB(x, y, (a<<24) | (avg<<16) | (avg<<8) | avg);
                }
            }

            this.modyfiedImage = image;

            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.grayscale")),
                new Identifier("mtbe:textures/compose_schematic/grayscale.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 1, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            if (this.modyfiedImage == null) return;

            Kernel kernel = new Kernel(3,3, new float[]{
                    0.f, -1.f, 0.f,
                    -1.f, 5.0f, -1.f,
                    0.f, -1.f, 0.f});
            ConvolveOp cop = new ConvolveOp(kernel,
                    ConvolveOp.EDGE_NO_OP,
                    null);
            modyfiedImage = cop.filter(modyfiedImage, null);

            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.sharpen")),
                new Identifier("mtbe:textures/compose_schematic/sharpen.png"), 0, 0, iconWidth, iconHeight));

        this.addButton(new ButtonWidgetTexturedFix(xPos, yPos + (iconHeight + offset) * 2, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            if (this.modyfiedImage == null) return;

            // Deep copy of modyfiedImage
            BufferedImage tmp = new BufferedImage(modyfiedImage.getWidth(), modyfiedImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = tmp.getGraphics();
            g.drawImage(modyfiedImage, 0, 0, null);
            g.dispose();

            int w = tmp.getWidth();
            int h = tmp.getHeight();


            for (int x = 0; x < w; x++){
                for (int y = 0; y < h; y++){
                    int pixel = tmp.getRGB(x,y);
                    Color col = new Color(pixel, true);

                    if(col.getAlpha() == 0) continue;

                    col = new Color(255 - col.getRed(),
                            255 - col.getGreen(),
                            255 - col.getBlue());
                    tmp.setRGB(x, y, col.getRGB());
                }
            }

            this.modyfiedImage = tmp;

            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.makeNegative")),
                new Identifier("mtbe:textures/compose_schematic/negative.png"), 0, 0, iconWidth, iconHeight));








        // Resize
        int fieldsWidth = 40;
        int fieldsInnerOffset = 10;
        xPos = Math.round(this.width / 2) - Math.round((fieldsWidth + fieldsInnerOffset + fieldsWidth + fieldsInnerOffset + iconWidth) / 2);
        yPos = 30;
        this.widthField = new NumberFieldWidget(textRenderer, xPos, yPos, fieldsWidth, iconHeight, 1, 99999999);
        this.heightField = new NumberFieldWidget(textRenderer, xPos + fieldsWidth + fieldsInnerOffset, yPos, fieldsWidth, iconHeight, 1, 99999999);
        if(modyfiedImage != null){
            this.widthField.setValue(modyfiedImage.getWidth());
            this.heightField.setValue(modyfiedImage.getHeight());
        }

        this.addButton(this.widthField);
        this.addButton(this.heightField);

        this.addButton(new ButtonWidgetTexturedFix(xPos + fieldsWidth + fieldsInnerOffset + fieldsWidth + fieldsInnerOffset,
        yPos, iconWidth, iconHeight, Text.of(""), (buttonWidget) -> {
            modyfiedImage = Scalr.resize(this.modyfiedImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT, this.widthField.getValue(), this.heightField.getValue(), new BufferedImageOp[0]);
            generateSchematic();
        },
                new SimpleTooltip(textRenderer, new TranslatableText("composeSchematic.applySize")),
                new Identifier("mtbe:textures/compose_schematic/apply_size.png"), 0, 0, iconWidth, iconHeight));






        // Right column
        xPos = this.width - 10 - iconWidth;
        yPos = 40;
        offset = 2;









        // Bottom panel
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableText("composeSchematic.build.fabritone"), (buttonWidget) -> {

            try {
                this.schematic.saveAsTemp();

                IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();

                IPlayerContext player = bar.getPlayerContext();

                // Open generated schematic and build!
                File file = new File(new File(MinecraftClient.getInstance().runDirectory, "schematics"), this.schematic.getTempFilename() + ".eschematic");
                ExtendedMCEditSchematic schematic = new ExtendedMCEditSchematic(Objects.requireNonNull(NbtIo.readCompressed(file)));

                BaritoneProcess p = new BaritoneProcess(schematic, this.startPos);
                ProcessManager.pushAndStart(p);

                assert this.client != null;
                this.client.openScreen((Screen)null);
                this.client.mouse.lockCursor();
            } catch (IOException e) {
                assert this.client != null;
                this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
            }

        }));


        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 65, 200, 20, new TranslatableText("composeSchematic.build.cmd"), (buttonWidget) -> {

            try {
                this.schematic.saveAsTemp();
                IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();

                CommandProcess commandBuilder = new CommandProcess(this.schematic, 50, "/setblock %X% %Y% %Z% %BLOCK_ID%", this.startPos);
                ProcessManager.pushAndStart(commandBuilder);

                assert this.client != null;
                this.client.openScreen((Screen)null);
                this.client.mouse.lockCursor();
            } catch (IOException e) {
                assert this.client != null;
                this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
            }

        }));


        // Start cords + position grabber

        this.addButton(new ButtonWidget(15, this.height - 25 * 4, 50, 20, new TranslatableText("composeSchematic.grabPlayerPosition"), (buttonWidget) -> {
            grabPlayerPosition();
            Vec3d pos = this.startPos;
            this.x.setValue((int) pos.x);
            this.y.setValue((int) pos.y);
            this.z.setValue((int) pos.z);
        }));

        this.z = new NumberFieldWidget(textRenderer, 15, this.height - 25 * 1, 50, 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.z.setValue((int) this.startPos.z);
        this.z.setChangedListener(this::positionChanged);

        this.y = new NumberFieldWidget(textRenderer, 15, this.height - 25 * 2, 50, 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.y.setValue((int) this.startPos.y);
        this.y.setChangedListener(this::positionChanged);

        this.x = new NumberFieldWidget(textRenderer, 15, this.height - 25 * 3, 50, 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.x.setValue((int) this.startPos.x);
        this.x.setChangedListener(this::positionChanged);

        this.addButton(this.x);
        this.addButton(this.y);
        this.addButton(this.z);



        this.dimensionSwitchButton = this.addButton(new ButtonWidget(this.width - 25 - 60 - 5, 5, 60, 20, Text.of("Dimension"), (button) -> {
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



        if(this.image == null){
            downloadImage();
            generateSchematic();
        }
    }

    public void positionChanged(String s){
        this.startPos = new Vec3d(this.x.getValue(), this.y.getValue(), this.z.getValue());
    }

    public void filesDragged(List<Path> paths) {
        if(paths.size() > 0){
            this.url = "file:///"+paths.get(0).toString();
            downloadImage();
            generateSchematic();
        }
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

    private int renderedImageHash = 0;
    private Identifier renderedImage = null;
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        float blockSize = (float) 2.0;

        switch (this.render){

            case Image:
                if(modyfiedImage == null) {
                    drawCenteredText(matrices, this.textRenderer, new TranslatableText("compose.image-error"), this.width / 2, this.height / 2, Color.red.getRGB());
                    break;
                }

                float z = this.zoom;

                blockSize *= z;

                int paddingLeft = Math.round((modyfiedImage.getWidth() * blockSize) / 2) + offsetX;
                int paddingBottom = Math.round((modyfiedImage.getHeight() * blockSize) / 2) + offsetY;

                //Regenerate image if changed
                if(renderedImageHash != modyfiedImage.hashCode()){
                    renderedImageHash = modyfiedImage.hashCode();
                    try {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(modyfiedImage, "png", os);
                        InputStream is = new ByteArrayInputStream(os.toByteArray());

                        NativeImage i = NativeImage.read(is);
                        this.renderedImage = this.client.getTextureManager().registerDynamicTexture("tmp", new NativeImageBackedTexture(i));
                    } catch (IOException e) {
                        this.renderedImage = new Identifier("mtbe:textures/menu/unknown.png");
                    }
                }

                this.client.getTextureManager().bindTexture(this.renderedImage);
                drawTexture(
                    matrices,
                    Math.round(this.width / 2 - paddingLeft),
                    Math.round(this.height / 2 - paddingBottom),
                    Math.round(modyfiedImage.getWidth() * blockSize),
                    Math.round(modyfiedImage.getHeight() * blockSize),
                    0.0F,
                    0.0F,
                    16,
                    128,
                    16,
                    128
                );

                break;

            case Schematic:
                if(schematic == null) {
                    drawCenteredText(matrices, this.textRenderer, new TranslatableText("compose.generating-schematic"), this.width / 2, this.height / 2, Color.yellow.getRGB());
                } else {

                    // TODO: make a png of blockmap to reduce lags

                    SchematicBlock[][][] blocks = this.schematic.getBlocks();

                    assert this.client != null;
                    TextureManager manager = this.client.getTextureManager();


                    z = this.zoom;
                    blockSize *= z;

                    paddingLeft = Math.round((blocks.length * blockSize) / 2) + offsetX;
                    paddingBottom = Math.round((blocks[0].length * blockSize) / 2) + offsetY;

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
                    break;
                }
        }



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

        if(modyfiedImage != null && this.schematic != null){

            String[] strings = new String[]{
                "Start = left-bottom corner",
                "XYZ: ["+this.x.getValue()+"; "+this.y.getValue()+"; "+this.z.getValue()+"]",
                "End = right-top corner",
                "XYZ: ["+(this.x.getValue() + this.schematic.getWidth() - 1)+"; "+(this.y.getValue() + this.schematic.getHeight() - 1)+"; "+(this.z.getValue() + this.schematic.getLength() - 1)+"]",
                "",
                "Direction: "+this.dimension.name()
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


        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);

        this.widthField.renderButton(matrices, mouseX, mouseY, delta);
        this.heightField.renderButton(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, this.textRenderer, Text.of("X:"), 7, this.height + 7 - 25 * 3, 16777215);
        drawCenteredText(matrices, this.textRenderer, Text.of("Y:"), 7, this.height + 7 - 25 * 2, 16777215);
        drawCenteredText(matrices, this.textRenderer, Text.of("Z:"), 7, this.height + 7 - 25 * 1, 16777215);

        this.x.renderButton(matrices, mouseX, mouseY, delta);
        this.y.renderButton(matrices, mouseX, mouseY, delta);
        this.z.renderButton(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
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
}
