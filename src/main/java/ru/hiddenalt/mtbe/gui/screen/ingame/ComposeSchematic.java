package ru.hiddenalt.mtbe.gui.screen.ingame;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.utils.IPlayerContext;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import ru.hiddenalt.mtbe.gui.screen.ErrorScreen;
import ru.hiddenalt.mtbe.schematic.BufferedImageToSchematic;
import ru.hiddenalt.mtbe.schematic.Schematic;
import ru.hiddenalt.mtbe.schematic.SchematicBlock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ComposeSchematic extends Screen {
    private final Screen parent;
    public int zoom = 1;
    private BufferedImage image;
    private Schematic schematic;
    private String url;
    private int imageWidth = 128;

    private int imageHeight = 128;

    public ComposeSchematic(MinecraftClient client, Screen parent, String url) {
        super(new TranslatableText("compose.title"));
        this.parent = parent;
        this.client = client;
        this.url = url;
    }

    public void downloadImage(){
        try {
            this.image = ImageIO.read(new URL(this.url).openStream());
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
            if(image == null) return;

            BufferedImageToSchematic generator = new BufferedImageToSchematic(image);

            if(imageWidth != -1 && imageHeight != -1)
                generator.setCustomImageSize(imageWidth, imageHeight);

            this.schematic = generator.generateSchematic();
        } catch (Exception e){
            assert this.client != null;
            this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
        }
    }

    protected void init(){

        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableText("composeSchematic.build.fabritone"), (buttonWidget) -> {

            try {
                this.schematic.saveAsTemp();

                IBaritone bar = BaritoneAPI.getProvider().getPrimaryBaritone();

                IPlayerContext player = bar.getPlayerContext();
                Vec3d vec = player.playerFeetAsVec();
                bar.getBuilderProcess().build(this.schematic.getTempFilename(), new BlockPos(vec.x,vec.y,vec.z));

                // TODO: full schematic support to build

                assert this.client != null;
                this.client.openScreen((Screen)null);
                this.client.mouse.lockCursor();
            } catch (IOException e) {
                assert this.client != null;
                this.client.openScreen(new ErrorScreen(this.parent, new TranslatableText("id.error.exception", e.getLocalizedMessage()).getString()));
            }

        }));

        downloadImage();
        generateSchematic();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);


        if(schematic == null) {
            drawCenteredText(matrices, this.textRenderer, new TranslatableText("compose.generating-schematic"), this.width / 2, this.height / 2, Color.yellow.getRGB());
        } else {

            SchematicBlock[][][] blocks = this.schematic.getBlocks();

            assert this.client != null;
            TextureManager manager = this.client.getTextureManager();

            int size = 2;

            int paddingLeft = Math.round((blocks.length * size) / 2);
            int paddingBottom = Math.round((blocks[0].length * size) / 2);


            for(int x = 0; x < this.schematic.getWidth(); x++){
                for(int y = 0; y < this.schematic.getHeight(); y++){
                    if(blocks[x][y][0] == null) continue;
                    SchematicBlock block = blocks[x][y][0];
                    if(block.getIdentifier().equals(new Identifier("minecraft:air"))) continue;



                    manager.bindTexture(new Identifier("minecraft:textures/block/" + block.getIdentifier().getPath() + ".png"));
                    drawTexture(matrices, this.width / 2 - paddingLeft + x * size, this.height / 2 - paddingBottom + y * size, size, size, 0.0F, 0.0F, 16, 128, 16, 128);
                }
            }
            SchematicBlock block = new SchematicBlock(1);
            Block blockInstance = block.getBlockInstance();

            assert this.client != null;
            Identifier id = this.client.getBakedModelManager().getBlockModels().getModel(blockInstance.getDefaultState()).getSprite().getId();

//            manager.bindTexture(new Identifier("minecraft:textures/block/acacia_door_top.png")); // EXAMPLE!!!!


        }

        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }



}
