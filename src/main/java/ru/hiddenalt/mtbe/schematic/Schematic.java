package ru.hiddenalt.mtbe.schematic;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTOutputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import ru.hiddenalt.mtbe.settings.SettingsManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class Schematic {
    protected SchematicBlock[][][] blocks;
    protected int width = 0;
    protected int height = 0;
    protected int length = 0;
    protected String tempName = "generatedSchematic.temp";

    public Schematic() {
        this.setSize(1, 1, 1);
    }

    public Schematic(int x, int y, int z) {
        this.setSize(x, y, z);
    }

    public String getTempFilename() {
        return this.tempName;
    }

    public void fillWith(SchematicBlock block) {
        for(int i = 0; i < this.width; ++i) {
            for(int j = 0; j < this.height; ++j) {
                for(int k = 0; k < this.length; ++k) {
                    block.setPos(i, j, k);
                    this.insertBlock(block);
                }
            }
        }

    }

    public void insertBlock(SchematicBlock block) {
        int x = block.x;
        int y = block.y;
        int z = block.z;
        if (x >= 0 && y >= 0 && z >= 0 && x < this.width && y < this.height && z < this.length) {
            this.blocks[x][y][z] = block;
        }

    }

    public void saveAsTemp() throws IOException {
        this.saveAsESchematic(this.getTempFilename());
    }

    public String associatedURL = "";

    public void saveAsESchematic(String filename) throws IOException {
        File file = new File(SettingsManager.getSchematicsDir() + filename);
        Map<String, Tag<?>> schematic = new HashMap();
        int width = this.width;
        int height = this.height;
        int length = this.length;
        byte[] blocks = new byte[width * height * length];
        byte[] data = new byte[width * height * length];

        String username = "unknown";
        try {
            username = MinecraftClient.getInstance().getSession().getUsername();
        } catch(Exception ignored){ }


        schematic.put("ESchematicVersion", new StringTag("ESchematicVersion", "1.0"));
        schematic.put("CompiledBy", new StringTag("CompiledBy", "MTBE for Minecraft"));
        schematic.put("Name", new StringTag("Name", filename));
        schematic.put("URL", new StringTag("URL", associatedURL));
        schematic.put("Author", new StringTag("Author", username));
        schematic.put("Created", new StringTag("CreatedAt", new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date())));

        schematic.put("Width", new ShortTag("Width", (short)width));
        schematic.put("Height", new ShortTag("Height", (short)height));
        schematic.put("Length", new ShortTag("Length", (short)length));

        // byte => string
        CompoundMap map = new CompoundMap();

        // string => byte
        HashMap<Identifier, Byte> registeredBlocks = new HashMap<>();

        // air as default (if missing)
        registeredBlocks.put(new Identifier("minecraft:air"), (byte)0);
        map.put("0", new StringTag("0", "minecraft:air"));

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                for(int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    blocks[index] = 0;
                    data[index] = 0;
                    SchematicBlock b = this.blocks[x][y][z];

                    if (b != null) {
                        byte id = (byte) map.size();

                        Identifier idKey = b.getIdentifier();
                        if(registeredBlocks.containsKey(idKey)){
                            id = registeredBlocks.get(idKey);
                        } else {
                            registeredBlocks.put(idKey, id);
                            map.put(""+id, new StringTag(""+id, idKey.toString()));
                        }

                        blocks[index] = id;
                    }
                }
            }
        }

        ArrayUtils.reverse(blocks);
        ArrayUtils.reverse(data);

        CompoundTag compound = new CompoundTag("Reference", map);
        schematic.put("Reference", compound);


        schematic.put("Materials", new StringTag("Materials", "Alpha"));
        schematic.put("Blocks", new ByteArrayTag("Blocks", blocks));
        schematic.put("Data", new ByteArrayTag("Data", data));

        CompoundTag schematicTag = new CompoundTag("", new CompoundMap(schematic));
        NBTOutputStream stream = new NBTOutputStream(new FileOutputStream(file));
        stream.writeTag(schematicTag);
        stream.close();
    }

    public void saveAs2DPNG(String filename) throws IOException {
        File file = new File(SettingsManager.getExportedPNGFolder() + filename);
        int w = this.getWidth();
        int h = this.getHeight();
        int l = this.getLength();
        MinecraftClient client = MinecraftClient.getInstance();


        int blockScale = 16;

        int imageWidth = w*blockScale;
        int imageHeight = h*blockScale;

        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // fill all the image with transparent color
        Color tc = new Color(0f,0f,0f,0f );
        g2d.setColor(tc);
        g2d.fillRect(0, 0, imageWidth, imageHeight);


        SchematicBlock[][][] blocks = this.getBlocks();


        HashMap<Identifier, BufferedImage> idTextureMap = new HashMap<>();

        for(int x = 0; x < w; ++x) {
            for(int y = 0; y < h; ++y) {
                int z = 0;
                SchematicBlock block = blocks[x][y][z];

                if(block.getIdentifier().equals(new Identifier("minecraft:air"))) continue;

                if (block != null) {

                    Identifier texture = new Identifier(block.getIdentifier().getNamespace() + ":textures/block/" + block.getIdentifier().getPath() + ".png");

                    BufferedImage imBuff = idTextureMap.get(texture);

                    // If not cached
                    if(imBuff == null) {
                        assert client != null;
                        InputStream stream = client.getResourceManager().getResource(texture).getInputStream();
                        imBuff = ImageIO.read(stream);
                        idTextureMap.put(texture, imBuff);
                    }

                    g2d.drawImage(imBuff, x*blockScale,y * blockScale,blockScale,blockScale, null);
                }
            }
        }

        g2d.dispose();


        ImageIO.write(bufferedImage, "png", file);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLength() {
        return this.length;
    }

    public void setSize(int width, int height, int length) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = new SchematicBlock[width][height][length];

        for(int i = 0; i < width; ++i) {
            for(int j = 0; j < height; ++j) {
                for(int k = 0; k < length; ++k) {
                    this.blocks[i][j][k] = new SchematicBlock(0, i, j, k);
                }
            }
        }

    }

    public SchematicBlock[][][] getBlocks() {
        return this.blocks;
    }
}
